package org.sef4j.core.appenders;

import java.util.Collection;

import org.sef4j.api.EventAppender;
import org.sef4j.api.EventSource;
import org.sef4j.core.utils.CopyOnWriteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * composite of EventSender<T> to add/remove several listener EventSender<T>
 * this class is mutable, and thread-safe
 * 
 * It may be confusing to have both role EventSender and EventProvider ... see
 * other more restrictive classes
 * 
 * 
 * <PRE>
 *       addEventListener() 
 *           -------->              
 *       removeEventListener() 
 *           -------->              
 *                       +------------+
 *    sendEvent(..)      |            |   target[0].sendEvent(..)
 *         ---------->   |  targets[] |   ---------->   
 *                       +------------+     target[1].sendEvent(..)
 *                                          ----------> 
 *                                           ...  
 *                                               target[N-1].sendEvent(..)
 *                                               ---------->
 * </PRE>
 * 
 * @param <T>
 */
public class CompositeEventAppender<T> implements EventAppender<T>, EventSource<T> {

    private static final Logger LOG = LoggerFactory.getLogger(CompositeEventAppender.class);
    private static final EventAppender<?>[] EMPTY_ARRAY = new EventAppender<?>[0];

    /** copy-on-write */
    @SuppressWarnings("unchecked")
    private EventAppender<T>[] targets = (EventAppender<T>[]) EMPTY_ARRAY;

    private Object lock = new Object();

    // ------------------------------------------------------------------------

    public CompositeEventAppender() {
    }

    // ------------------------------------------------------------------------

    public void sendEvent(T event) {
	final EventAppender<T>[] to = targets;
	final int len = to.length;
	for (int i = 0; i < len; i++) {
	    try {
		to[i].sendEvent(event);
	    } catch (Exception ex) {
		LOG.error("Failed to sendEvent to " + to[i] + ", ex:" + ex.getMessage() + " ... ignore, no rethrow!");
	    }
	}
    }

    public void sendEvents(Collection<T> events) {
	final EventAppender<T>[] to = targets;
	final int len = to.length;
	for (int i = 0; i < len; i++) {
	    try {
		to[i].sendEvents(events);
	    } catch (Exception ex) {
		LOG.error("Failed to sendEvents to " + to[i] + ", ex:" + ex.getMessage() + " ... ignore, no rethrow!");
	    }
	}
    }

    @SuppressWarnings("unchecked")
    public void addEventListener(EventAppender<T> listener) {
	if (listener == null)
	    throw new IllegalArgumentException();
	synchronized (lock) {
	    this.targets = CopyOnWriteUtils.newWithAdd(EventAppender.class, targets, listener);
	}
    }

    @SuppressWarnings("unchecked")
    public void removeEventListener(EventAppender<T> listener) {
	if (listener == null)
	    throw new IllegalArgumentException();
	synchronized (lock) {
	    this.targets = CopyOnWriteUtils.newWithRemove(EventAppender.class, targets, listener);
	}
    }

    public int getEventListenerCount() {
	return targets.length;
    }

}
