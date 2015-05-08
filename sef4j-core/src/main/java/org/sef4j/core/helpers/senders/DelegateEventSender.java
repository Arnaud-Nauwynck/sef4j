package org.sef4j.core.helpers.senders;

import java.util.Collection;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.SingleListenerEventProvider;

/**
 * delegate (proxy) of EventSender<T>
 * 
 * <PRE>
 *       setTarget() 
 *           -------->              
 *                       +------------+
 *    sendEvent(..)      |            |   target.sendEvent(..)
 *         ---------->   |  target    |   ---------->   
 *                       +------------+
 * </PRE>
 * @param <T>
 */
public class DelegateEventSender<T> implements EventSender<T>, SingleListenerEventProvider<T> {

	private EventSender<T> eventListener;
	
	// ------------------------------------------------------------------------

	public DelegateEventSender(EventSender<T> target) {
		this.eventListener = target;
	}

	// ------------------------------------------------------------------------
	
	public void sendEvent(T event) {
		EventSender<T>  t = eventListener;
		if (t != null) {
			t.sendEvent(event);
		}
	}

	public void sendEvents(Collection<T> events) {
		EventSender<T> t = eventListener;
		if (t != null) {
			t.sendEvents(events);
		}
	}

	public EventSender<T> getEventListener() {
		return eventListener;
	}

	public void setEventListener(EventSender<T> target) {
		this.eventListener = target;
	}

	public void clearEventListener() {
		this.eventListener = null;
	}
	
	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "DelegateEventSender [" + eventListener + "]";
	}
	
}
