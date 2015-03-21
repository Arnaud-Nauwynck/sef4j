package org.sef4j.elasticsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * helper class for asynchronous support + disruptor <BR/>
 * 
 * This class bufferize requests in memory, and periodic send BulkRequest.
 * On error, it will reschedule bulk requests, and possibly lost some!
 *
 * <PRE>
 *   Unitary       +-------------------------+    _
 *   Request --->  |   buffer (+ retryList)  |   /  \  Flush Timer
 *                 +-------------------------+    <-/  -------------> BULK Requests
 *                                 |                   _  
 *                                 <---------------   /  \ Flush Retry 
 *                                                     <-/  ----> LOST Requests !
 * </PRE>
 */
public class ElasticSearchBulkAsyncDisrupterHelper {

	private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchBulkAsyncDisrupterHelper.class);
	
	protected ElasticSearchClientFacade target;
	
	protected int flushPeriod;
	protected ScheduledExecutorService scheduledExecutor;
	
	protected int maxBulkRequests;
	protected int maxBulkLength;

	protected int maxRetryCount = 3;
	protected int maxBulkLengthNoRetry = 4*4192;

	
	protected Object lock = new Object();

	
	protected BulkRequest currentBulkRequest;
	protected int currentRetryIndex;
	
	protected Runnable flushRunnable = new Runnable() {
		public void run() {
			onAsyncFlush();
		}
	};

	protected long scheduledFutureTime;
	protected ScheduledFuture<?> scheduledFuture;
			
	protected int statTotalRequestsCount;
	protected int statTotalFlushesCount;
	
	
	// ------------------------------------------------------------------------
	
	private ElasticSearchBulkAsyncDisrupterHelper(ElasticSearchClientFacade target,
			Builder builder) {
		this.target = target;
		this.flushPeriod = builder.flushPeriod;
		this.scheduledExecutor = builder.scheduledExecutor;
		this.maxBulkRequests = builder.maxBulkRequests;
		this.maxBulkLength = builder.maxBulkLength;
		this.maxRetryCount = builder.maxRetryCount;
		this.maxBulkLengthNoRetry = builder.maxBulkLengthNoRetry;
	}

	public void start() {
		// do nothing!  (method exists for symmetry with stop())
	}

	public void stop() {
		try {
			flush();
		} catch(Exception ex) {
			LOG.error("Failed to flush pending bulk requests  ... ignore, no rethrow!", ex);
		}
		synchronized(lock) {
			if (scheduledFuture != null) {
				scheduledFuture.cancel(false);
				scheduledFuture = null;
				scheduledFutureTime = 0;
			}
		}
	}

	// ------------------------------------------------------------------------

	protected void flush() {
		BulkRequest bulkRequest = null;
		synchronized(lock) {
			if (currentBulkRequest != null) {
				bulkRequest = currentBulkRequest;
				this.currentBulkRequest = null;
			}
			this.scheduledFuture = null;
		}
		if (bulkRequest != null) {
			this.statTotalFlushesCount++;
			try {
				// *** do send BulkRequest ****
				target.bulk(bulkRequest);

				if (currentRetryIndex != 0) {
					// resume from error!
					LOG.info("Bulk requests sent, resume from previous error at retry " + (1+currentRetryIndex) + "/" + maxRetryCount);
					currentRetryIndex = 0;
				}
			} catch(RuntimeException ex) {
				onFlushFailMayRetry(ex, bulkRequest);
				throw ex;
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected void onFlushFailMayRetry(RuntimeException ex, BulkRequest failedBulk) {
		synchronized(lock) {
			currentRetryIndex++;
			String retryInfo = (1 + currentRetryIndex) + "/" + maxRetryCount;
			if (currentRetryIndex < maxRetryCount) {
				// re-put (or re-prepend) requests in current Bulk request to send! .. may also reschedule
				List<ActionRequest> tmpAllReqs = new ArrayList<ActionRequest>();
				tmpAllReqs.addAll(failedBulk.requests());
				if (currentBulkRequest != null) {
					tmpAllReqs.addAll(currentBulkRequest.requests());
					currentBulkRequest = null; // recreated with correct req order next!
				}
				int skipCount = 0;
				if (tmpAllReqs.size() > maxBulkRequests) {
					skipCount += maxBulkRequests - tmpAllReqs.size();
					tmpAllReqs = new ArrayList<ActionRequest>(tmpAllReqs.subList(skipCount, tmpAllReqs.size()));
				}
				// also check if estimated length overflow maxBulkLength ..skip more is needed
				BulkRequest tmpReversedReqsBulk = new BulkRequest();
				int retainFromIndex = tmpAllReqs.size()-1;
				for(int i = tmpAllReqs.size()-1; i >= 0; i--) {
					tmpReversedReqsBulk.add(tmpAllReqs.get(i));
					if (tmpReversedReqsBulk.estimatedSizeInBytes() > maxBulkLength) {
						// skip remaining requests in range [0...i] , retain from [i+1, len]
						break;
					}
					retainFromIndex = i;
				}
				if (retainFromIndex != 0) {
					skipCount += retainFromIndex;
					tmpAllReqs = new ArrayList<ActionRequest>(tmpAllReqs.subList(retainFromIndex, tmpAllReqs.size()));
				}
				
				if (skipCount != 0) {
					LOG.error("Failed bulk request, retry " + retryInfo
							+ ": skipping " + skipCount + " old request(s)" 
							+ " to satisfy maxBulkRequests=" + maxBulkRequests + ", maxBulkLength=" + maxBulkLength
							+ " .. reschedule, no rethrow", ex);
				} else {
					LOG.warn("Failed bulk request, retry " + retryInfo
							+ " .. reschedule, no rethrow, ex:" + ex.getMessage());
				}

				// now rebuild BulkRequest with remaining + reschedule
				if (tmpAllReqs.size() != 0) {
					this.currentBulkRequest = new BulkRequest();
					currentBulkRequest.add(tmpAllReqs);
					
					scheduleFlush(flushPeriod);
				}
			} else {
				// giving up ... current messages
				// keep incremented currentRetryIndex ?
				String msg = "Retry failed " + retryInfo
						+ ": skipping " + failedBulk.requests().size() + " bulk request(s) !";
				if (currentRetryIndex > maxRetryCount) {
					// do not print with exception stack trace again and again
					LOG.error(msg + " ex=" + ex.getMessage());
				} else {
					LOG.error(msg, ex);
				}
			}
		}
	}

	protected void onAsyncFlush() {
		try {
			flush();
		} catch(Exception ex) {
			LOG.error("Failed to flush bulk request (from async thread pool) ... ignore, no rethrow!", ex);
		}
	}
	
	public void asyncRequest(ActionRequest<?> req) {
		boolean needScheduleFlush = false;
		boolean needFlush = false;
		// boolean needFlushSync = false;
		synchronized(lock) {			
			if (currentBulkRequest == null) {
				currentBulkRequest = new BulkRequest();
				needScheduleFlush = true;
			}
			// append in memory : ultra fast .. but may flush / schedule hereafter
			currentBulkRequest.add(req);
			
			int currentBulkRequestsCount = currentBulkRequest.requests().size();
			if (currentBulkRequestsCount >= maxBulkRequests) {
				needFlush = true;
			}
		    long estimatedLength = currentBulkRequest.estimatedSizeInBytes();
		    if (estimatedLength >= maxBulkLength) {
		    	needFlush = true;
		    }
		    
		    if (needFlush) {
		    	scheduleFlush(0);
		    } else if (needScheduleFlush) {
		    	scheduleFlush(flushPeriod);
		    }
		}
		// alternative: flush immediate in current thread?, but outside of lock...
		// if (needFlushSync) {
		//	flush();
		// }
	}

	private void scheduleFlush(int delay) {
		synchronized(lock) {
			long nextTime = System.currentTimeMillis() + delay*1000;
			if (scheduledFuture != null && scheduledFutureTime > nextTime+200) { // 200 ms: precision for not cancel+reschedule!
				scheduledFuture.cancel(false);
				scheduledFuture = null;
			}
			if (scheduledFuture != null) {
				this.scheduledFutureTime = nextTime;
				this.scheduledFuture = scheduledExecutor.schedule(flushRunnable, delay, TimeUnit.SECONDS);
			}
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		String currentInfo = "";
		synchronized(lock) {			
			if (currentBulkRequest != null) {
				currentInfo = "currentBulkRequestsCount:" + currentBulkRequest.requests().size();
			}
		}
		return "ElasticSearchBulkAsyncHelper[flushPeriod=" + flushPeriod 
					+ ", maxBulkRequests:" + maxBulkRequests
					+ currentInfo
					+ "]";
	}
	
	
	// ------------------------------------------------------------------------
	
	public static class Builder {
		protected int flushPeriod = 30;
		protected ScheduledExecutorService scheduledExecutor;
		protected int maxBulkRequests = 50;
		protected int maxBulkLength = 4*4192;
		protected int maxRetryCount = 3;
		protected int maxBulkLengthNoRetry = 4*4192;

		private static ScheduledExecutorService defaultScheduledExecutor;

		public ElasticSearchBulkAsyncDisrupterHelper build(ElasticSearchClientFacade target) {
			if (scheduledExecutor == null) {
				defaultScheduledExecutor = Executors.newScheduledThreadPool(1);
				scheduledExecutor = defaultScheduledExecutor;
			}
			return new ElasticSearchBulkAsyncDisrupterHelper(target, this);
		}

		public Builder flushPeriod(int flushPeriod) {
			this.flushPeriod = flushPeriod;
			return this;
		}

		public Builder asyncScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
			this.scheduledExecutor = scheduledExecutor;
			return this;
		}

		public Builder maxBulkRequests(int maxBulkRequests) {
			this.maxBulkRequests = maxBulkRequests;
			return this;
		}

		public Builder maxBulkLength(int maxBulkLength) {
			this.maxBulkLength = maxBulkLength;
			return this;
		}
		
	}
	

	
}
