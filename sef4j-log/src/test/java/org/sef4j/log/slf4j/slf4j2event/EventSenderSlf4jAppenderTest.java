package org.sef4j.log.slf4j.slf4j2event;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.core.helpers.senders.InMemoryEventSender;
import org.sef4j.log.slf4j.BasicConfigureTstHelper;
import org.sef4j.log.slf4j.LogLevel;
import org.slf4j.Logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.IThrowableProxy;


public class EventSenderSlf4jAppenderTest {

	InMemoryEventSender<LoggingEventExt> targetEventSender = new InMemoryEventSender<LoggingEventExt>();
	EventSenderSlf4jAppender sut = new EventSenderSlf4jAppender(targetEventSender);

	private LoggerContext slf4jLoggerContext = BasicConfigureTstHelper.newLoggerContextWithAppender(
			null, "appender1", sut);
	private Logger logger = slf4jLoggerContext.getLogger(EventSenderSlf4jAppenderTest.class);
	
	@Test
	public void testInfo() {
		// Prepare
		String msg1 = "test message1";
		String msg2 = "test message2";
		
		// Perform
		logger.info(msg1);
		logger.info(msg2);

		// Post-check
		List<LoggingEventExt> events = targetEventSender.clearAndGet();
		Assert.assertEquals(2,  events.size());
		LoggingEventExt e1 = events.get(0);
		Assert.assertEquals(msg1, e1.getMessage());
		LoggingEventExt e2 = events.get(1);
		Assert.assertEquals(msg2, e2.getMessage());
	}


	@Test
	public void testError() {
		// Prepare
		String msg1 = "test error message1";
		String msg2 = "test error message2";
		// Perform
		logger.error(msg1);
		logger.error(msg2, new Exception());
		// Post-check
		List<LoggingEventExt> events = targetEventSender.clearAndGet();
		Assert.assertEquals(2,  events.size());
		LoggingEventExt e1 = events.get(0);
		Assert.assertEquals(msg1, e1.getMessage());
		IThrowableProxy e1Ex = e1.getThrowable();
		Assert.assertNull(e1Ex);
		LoggingEventExt e2 = events.get(1);
		Assert.assertEquals(msg2, e2.getMessage());
		IThrowableProxy e2Ex = e2.getThrowable();
		Assert.assertNotNull(e2Ex);
	}

	
	@Test
	public void testInfo_withPropsParams() {
		// Prepare
		String msg1 = "test message1 (with stack params...)";
		String msg2 = "test nested message";
		// Perform
		StackPopper toPop = LocalCallStack.meth("C", "test")
				.withInheritableProp("prop1", "propValue1")
				.p("param1", "paramValue1")
				.push();
		try {
			logger.info(msg1);
			logWithinNestedMeth(msg2);
		} finally {
			toPop.close();
		}
		// Post-check
		List<LoggingEventExt> events = targetEventSender.clearAndGet();
		Assert.assertEquals(2, events.size());
		
		LoggingEventExt e1 = events.get(0);
		Assert.assertEquals(msg1, e1.getMessage());
		assertEqualsMap(new Object[] { "param1", "paramValue1" }, e1.getParams());
		assertEqualsMap(new Object[] { "prop1", "propValue1" }, e1.getProps());
		
		LoggingEventExt e2 = events.get(1);
		Assert.assertEquals(msg2, e2.getMessage());
		assertEqualsMap(new Object[] { "param2", "paramValue2" }, e2.getParams());
		assertEqualsMap(new Object[] { "prop1", "propValue1", "prop2", "propValue2" }, e2.getProps());
	}
	

	protected void logWithinNestedMeth(String msg) {
		StackPopper toPop = LocalCallStack.meth("C", "nestedMeth")
				.withInheritableProp("prop2", "propValue2")
				.p("param2", "paramValue2")
				.push();
		try {
			logger.info(msg);
		} finally {
			toPop.close();
		}
	}

	@Test
	public void testInfo_maskReplace() {
		// Prepare
		String msg1 = "test message1";
		// Perform
		Slf4jAppenderEventMask tmpMmask = new Slf4jAppenderEventMask(true, null, null);
		Slf4jAppenderThreadLocalMask.maskLogLevelText(tmpMmask, logger, LogLevel.INFO, msg1);
		// => mask .. logger.info(msg1) ..unmask;
		// Post-check
		List<LoggingEventExt> events = targetEventSender.clearAndGet();
		Assert.assertEquals(0,  events.size());

		// Prepare
		String replacedMsg2 = "replaced test message2";
		LoggingEventExt replacedEvent2 = new LoggingEventExt.Builder()
			.withMessage(replacedMsg2)
			.build();
		// Perform
		Slf4jAppenderEventMask tmpMmask2 = new Slf4jAppenderEventMask(true, replacedEvent2, null);
		Slf4jAppenderThreadLocalMask.maskLogLevelText(tmpMmask2, logger, LogLevel.INFO, msg1);
		// Post-check
		events = targetEventSender.clearAndGet();
		Assert.assertEquals(1,  events.size());
		LoggingEventExt e2 = events.get(0);
		Assert.assertEquals(replacedMsg2, e2.getMessage());
	}

	
	protected static void assertEqualsMap(Object[] expectedNV, Map<String,Object> actual) {
		int len = expectedNV.length / 2;
		Assert.assertEquals(len, actual.size());
		for (int index = 0, i = 0; i < len; i++,index+=2) {
			String expectedKey = (String) expectedNV[index];
			Object expectedValue = expectedNV[index + 1];
			Assert.assertEquals(expectedValue, actual.get(expectedKey));
		}
	}
}
