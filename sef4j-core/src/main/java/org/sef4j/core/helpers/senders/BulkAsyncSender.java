package org.sef4j.core.helpers.senders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.sef4j.core.api.EventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * asynchronous adapter for EventSender to EventSender<BR/>
 * 
 * This class bufferize Events in memory, and send bulk events,
 * either after a wait delay (example: 15 secondes), or after the bulk is "filled enough":
 * having reached maxEventsCount or having reached a cumulated of maxBulkByteLength
 *
 * <PRE>
 *   Unitary          +-------------------+    _
 *   sendEvent() ---> |   current buffer  |   /  \  Flush Delay
 *                    +-------------------+    <-/  -------------> 
 *                     /\           |    reached maxEventsCount      \
 *                     |            +---------------------------->   -->  BULK sendEvents()
 *                     |            |    reached maxBulkByteLength   /
 *                     |            +---------------------------->
 *                     |                                                             onError 
 *                     |                                                               |
 *                      <--------------------------------------------------------------           
 *                                    asyncDisruptorErrorHandler
 *                                    decide to skip/retry some events
 *
 * </PRE>
 */
public class BulkAsyncSender<T> implements EventSender<T> {

	private static final Logger LOG = LoggerFactory.getLogger(BulkAsyncSender.class);
	private static final boolean DEBUG = true;
	
	protected EventSender<T> targetOutput;
	
	protected int flushPeriod;
	protected ScheduledExecutorService flushScheduledExecutor;
	protected boolean flushFilledBulkInCurrentThread;
	
	protected int maxBulkEventsCount;
	
	protected Function<T,Integer> eventByteLengthProvider;
	protected int maxBulkByteLength;

	
	protected Object lock = new Object();

	
	protected List<T> bufferedEvents = new ArrayList<T>();
	protected int bufferedeBulkByteLength;

	// this "temporary" queue is used to send bulk events asynchronously using another thread
	protected Queue<List<T>> asyncEventsBulksQueue = new ConcurrentLinkedQueue<List<T>>();

	protected Object asyncSenderLock = new Object();
	
	
	protected Runnable asyncFlushDelayTask = new Runnable() {
		public void run() {
			onAsyncFlushDelay();
		}
	};
	protected Runnable asyncFlushImmediateTask = new Runnable() {
		public void run() {
			onAsyncFlushImmediate();
		}
	};

	protected long scheduledFutureTime;
	protected ScheduledFuture<?> scheduledFuture;
			
	protected int statTotalEventsCount;
	protected int statTotalEventBulksCount;
	
	
	public static interface IAsyncDisruptorErrorHandler<T> {
	    
	    public void onSendEventsOK(List<T> events);

	    /** @return next delay period to reschedule */
	    public int onSendEventsFailed(List<T> events, RuntimeException ex, 
	                List<List<T>> retryPrependBulkEvents,
	                List<List<T>> retryAppendBulkEvents);

	    public void onQueued(Queue<List<T>> queue, List<T> events);

	}
	
	protected IAsyncDisruptorErrorHandler<T> asyncDisruptorErrorHandler;
	
	// ------------------------------------------------------------------------
	
	private BulkAsyncSender(EventSender<T> target,
			Builder<T> builder) {
		this.targetOutput = target;
		this.flushPeriod = builder.flushPeriod;
		this.flushScheduledExecutor = builder.scheduledExecutor;
		this.maxBulkEventsCount = builder.maxBulkEventsCount;
		this.eventByteLengthProvider = builder.eventByteLengthProvider;
		this.maxBulkByteLength = builder.maxBulkByteLength;
		this.flushFilledBulkInCurrentThread = builder.flushFilledBulkInCurrentThread;
		this.asyncDisruptorErrorHandler = builder.asyncDisruptorErrorHandler;
	}

	/*pp for test*/
	void setFlushFilledBulkInCurrentThread(boolean p) {
		this.flushFilledBulkInCurrentThread = p;
	}
	
	// implements EventSender  (input for unitary events)
	// ------------------------------------------------------------------------
	
	public void sendEvent(T req) {
		sendEvents(Collections.singleton(req));
	}
	
	public void sendEvents(Collection<T> events) {
		boolean needScheduleFlush = false;
		boolean needFlush = false;
		synchronized(lock) {			
			if (bufferedEvents  == null) {
				bufferedEvents = new ArrayList<T>(maxBulkEventsCount);
				needScheduleFlush = true;
			}
			// append in memory buffer: ultra fast .. but may flush / schedule hereafter
			// group events by bulk with max EventCount/BulkByteLength
			for(T event : events) {
				int eventByteLen = eventByteLengthProvider != null? eventByteLengthProvider.apply(event) : 0;
				if ( (bufferedEvents.size() + 1 > maxBulkEventsCount)
						|| (eventByteLen + bufferedeBulkByteLength > maxBulkByteLength)) {
					// start a new bulk
					if (DEBUG) {
						if (bufferedEvents.size() + 1 > maxBulkEventsCount) {
							LOG.info(System.currentTimeMillis() + " sendEvent .. reached maxBulkEventsCount " + maxBulkEventsCount 
									+ " .. need flush");
						} else {
							LOG.info(System.currentTimeMillis() + " sendEvent .. reached maxBulkByteLength:" + bufferedeBulkByteLength 
									+ " + " + eventByteLen + " .. need flush");
						}
					}
					this.asyncEventsBulksQueue.add(bufferedEvents);
					if (asyncDisruptorErrorHandler != null) {
					    asyncDisruptorErrorHandler.onQueued(asyncEventsBulksQueue, bufferedEvents);
					}
					
					this.bufferedEvents = new ArrayList<T>(maxBulkEventsCount);
					this.bufferedeBulkByteLength = 0;
					needFlush = ! asyncEventsBulksQueue.isEmpty(); // mostly true  ... cf disruptorErrorHandler to purge queue
				}
				this.bufferedEvents.add(event);
				bufferedeBulkByteLength += eventByteLen;
			}
		    
		    if (needFlush) {
		    	if (! flushFilledBulkInCurrentThread) {
		    		// do not flush in current thread (and inside lock) => schedule immediate in separate thread 
		    		scheduleFlush(0);
		    	} else {
		    		// cf next: flush outside of lock
		    	}
		    } else if (needScheduleFlush) {
		    	scheduleFlush(flushPeriod);
		    }
		}// synchronized(lock)
	    if (needFlush) {
	    	if (flushFilledBulkInCurrentThread) {
	    		doFlush(false, false);
	    	}
	    }
	}
	
	// ------------------------------------------------------------------------
	
	public void start() {
		synchronized(lock) {
			if (! asyncEventsBulksQueue.isEmpty()) {
				scheduleFlush(0);
			}
		}
	}

	public void stop() {
		try {
			doFlush(true, true);
		} catch(Exception ex) {
			LOG.error("Failed to flush pending bulk Events  ... ignore, no rethrow!", ex);
		}
		synchronized(lock) {
			if (scheduledFuture != null) {
				scheduledFuture.cancel(false);
				scheduledFuture = null;
				scheduledFutureTime = 0;
			}
		}
	}

	public void flush() {
		flush(true, true);
	}
	
	public void flush(boolean forceSendAll, boolean sendPartiallyFilledBulk) {
		if (DEBUG) LOG.info(System.currentTimeMillis() + " flush " + flushParamToString(forceSendAll, sendPartiallyFilledBulk) + " " + infoToString());
		doFlush(forceSendAll, sendPartiallyFilledBulk);
	}

	protected static String flushParamToString(boolean forceSendAll, boolean sendPartiallyFilledBulk) {
		return (forceSendAll? "all" : sendPartiallyFilledBulk? "partial" : "filledBulksOnly");
	}
	
	public void waitAsyncEventBulksQueueFlushed(int repeat, int sleep) {
		for(int i = 0; i < repeat; i++) {
			int asyncBulkQueueSize = getCurrentAsyncEventsBulksQueueSize();
			if (asyncBulkQueueSize != 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public int getCurrentBufferedEventsSize() {
		synchronized(lock) {
			return bufferedEvents != null? bufferedEvents.size() : 0;
		}
	}
	
	public int getCurrentAsyncEventsBulksQueueSize() {
		synchronized(lock) {
			return asyncEventsBulksQueue.size();
		}
	}
	
	public int getFlushPeriod() {
	    return flushPeriod;
	}
	
	
	// ------------------------------------------------------------------------

    protected void scheduleFlush(int delay) {
		synchronized(lock) {
			if (DEBUG) LOG.info(System.currentTimeMillis() + " scheduleFlush " + delay);
			long nextTime = System.currentTimeMillis() + delay*1000;
			if (scheduledFuture != null && scheduledFutureTime > nextTime+400) { // 400 ms: precision for not cancel+reschedule!
				doCancelFutureSchedule();
			}
			if (scheduledFuture == null) {
				doScheduleNextTime(delay, nextTime);
			}
		}
	}
	
	private void doCancelFutureSchedule() {
        if (DEBUG) LOG.info(System.currentTimeMillis() + " cancel schedule " + scheduledFutureTime);
	    scheduledFuture.cancel(false);
	    scheduledFuture = null;
	    scheduledFutureTime = 0;
	}

    private void doScheduleNextTime(int delay, long nextTime) {
        if (delay != 0) {
        	this.scheduledFutureTime = nextTime;
//					if (DEBUG) LOG.info(System.currentTimeMillis() + " schedule");
        	this.scheduledFuture = flushScheduledExecutor.schedule(
        			asyncFlushDelayTask, delay, TimeUnit.SECONDS);
        } else {
//					if (DEBUG) LOG.info(System.currentTimeMillis() + " submit");
        	flushScheduledExecutor.submit(asyncFlushImmediateTask);
        	scheduledFutureTime = 0;
        }
    }

	protected void forceReschedule(int newDelay) {
	    // assert thread owns lock
	    if (DEBUG) LOG.info(System.currentTimeMillis() + " force reschedule " + newDelay + " + cancel old " + scheduledFutureTime);
        if (scheduledFuture != null) {
            doCancelFutureSchedule();
        }
        long nextTime = System.currentTimeMillis() + newDelay*1000;
        doScheduleNextTime(newDelay, nextTime);
    }

    
	private void onAsyncFlushDelay() {
		synchronized(lock) {
			if (DEBUG) LOG.info(System.currentTimeMillis() + " onAsyncFlushDelay " + infoToString());
			doAsyncFlush(false, true);
		}
	}

	private void onAsyncFlushImmediate() {
		synchronized(lock) {
			if (DEBUG) LOG.info(System.currentTimeMillis() + " onAsyncFlushImmediate " + infoToString());
			doAsyncFlush(false, false);
		}
	}

	private void doAsyncFlush(boolean forceSendAll, boolean sendPartiallyFilledBulk) {
		scheduledFuture = null;
		scheduledFutureTime = 0;
		try {
			doFlush(forceSendAll, sendPartiallyFilledBulk);
		} catch(Exception ex) {
			LOG.error("Should not occur ... Failed to flush bulk event (from async thread pool) ... ignore, no rethrow!", ex);
		}
		if (bufferedEvents != null) {
			scheduleFlush(this.flushPeriod);
		}
	}

	protected void doFlush(boolean forceSendAll, boolean sendPartiallyFilledBulk) {
		synchronized(asyncSenderLock) { // lock sender, to respect events orders
			if (DEBUG) LOG.info(System.currentTimeMillis() + " doFlush " + flushParamToString(forceSendAll, sendPartiallyFilledBulk) + " " + infoToString());
			List<List<T>> bulksToSend = null;
			// clear and get queue 
			synchronized(lock) {
				if (! asyncEventsBulksQueue.isEmpty()) {
					bulksToSend = new ArrayList<List<T>>(); //asyncEventsBulksQueue.size()
					while(! asyncEventsBulksQueue.isEmpty()) {
						bulksToSend.add(asyncEventsBulksQueue.poll());
					};
				}
				// add uncompleted bufferedEvents if any
				if (bufferedEvents != null) {
					if (forceSendAll || (bulksToSend == null && sendPartiallyFilledBulk)) {
						if (bulksToSend == null) bulksToSend = new ArrayList<List<T>>(1);
						// TODO ... should check for maxBulkEventsCount  / maxBulkByteLength !!
						bulksToSend.add(bufferedEvents);
						
						this.bufferedEvents = null;
						this.bufferedeBulkByteLength = 0;
					} else {
						// needScheduleMore...
					}
				} else {
					// needScheduleMore = true;
				}
			} // synchronized(lock)
			
			if (bulksToSend != null) {
				// *** do send Bulk events ***
				// outside of "lock", but inside of "asyncSenderLock"
				// TODO... may reuse jdk class for Queue + ExecutorService 
				// (with single thread executor and/or garantee of order)?
				this.statTotalEventBulksCount += bulksToSend.size();
				for(List<T> bulkEvents : bulksToSend) {
					if (DEBUG) LOG.info(System.currentTimeMillis() + " => send bulk: " + bulkEvents.size() + " event(s)");
					try {
					    // **** The biggy ****
						targetOutput.sendEvents(bulkEvents);
						
						onSendEventsOK(bulkEvents);
					} catch(RuntimeException ex) {
					    onSendEventsFailed(bulkEvents, ex);
					}
				}
			}
	
		}// synchronized(asyncSenderLock)
	}

	protected void onSendEventsOK(List<T> bulkEvents) {
        if (asyncDisruptorErrorHandler != null) {
            asyncDisruptorErrorHandler.onSendEventsOK(bulkEvents);
        }
	}
	
	protected void onSendEventsFailed(List<T> bulkEvents, RuntimeException ex) {
	    if (asyncDisruptorErrorHandler != null) {
	        List<List<T>> retryPrependBulks = new ArrayList<List<T>>();
            List<List<T>> retryAppendBulks = new ArrayList<List<T>>();
	        
	        int rescheduleDelay = asyncDisruptorErrorHandler.onSendEventsFailed(bulkEvents, ex, retryPrependBulks, retryAppendBulks);
	        if (! retryPrependBulks.isEmpty() || ! retryAppendBulks.isEmpty()) {
	            int countRetryPrepend = 0;
	            for(List<T> re : retryPrependBulks) countRetryPrepend += re.size();  
                int countRetryAppend = 0;
                for(List<T> re : retryAppendBulks) countRetryAppend += re.size();
                int countReInsert = countRetryPrepend + countRetryAppend; 
                LOG.warn("Failed to send bulk events ... disruptorErrorHandler => re-insert " + countReInsert + " event(s)"
                        + ((countRetryAppend != 0)? " ( " + countRetryAppend + " reordered at end)" : "")
                        + ", ex:" + ex.getMessage());
	            onFailedReInsertBulkEventsToSend(rescheduleDelay, retryPrependBulks, retryAppendBulks);
	        } else {
	            LOG.warn("Failed to send bulk events ... disruptorErrorHandler => skip all, ex:" + ex.getMessage());
	        }
	    } else {
	        LOG.error("Failed to send bulk events => skip all event(s) ... ignore, continue?!", ex);
	    }
	}

	protected void onFailedReInsertBulkEventsToSend(int delayReschedule, List<List<T>> retryPrependBulks, List<List<T>> retryAppendBulks) {
	    // similar to sendEvents() ... but events are prepended/appended to asyncEventsBulksQueue instead of appended to list bufferedEvents 
	    synchronized(lock) {
            // re-insert at begining => remove all + re-add !!
            // asyncEventsBulksQueue.add(0, bufferedEvents); 
            List<List<T>> reinsertBulks = new ArrayList<List<T>>(); 
            reinsertBulks.addAll(retryPrependBulks);
            while(! asyncEventsBulksQueue.isEmpty()) {
                reinsertBulks.add(asyncEventsBulksQueue.poll());
            };
            reinsertBulks.addAll(retryAppendBulks);
            asyncEventsBulksQueue.addAll(reinsertBulks);
            
            forceReschedule(delayReschedule);
        }// synchronized(lock)
	}
	
	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "BulkAsyncSender[" 
			+ "flushPeriod=" + flushPeriod 
			+ infoToString()
			+ "]";
	}

	public String infoToString() {
		String currentInfo = "";
		synchronized(lock) {			
			if (! asyncEventsBulksQueue.isEmpty()) {
				currentInfo += ", current queued bulk count:" + asyncEventsBulksQueue.size();
			}
			if (bufferedEvents != null) {
				currentInfo += ", current buffered events count:" + bufferedEvents.size();
			}
			if (scheduledFuture != null) {
				long nextFlushDelay = System.currentTimeMillis() - scheduledFutureTime;
				currentInfo += ", next flush in " + nextFlushDelay + " ms";
			}
		}
		return currentInfo;
	}
	
	
	// ------------------------------------------------------------------------
	
	public static class Builder<T> {
		protected int flushPeriod = 30;
		protected ScheduledExecutorService scheduledExecutor;
		protected int maxBulkEventsCount = 50;
		protected Function<T,Integer> eventByteLengthProvider;
		protected int maxBulkByteLength = 4*4192;
		protected boolean flushFilledBulkInCurrentThread;
		protected IAsyncDisruptorErrorHandler<T> asyncDisruptorErrorHandler;
		
		private static ScheduledExecutorService defaultScheduledExecutor;

		public BulkAsyncSender<T> build(EventSender<T> target) {
			if (scheduledExecutor == null) {
				defaultScheduledExecutor = Executors.newScheduledThreadPool(1);
				scheduledExecutor = defaultScheduledExecutor;
			}
			return new BulkAsyncSender<T>(target, this);
		}

		public Builder<T> flushPeriod(int p) {
			this.flushPeriod = p;
			return this;
		}

		public Builder<T> asyncScheduledExecutor(ScheduledExecutorService p) {
			this.scheduledExecutor = p;
			return this;
		}

		public Builder<T> maxBulkEventsCount(int p) {
			this.maxBulkEventsCount = p;
			return this;
		}

		public Builder<T> eventByteLengthProvider(Function<T,Integer> p) {
			this.eventByteLengthProvider = p;
			return this;
		}

		public Builder<T> maxBulkByteLength(int p) {
			this.maxBulkByteLength = p;
			return this;
		}
		
		public Builder<T> flushFilledBulkInCurrentThread(boolean p) {
			this.flushFilledBulkInCurrentThread = p;
			return this;
		}
		
		public Builder<T> asyncDisruptorErrorHandler(IAsyncDisruptorErrorHandler<T> p) {
		    this.asyncDisruptorErrorHandler = p;
		    return this;
		}

        public IAsyncDisruptorErrorHandler<T> getAsyncDisruptorErrorHandler() {
            return asyncDisruptorErrorHandler;
        }
		
	}

}
