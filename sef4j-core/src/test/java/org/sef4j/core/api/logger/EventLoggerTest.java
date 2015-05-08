package org.sef4j.core.api.logger;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sef4j.core.api.EventSender;
import org.sef4j.core.helpers.senders.InMemoryEventSender;


public class EventLoggerTest {

    private static class E {}
	private InMemoryEventSender<E> inMemoryEventSender;
	private EventLogger sut;

	@Before
	public void setup() {
		inMemoryEventSender = new InMemoryEventSender<E>();
		EventLoggerFactory eventLoggerFactory = new EventLoggerFactory(new EventLoggerContext() {
		    @SuppressWarnings("unchecked")
            @Override
		    public EventSender<E>[] getInheritedAppendersFor(String eventLoggerName) {
		    	return (EventSender<E>[]) new EventSender<?>[] { inMemoryEventSender };
		    }
		});
		sut = eventLoggerFactory.getEventLogger("test");
	}

	@Test
	public void testSendEvent() {
		// Prepare
		E event0 = new E();
		E event1 = new E();
		// Perform
		sut.sendEvent(event0);
		sut.sendEvent(event1);
		// Post-check
		List<E> res = inMemoryEventSender.clearAndGet();
		Assert.assertEquals(2, res.size());
		Assert.assertSame(event0, res.get(0));
		Assert.assertSame(event1, res.get(1));
	}

}
