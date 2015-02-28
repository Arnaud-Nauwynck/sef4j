package org.sef4j.core.appenders;

import java.util.ArrayList;
import java.util.List;

import org.sef4j.core.api.EventAppender;

public class InMemoryEventAppender extends EventAppender {

	private Object lock = new Object();
	private List<Object> events = new ArrayList<Object>();
	
	// ------------------------------------------------------------------------
	
	public InMemoryEventAppender(String appenderName) {
		super(appenderName);
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public void handleEvent(Object event) {
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
