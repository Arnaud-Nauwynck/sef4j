package org.sef4j.core.appenders;

import java.util.List;
import java.util.Queue;

import org.sef4j.core.appenders.BulkAsyncAppender.IAsyncDisruptorErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * default implementation of IAsyncDisruptorErrorHandler<T>, to be used within
 * BulkAsyncSender<T>
 */
public class DefaultAsyncDisrupterErrorHandler<T> implements IAsyncDisruptorErrorHandler<T> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAsyncDisrupterErrorHandler.class);

    protected int currentRetryIndex = 0;
    protected int maxRetryCount = 3;

    protected int maxQueueBulksCount = 100;

    protected int delayOnError = 60; // 1 minute

    // ------------------------------------------------------------------------

    public DefaultAsyncDisrupterErrorHandler() {
    }

    // ------------------------------------------------------------------------

    @Override
    public void onSendEventsOK(List<T> events) {
	this.currentRetryIndex = 0;
    }

    @Override
    public int onSendEventsFailed(List<T> events, RuntimeException ex, List<List<T>> retryPrependBulkEvents,
	    List<List<T>> retryAppendBulkEvents) {
	currentRetryIndex++;
	String retryInfo = (1 + currentRetryIndex) + "/" + maxRetryCount;
	if (currentRetryIndex < maxRetryCount) {
	    // re-put (or re-prepend) requests in current Bulk request to send!
	    // TODO ... retryable/not-retryable => should analyse exception...

	    int skipCount = 0;
	    // TODO skip somes events?

	    retryPrependBulkEvents.add(events);

	    if (skipCount != 0) {
		LOG.error("Failed bulk request, retry " + retryInfo + ": skipping " + skipCount + " old request(s)"
			+ " .. reschedule, no rethrow", ex);
	    } else {
		LOG.warn("Failed bulk request, retry " + retryInfo + " .. reschedule, no rethrow, ex:"
			+ ex.getMessage());
	    }
	} else {
	    // giving up ... current messages
	    // keep incremented currentRetryIndex ?
	    String msg = "Retry failed " + retryInfo + ": skipping " + events.size() + " bulk request(s) !";
	    if (currentRetryIndex > maxRetryCount) {
		// do not print with exception stack trace again and again
		LOG.error(msg + " ex=" + ex.getMessage());
	    } else {
		LOG.error(msg, ex);
	    }
	}

	return delayOnError; // + bulkAsyncSender.getFlushPeriod();
    }

    @Override
    public void onQueued(Queue<List<T>> queue, List<T> events) {
	while (queue.size() > maxQueueBulksCount) { // avoid out of memory, at least! (retain max= 100 bulks of ~20
						    // requests)
	    queue.poll();
	}
    }

}