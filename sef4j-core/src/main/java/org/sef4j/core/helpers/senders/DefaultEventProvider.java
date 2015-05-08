package org.sef4j.core.helpers.senders;

import java.util.Collection;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.EventProvider;
import org.sef4j.core.util.CopyOnWriteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * composite of EventSender<T> to add/remove several listener EventSender<T>
 * this class is mutable, and thread-safe
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
 * @param <T>
 */
public class DefaultEventProvider<T> implements EventSender<T>, EventProvider<T> {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultEventProvider.class);
	private static final EventSender<?>[] EMPTY_ARRAY = new EventSender<?>[0];
			
	/** copy-on-write */
	@SuppressWarnings("unchecked")
	private EventSender<T>[] targets = (EventSender<T>[]) EMPTY_ARRAY;
	
	private Object lock = new Object();
	
	// ------------------------------------------------------------------------

	public DefaultEventProvider() {
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

	@SuppressWarnings("unchecked")
	public void addEventListener(EventSender<T> listener) {
		if (listener == null) throw new IllegalArgumentException();
		synchronized(lock) {
			this.targets = CopyOnWriteUtils.newWithAdd(EventSender.class, targets, listener);
		}
	}

	@SuppressWarnings("unchecked")
	public void removeEventListener(EventSender<T> listener) {
		if (listener == null) throw new IllegalArgumentException();
		synchronized(lock) {
			this.targets = CopyOnWriteUtils.newWithRemove(EventSender.class, targets, listener);
		}
	}

}
