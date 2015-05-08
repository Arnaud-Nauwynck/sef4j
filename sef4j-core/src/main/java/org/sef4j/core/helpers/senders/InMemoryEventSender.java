package org.sef4j.core.helpers.senders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.sef4j.core.MockEvent;
import org.sef4j.core.api.EventSender;

/**
 * in-memory EventSender : append events in list, 
 * this class is thread-safe, and offer operation to atomically "clearAndGet" event list
 * 
 */
public class InMemoryEventSender<T> implements EventSender<T> {

	private Object lock = new Object();
	private List<T> events = new ArrayList<T>();
	
	// ------------------------------------------------------------------------
	
	public InMemoryEventSender() {
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public void sendEvent(T event) {
		synchronized(lock) {
			events.add(event);
		}
	}

	public void sendEvents(Collection<T> events) {
		synchronized(lock) {
			this.events.addAll(events);
		}
	}

	public List<T> clearAndGet() {
		List<T> res;
		synchronized(lock) {
			res = events;
			this.events = new ArrayList<T>();
		}
		return res;
	}

	public void assertSameClearAndGet(MockEvent... expected) {
		List<T> actual = clearAndGet();
		Assert.assertEquals(expected.length, actual.size());
		for(int i = 0; i < expected.length; i++) {
			Assert.assertSame(expected[i], actual.get(i));
		}
	}
	
}
