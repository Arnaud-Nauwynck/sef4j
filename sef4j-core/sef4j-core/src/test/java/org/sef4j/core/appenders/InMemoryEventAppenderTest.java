package org.sef4j.core.appenders;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sef4j.core.api.EventAppender;
import org.sef4j.core.api.EventLogger;
import org.sef4j.core.api.EventLoggerContext;
import org.sef4j.core.api.EventLoggerFactory;


public class InMemoryEventAppenderTest {

	private InMemoryEventAppender sut;
	private EventLogger eventLogger;

	@Before
	public void setup() {
		sut = new InMemoryEventAppender("appender1");
		EventLoggerFactory eventLoggerFactory = new EventLoggerFactory(new EventLoggerContext() {
		    public EventAppender[] getInheritedAppendersFor(String eventLoggerName) {
		    	return new EventAppender[] { sut };
		    }
		});
		eventLogger = eventLoggerFactory.getEventLogger("test");
	}

	@Test
	public void testHandleEvent() {
		// Prepare
		Object event0 = new Object();
		Object event1 = new Object();
		// Perform
		sut.handleEvent(event0);
		sut.handleEvent(event1);
		// Post-check
		List<Object> res = sut.clearAndGet();
		Assert.assertEquals(2, res.size());
		Assert.assertSame(event0, res.get(0));
		Assert.assertSame(event1, res.get(1));
	}

	@Test
	public void testLoggerSendEvent() {
		// Prepare
		Object event0 = new Object();
		Object event1 = new Object();
		// Perform
		eventLogger.sendEvent(event0); // => sut.handleEvent(event0);
		eventLogger.sendEvent(event1); // => sut.handleEvent(event1);
		// Post-check
		List<Object> res = sut.clearAndGet();
		Assert.assertEquals(2, res.size());
		Assert.assertSame(event0, res.get(0));
		Assert.assertSame(event1, res.get(1));
	}

}
