package org.sef4j.callstack.event.impl;

import java.util.List;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.event.StackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;
import org.sef4j.callstack.event.StackEventTstUtils;


public class AsyncCompoundPushPopSenderListenerTest {

	private InMemoryStackEventListener inMemoryEventListener = new InMemoryStackEventListener();
	private AsyncCompoundPushPopSenderListener sut = new AsyncCompoundPushPopSenderListener(
			Executors.newScheduledThreadPool(1), 1, // period=1 second 
			inMemoryEventListener);
	
	@Test
	public void testStartStop() throws Exception {
		// Prepare
		// Perform
		sut.start();
		for (int i = 0; i < 10; i++) {
			sut.onEvent(StackEventTstUtils.newPush("tmpFoo"));
			sut.onEvent(StackEventTstUtils.newPop("tmpFoo"));
		}
		sut.onEvent(StackEventTstUtils.newPush("foo"));
		sut.onEvent(StackEventTstUtils.newPush("bar"));
		Thread.sleep(1500);
		sut.stop();
		// sut.flush();
		// Post-check
		List<StackEvent> events = inMemoryEventListener.clearAndGet();
		Assert.assertTrue(events.size() >= 1);
		StackEvent.CompoundPopPushStackEvent lastCompoundEvent = (StackEvent.CompoundPopPushStackEvent) events.get(events.size() - 1);
		PushStackEvent[] lastPushedEvents = lastCompoundEvent.getPushedEvents();
		Assert.assertEquals(2, lastPushedEvents.length);
		Assert.assertEquals("foo", lastPushedEvents[0].getName());
		Assert.assertEquals("bar", lastPushedEvents[1].getName());
	}
}
