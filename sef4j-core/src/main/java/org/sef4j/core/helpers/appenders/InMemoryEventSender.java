package org.sef4j.core.helpers.appenders;

import java.util.ArrayList;
import java.util.List;

import org.sef4j.core.api.EventSender;

/**
 * in-memory EventSender : append events in list, 
 * this class is thread-safe, and offer operation to atomically "clearAndGet" event list
 * 
 */
public class InMemoryEventSender implements EventSender {

	private Object lock = new Object();
	private List<Object> events = new ArrayList<Object>();
	
	// ------------------------------------------------------------------------
	
	public InMemoryEventSender() {
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public void sendEvent(Object event) {
		synchronized(lock) {
			events.add(event);
		}
	}

	public List<Object> clearAndGet() {
		List<Object> res;
		synchronized(lock) {
			res = events;
			this.events = new ArrayList<Object>();
		}
		return res;
	}
	
}
