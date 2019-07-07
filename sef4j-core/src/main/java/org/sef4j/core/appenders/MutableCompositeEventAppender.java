package org.sef4j.core.appenders;

import java.util.Collection;

import org.sef4j.api.EventAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * immutable composite of EventSender<T> to send events to several EventSender
 * this class is immutable (so also thread-safe!)
 * 
 * 
 * <PRE>
 *    new CompositeEventSender(targets)
 *    
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

public class MutableCompositeEventAppender<T> implements EventAppender<T> {

    private static final Logger LOG = LoggerFactory.getLogger(MutableCompositeEventAppender.class);

    private final EventAppender<T>[] targets;

    // ------------------------------------------------------------------------

    public MutableCompositeEventAppender(EventAppender<T>[] targets) {
	this.targets = targets;
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

}
