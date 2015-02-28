package org.sef4j.callstack.event.impl;

import java.util.ArrayList;
import java.util.List;

import org.sef4j.callstack.event.StackEvent;
import org.sef4j.callstack.event.StackEventListener;

/**
 * listener of StackEvent, that append events in in-memory list.
 * 
 * This class is thread-safe, and offer atomic clearAndGet() operation
 * (cf also InMemoryEventAppender for untyped event Object)
 */
public class InMemoryStackEventListener extends StackEventListener {

	private Object lock = new Object();
	private List<StackEvent> events = new ArrayList<StackEvent>();

	// ------------------------------------------------------------------------

	public InMemoryStackEventListener() {
	}

	// ------------------------------------------------------------------------

	@Override
	public void onEvent(StackEvent event) {
		synchronized(lock) {
			events.add(event);
		}
	}

	public List<StackEvent> clearAndGet() {
		List<StackEvent> res;
		synchronized(lock) {
			res = events;
			this.events = new ArrayList<StackEvent>();
		}
		return res;
	}
	
}
