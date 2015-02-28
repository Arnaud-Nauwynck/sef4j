package org.sef4j.callstack.event.impl;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mockito.Mockito;
import org.sef4j.callstack.event.StackEvent.PopStackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;
import org.sef4j.callstack.event.StackEventTstUtils;
import org.sef4j.callstack.stats.ThreadTimeUtils;
import org.slf4j.Logger;


public class Slf4jLoggerAdapterStackEventVisitorTest {

	private Logger logger = Mockito.mock(Logger.class);
	private Slf4jLoggerAdapterStackEventVisitor sut = new Slf4jLoggerAdapterStackEventVisitor(logger);
	
	@Test
	public void testAcceptPushStackEvent() {
		// Prepare
		Mockito.doNothing().when(logger).info("> foo");
		// Perform
		sut.acceptPushStackEvent(StackEventTstUtils.newPush("foo"));
		// Post-check
		Mockito.verify(logger).info("> foo");
	}

	@Test
	public void testAcceptPushStackEvent_params() {
		// Prepare
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("param1", 123);
		params.put("param2", 456);
		Map<String, Object> props = new HashMap<String,Object>();
		props.put("prop1", 789);
		props.put("prop2", 890);
		PushStackEvent event = new PushStackEvent("foo",
				params , props, // params, props
				0, 0, 0, // <= start times
				0);

		Mockito.doNothing().when(logger).info("> foo [prop2=890, prop1=789] (param1=123, param2=456)");
		// Perform
		sut.acceptPushStackEvent(event);
		// Post-check
		Mockito.verify(logger).info("> foo [prop2=890, prop1=789] (param1=123, param2=456)");
	}

	@Test
	public void testAcceptPopStackEvent() {
		// Prepare
		long elapsedTimeNanos = ThreadTimeUtils.approxMillisToNanos(10);
		PopStackEvent event = new PopStackEvent("foo",
				elapsedTimeNanos, 
				0, 0, 0); // <= end times

		Mockito.doNothing().when(logger).info("< foo, took 10 ms");
		// Perform
		sut.acceptPopStackEvent(event);
		// Post-check
		Mockito.verify(logger).info("< foo, took 10 ms");
	}

}
