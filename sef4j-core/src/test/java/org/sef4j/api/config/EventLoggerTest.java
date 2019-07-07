package org.sef4j.api.config;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sef4j.api.EventAppender;
import org.sef4j.api.config.EventLogger;
import org.sef4j.api.config.EventLoggerContext;
import org.sef4j.api.config.EventLoggerFactory;
import org.sef4j.core.appenders.InMemoryEventAppender;

public class EventLoggerTest {

    private static class E {
    }

    private InMemoryEventAppender<E> inMemoryEventSender;
    private EventLogger sut;

    @Before
    public void setup() {
	inMemoryEventSender = new InMemoryEventAppender<E>();
	EventLoggerFactory eventLoggerFactory = new EventLoggerFactory(new EventLoggerContext() {
	    @SuppressWarnings("unchecked")
	    @Override
	    public EventAppender<E>[] getInheritedAppendersFor(String eventLoggerName) {
		return (EventAppender<E>[]) new EventAppender<?>[] { inMemoryEventSender };
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
