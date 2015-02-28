package org.sef4j.callstack.handlers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.dummy.InstrumentedRecurseCallStackFoo;
import org.sef4j.callstack.event.StackEvent.PopStackEvent;
import org.sef4j.callstack.event.StackEvent.ProgressStepStackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;
import org.sef4j.callstack.event.StackEventTstUtils;
import org.sef4j.callstack.stats.ThreadTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Slf4jLoggerAdapterCallStackHandlerTest {

	private Logger logger = LoggerFactory.getLogger("test");
	private Slf4jLoggerAdapterCallStackHandler sut = new Slf4jLoggerAdapterCallStackHandler(logger);
	
	@Test
	public void testOnPush() {
		// cf testOnPushPop_InstrumentedFoo
	}

	@Test
	public void testOnPop() {
		// cf testOnPushPop_InstrumentedFoo
	}

	@Test
	public void test_InstrumentedFoo() {
		// Prepare
		InstrumentedRecurseCallStackFoo foo = new InstrumentedRecurseCallStackFoo();
		LocalCallStack.get().curr().addRootCallStackHandler(sut);
		
		// Perform
		foo.fooBar();
		
		// Post-check
		LocalCallStack.get().curr().removeRootCallStackHandler(sut);
		// no assertion here! .. cf log console:
//		[main] INFO  test - > foo
//		[main] INFO  test - > bar
//		[main] INFO  test - < bar, took 0 ms
//		[main] INFO  test - < foo, took 6 ms
	}

	@Test
	public void testOnProgressStep() {
		// Prepare
		InstrumentedRecurseCallStackFoo foo = new InstrumentedRecurseCallStackFoo();
		LocalCallStack.get().curr().addRootCallStackHandler(sut);
		
		// Perform
		foo.fooProgress(3);
		
		// Post-check
		LocalCallStack.get().curr().removeRootCallStackHandler(sut);
		// no assertion here! .. cf log console:

//		[main] INFO  test - > foo .. [0/3]
//		[main] INFO  test -  .. [1/3]
//		[main] INFO  test -  .. [2/3]
//		[main] INFO  test -  .. [3/3]
//		[main] INFO  test - < foo, took 6 ms
	}

	@Test
	public void testFormatLogMessagePush() {
		// Prepare
		String name = "foo"; 
		Map<String, Object> inheritedProps = new HashMap<String, Object>(); 
		inheritedProps.put("prop1", 123);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("param1", 456);
		int progressExpectedCount = 3;
		// Perform
		String msg = Slf4jLoggerAdapterCallStackHandler.formatLogMessagePush(name, inheritedProps, params, progressExpectedCount);
		// Post-check
		Assert.assertEquals("> foo [prop1=123] (param1=456) .. [0/3]", msg);
	}

	@Test
	public void testFormatLogMessagePop() {
		// Prepare
		String name = "foo";
		long elapsedTime = ThreadTimeUtils.approxMillisToNanos(10);
		// Perform
		String msg = Slf4jLoggerAdapterCallStackHandler.formatLogMessagePop(name, elapsedTime);
		// Post-check
		Assert.assertEquals("< foo, took 10 ms", msg);
	}

	@Test
	public void testFormatLogMessageProgress() {
		int progressIndex = 1;
		int progressExpectedCount = 3;
		String messageProgress = "step1";
		// Perform
		String msg = Slf4jLoggerAdapterCallStackHandler.formatLogMessageProgress(progressIndex, progressExpectedCount, messageProgress);
		// Post-check
		Assert.assertEquals(" .. [1/3: step1]", msg);
	}

	@Test
	public void testFormatLogMessageCompound() {
		// Prepare
		PopStackEvent[] popEvents = new PopStackEvent[] {
				StackEventTstUtils.newPop("prev1a"), StackEventTstUtils.newPop("prev1"),
		};
		PushStackEvent[] pushEvents = new PushStackEvent[] {
				StackEventTstUtils.newPush("foo"), StackEventTstUtils.newPush("bar")
		};
		ProgressStepStackEvent[] progressSteps = new ProgressStepStackEvent[] {
				StackEventTstUtils.newProgress(1, 3, "step1"), null
		};
		// Perform
		String msg = Slf4jLoggerAdapterCallStackHandler.formatLogMessageCompound(popEvents, pushEvents, progressSteps);
		// Post-check
		Assert.assertEquals("< prev1a < prev1 > foo ...[1/3: step1] > bar ", msg);
	}
}
