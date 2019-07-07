package org.sef4j.core.appenders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sef4j.api.EventAppender;

/**
 * in-memory EventSender : append events in list, this class is thread-safe, and
 * offer operation to atomically "clearAndGet" event list
 * 
 */
public class InMemoryEventAppender<T> implements EventAppender<T> {

    private Object lock = new Object();
    private List<T> events = new ArrayList<T>();

    // ------------------------------------------------------------------------

    public InMemoryEventAppender() {
    }

    // ------------------------------------------------------------------------

    @Override
    public void sendEvent(T event) {
	synchronized (lock) {
	    events.add(event);
	}
    }

    public void sendEvents(Collection<T> events) {
	synchronized (lock) {
	    this.events.addAll(events);
	}
    }

    public List<T> clearAndGet() {
	List<T> res;
	synchronized (lock) {
	    res = events;
	    this.events = new ArrayList<T>();
	}
	return res;
    }

}
