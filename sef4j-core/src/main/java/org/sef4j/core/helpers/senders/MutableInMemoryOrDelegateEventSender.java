package org.sef4j.core.helpers.senders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sef4j.core.api.EventSender;

/**
 * buffer events in memory while no delegate are present, otherwise redispatch to delegate
 * 
 * @param <T>
 */
public class MutableInMemoryOrDelegateEventSender<T> implements EventSender<T> {

	private EventSender<T> target;

	private List<T> events = new ArrayList<T>();

	private Object lock = new Object();

	private int maxEvents;
	
	// ------------------------------------------------------------------------

	public MutableInMemoryOrDelegateEventSender(EventSender<T> target, int maxEvents) {
		this.target = target;
		this.maxEvents = maxEvents;
	}

	// ------------------------------------------------------------------------

	public EventSender<T> getDelegate() {
		synchronized(lock) {
			return target;
		}
	}
	
	public void setDelegate(EventSender<T> target) {
		List<T> eventsToSend = null;
		synchronized(lock) {
			this.target = target;
			if (target != null && ! events.isEmpty()) {
				eventsToSend = events;
				events = new ArrayList<T>();
			}
		}
		if (eventsToSend != null) {
			target.sendEvents(eventsToSend);
		}
	}
	
	public void sendEvent(T event) {
		EventSender<T> to;
		synchronized (lock) {
			to = target;
		}
		if (to != null) {
			to.sendEvent(event);
		} else {
			synchronized (lock) {
				events.add(event);
				onCheckMaxEventsSize();
			}
		}
	}

	public void sendEvents(Collection<T> event) {
		EventSender<T> to;
		synchronized (lock) {
			to = target;
		}
		if (to != null) {
			to.sendEvents(events);
		} else {
			synchronized (lock) {
				events.addAll(events);
				onCheckMaxEventsSize();
			}
		}
	}

	protected void onCheckMaxEventsSize() {
		if (maxEvents > 0 && events.size() > maxEvents) {
			events = new ArrayList<T>(maxEvents);
			// TODO ... insert special event for "truncation event" 
			events.addAll(events.subList(events.size()-maxEvents, events.size()));
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		EventSender<T> to;
		int eventsCount;
		synchronized (lock) {
			to = target;
			eventsCount = events.size();
		}
		return "MutableInMemoryOrDelegateEventSender["
				+ ((to != null)? to : "")
				+ ((eventsCount != 0)? eventsCount + " buffered event(s)" : "")
				+"]";
	}

	
	
}
