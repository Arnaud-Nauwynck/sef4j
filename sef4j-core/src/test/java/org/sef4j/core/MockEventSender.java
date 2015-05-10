package org.sef4j.core;

import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.sef4j.core.helpers.senders.InMemoryEventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockEventSender<T> extends InMemoryEventSender<T> {

	private static final Logger LOG = LoggerFactory.getLogger(MockEventSender.class);
	
	private final String displayName;

	private static int idGenerator = 1;
	private static int generateId() {
		return idGenerator++;
	}
	
	public MockEventSender() {
		this("MockEventSender-" + generateId());
	}

	public MockEventSender(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public void sendEvent(T event) {
		LOG.debug(displayName + " sendEvent");
		super.sendEvent(event);
	}

	@Override
	public void sendEvents(Collection<T> events) {
		LOG.debug(displayName + " sendEvents");
		super.sendEvents(events);
	}

	public void assertSameClearAndGet(@SuppressWarnings("unchecked") T... expected) {
		List<T> actual = clearAndGet();
		Assert.assertEquals(expected.length, actual.size());
		for(int i = 0; i < expected.length; i++) {
			Assert.assertSame(expected[i], actual.get(i));
		}
	}

}
