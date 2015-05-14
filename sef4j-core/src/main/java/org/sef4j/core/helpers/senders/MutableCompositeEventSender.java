package org.sef4j.core.helpers.senders;

import java.util.Collection;

import org.sef4j.core.api.EventSender;
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
 * @param <T>
 */

public class MutableCompositeEventSender<T> implements EventSender<T> {

	private static final Logger LOG = LoggerFactory.getLogger(MutableCompositeEventSender.class);
	
	private final EventSender<T>[] targets;
	
	// ------------------------------------------------------------------------

	public MutableCompositeEventSender(EventSender<T>[] targets) {
		this.targets = targets;
	}

	// ------------------------------------------------------------------------
	
	public void sendEvent(T event) {
		final EventSender<T>[] to = targets; 
		final int len = to.length;
		for (int i = 0; i < len; i++) {
			try {
				to[i].sendEvent(event);
			} catch(Exception ex) {
				LOG.error("Failed to sendEvent to " + to[i] 
						+ ", ex:" + ex.getMessage() + " ... ignore, no rethrow!");
			}
		}
	}

	public void sendEvents(Collection<T> events) {
		final EventSender<T>[] to = targets; 
		final int len = to.length;
		for (int i = 0; i < len; i++) {
			try {
				to[i].sendEvents(events);
			} catch(Exception ex) {
				LOG.error("Failed to sendEvents to " + to[i] 
						+ ", ex:" + ex.getMessage() + " ... ignore, no rethrow!");
			}
		}
	}

}
