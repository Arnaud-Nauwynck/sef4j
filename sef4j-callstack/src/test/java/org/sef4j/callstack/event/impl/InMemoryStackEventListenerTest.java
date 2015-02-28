package org.sef4j.callstack.event.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.event.StackEvent;
import org.sef4j.callstack.event.StackEvent.PopStackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;
import org.sef4j.callstack.event.StackEventTstUtils;


public class InMemoryStackEventListenerTest {

	private InMemoryStackEventListener sut = new InMemoryStackEventListener();
	
	@Test
	public void testOnEvent() {
		// Prepare
		PushStackEvent event0 = StackEventTstUtils.newPush("foo");
		PopStackEvent event1 = StackEventTstUtils.newPop("foo");
		// Perform
		sut.onEvent(event0);
		sut.onEvent(event1);
		// Post-check
		List<StackEvent> res = sut.clearAndGet();
		Assert.assertSame(event0, res.get(0));
		Assert.assertSame(event1, res.get(1));
		List<StackEvent> res2 = sut.clearAndGet();
		Assert.assertTrue(res2.isEmpty());
	}

	@Test
	public void testClearAndGet() {
		List<StackEvent> res = sut.clearAndGet();
		Assert.assertTrue(res.isEmpty());
	}
	
}
