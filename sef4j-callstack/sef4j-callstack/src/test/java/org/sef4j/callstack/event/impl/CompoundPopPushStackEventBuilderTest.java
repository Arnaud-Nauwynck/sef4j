package org.sef4j.callstack.event.impl;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.event.StackEvent.CompoundPopPushStackEvent;
import org.sef4j.callstack.event.StackEvent.PopStackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;
import org.sef4j.callstack.event.StackEventTstUtils;


public class CompoundPopPushStackEventBuilderTest {

	private CompoundPopPushStackEventBuilder sut = new CompoundPopPushStackEventBuilder();

	@Test
	public void testClearAndBuildOrNull() {
		// Prepare
		// Perform
		CompoundPopPushStackEvent res = sut.clearAndBuildOrNull();
		// Post-check
		Assert.assertNull(res);
	}
	
	@Test
	public void testAcceptPushStackEvent_PushPop() {
		// Prepare
		PushStackEvent event1 = StackEventTstUtils.newPush("foo");
		PopStackEvent event2 = StackEventTstUtils.newPop("foo");
		// Perform
		sut.acceptPushStackEvent(event1);
		sut.acceptPopStackEvent(event2);
		// Post-check
		CompoundPopPushStackEvent res = sut.build();
		Assert.assertEquals(0, res.getPushedEvents().length);
		Assert.assertEquals(0, res.getPopEvents().length);
	}

	@Test
	public void testAcceptPushStackEvent_PopPush() {
		// Prepare
		PopStackEvent event1 = StackEventTstUtils.newPop("foo");
		PushStackEvent event2 = StackEventTstUtils.newPush("foo");
		// Perform
		sut.acceptPopStackEvent(event1);
		sut.acceptPushStackEvent(event2);
		// Post-check
		CompoundPopPushStackEvent res = sut.build();
		Assert.assertEquals(1, res.getPopEvents().length);
		Assert.assertEquals(1, res.getPushedEvents().length);
	}
	
	
}
