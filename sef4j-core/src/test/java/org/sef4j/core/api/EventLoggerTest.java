package org.sef4j.core.api;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sef4j.core.helpers.appenders.InMemoryEventSender;


public class EventLoggerTest {

	private InMemoryEventSender inMemoryEventSender;
	private EventLogger sut;

	@Before
	public void setup() {
		inMemoryEventSender = new InMemoryEventSender();
		EventLoggerFactory eventLoggerFactory = new EventLoggerFactory(new EventLoggerContext() {
		    public EventSender[] getInheritedAppendersFor(String eventLoggerName) {
		    	return new EventSender[] { inMemoryEventSender };
		    }
		});
		sut = eventLoggerFactory.getEventLogger("test");
	}

	@Test
	public void testSendEvent() {
		// Prepare
		Object event0 = new Object();
		Object event1 = new Object();
		// Perform
		sut.sendEvent(event0);
		sut.sendEvent(event1);
		// Post-check
		List<Object> res = inMemoryEventSender.clearAndGet();
		Assert.assertEquals(2, res.size());
		Assert.assertSame(event0, res.get(0));
		Assert.assertSame(event1, res.get(1));
	}

}
