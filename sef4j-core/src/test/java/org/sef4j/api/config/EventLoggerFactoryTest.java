package org.sef4j.api.config;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.api.config.EventLogger;
import org.sef4j.api.config.EventLoggerContext;
import org.sef4j.api.config.EventLoggerFactory;
import org.sef4j.core.appenders.InMemoryEventAppender;

public class EventLoggerFactoryTest {

    private static class E {
    }

    private InMemoryEventAppender<E> appender1 = new InMemoryEventAppender<E>();
    private InMemoryEventAppender<E> appender2 = new InMemoryEventAppender<E>();
    private EventLoggerContext eventLoggerContext = new EventLoggerContext();
    private EventLoggerFactory sut = new EventLoggerFactory(eventLoggerContext);

    private EventLogger loggerA = sut.getEventLogger("a");
    private EventLogger loggerAB = sut.getEventLogger("a.b");

    @Test
    public void testSendEvent_addAppender() {
	// Prepare
	E event = new E();
	// Perform
	loggerA.sendEvent(event);
	// Post-check
	assertAppenderEvents(appender1);
	assertAppenderEvents(appender2);

	// Prepare
	// Perform
	eventLoggerContext.addAppender("appender1", appender1);
	eventLoggerContext.addAppender("appender2", appender2);
	// Post-check
	assertAppenderEvents(appender1);
	assertAppenderEvents(appender2);

	// Prepare
	eventLoggerContext.addLoggerToAppenderRef("a", "appender1", true);
	// Perform
	loggerA.sendEvent(event);
	// Post-check
	assertAppenderEvents(appender1, event);
	assertAppenderEvents(appender2);

	// Prepare
	// Perform
	loggerAB.sendEvent(event);
	// Post-check
	assertAppenderEvents(appender1, event);
	assertAppenderEvents(appender2);

	// Prepare
	eventLoggerContext.addLoggerToAppenderRef("a.b", "appender2", true);
	// Perform
	loggerAB.sendEvent(event);
	// Post-check
	assertAppenderEvents(appender1, event);
	assertAppenderEvents(appender2, event);

    }

    private static void assertAppenderEvents(InMemoryEventAppender<E> appender, E... expectedEvents) {
	List<E> actualEvents = appender.clearAndGet();
	int len = Math.min(expectedEvents.length, actualEvents.size()); // see assert below
	for (int i = 0; i < len; i++) {
	    Assert.assertSame(expectedEvents[i], actualEvents.get(i));
	}
	Assert.assertEquals(expectedEvents.length, actualEvents.size());
    }

}
