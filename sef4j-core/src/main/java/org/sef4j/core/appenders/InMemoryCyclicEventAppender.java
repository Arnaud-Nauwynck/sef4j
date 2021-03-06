package org.sef4j.core.appenders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sef4j.api.EventAppender;

/**
 * in-memory cyclic buffer EventSender : append events in cyclic array this
 * class is thread-safe, and offer operation to atomically "clearAndGet" event
 * list
 * 
 */
public class InMemoryCyclicEventAppender<T> implements EventAppender<T> {

    public static final int DEFAUT_LEN = 50;

    private Object lock = new Object();

    private int maxEventLen;
    private int currIndex;
    private int currEventsLen;
    private Object/* T */[] events;

    // ------------------------------------------------------------------------

    public InMemoryCyclicEventAppender() {
	this(DEFAUT_LEN);
    }

    public InMemoryCyclicEventAppender(int len) {
	this.maxEventLen = len;
	this.events = new Object[len];
    }

    // ------------------------------------------------------------------------

    public int getMaxEventLen() {
	return maxEventLen;
    }

    @Override
    public void sendEvent(T event) {
	synchronized (lock) {
	    doSendEvent(event);
	}
    }

    @Override
    public void sendEvents(Collection<T> events) {
	synchronized (lock) {
	    for (T event : events) {
		doSendEvent(event);
	    }
	}
    }

    protected void doSendEvent(T event) {
	currIndex = modulo(currIndex + 1);
	if (events[currIndex] == null) {
	    currEventsLen++;
	}
	events[currIndex] = event;
    }

    @SuppressWarnings("unchecked") // TODO: use typed field "T[] events"
    public List<T> clearAndGet() {
	synchronized (lock) {
	    List<T> res = new ArrayList<T>(currEventsLen);
	    if (currEventsLen != 0) {
		int index = modulo(currIndex - currEventsLen + 1);
		for (int c = 0; c < currEventsLen; c++, index = modulo(index + 1)) {
		    res.add((T) events[index]);
		    events[index] = null;
		}
		currEventsLen = 0;
		currIndex = 0;
	    }
	    return res;
	}
    }

    @SuppressWarnings("unchecked") // TODO: use typed field "T[] events"
    public List<T> getCopy() {
	synchronized (lock) {
	    List<T> res = new ArrayList<T>(currEventsLen);
	    if (currEventsLen != 0) {
		int index = modulo(currIndex - currEventsLen + 1);
		for (int c = 0; c < currEventsLen; c++, index = modulo(index + 1)) {
		    res.add((T) events[index]);
		}
	    }
	    return res;
	}
    }

    private int modulo(int i) {
	if (i < 0)
	    return i + maxEventLen;
	else if (i >= maxEventLen)
	    return i - maxEventLen;
	else
	    return i;
    }

}
