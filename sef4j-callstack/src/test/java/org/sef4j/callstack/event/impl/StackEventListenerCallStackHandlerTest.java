package org.sef4j.callstack.event.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.dummy.InstrumentedRecurseCallStackFoo;
import org.sef4j.callstack.event.StackEvent;
import org.sef4j.callstack.event.StackEvent.PopStackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;


public class StackEventListenerCallStackHandlerTest {

	private InMemoryStackEventListener eventListener = new InMemoryStackEventListener();
	private StackEventListenerCallStackHandler sut = new StackEventListenerCallStackHandler(eventListener);

	@Test
	public void testOnPush() {
		// cf testOnPushPop_InstrumentedFoo
	}
	
	@Test
	public void testOnPop() {
		// cf testOnPushPop_InstrumentedFoo
	}
	
	@Test
	public void testOnPushPop_InstrumentedFoo_fooBar() {
		// Prepare
		InstrumentedRecurseCallStackFoo foo = new InstrumentedRecurseCallStackFoo();
		LocalCallStack.currThreadStackElt().addRootCallStackHandler(sut);
		// Perform
		foo.fooBar();
		// Post-check
		LocalCallStack.currThreadStackElt().removeRootCallStackHandler(sut);
		List<StackEvent> events = eventListener.clearAndGet();
		Assert.assertEquals(4,  events.size());
		PushStackEvent event1 = (PushStackEvent) events.get(0);
		Assert.assertEquals("foo", event1.getName());
		PushStackEvent event2 = (PushStackEvent) events.get(1);
		Assert.assertEquals("bar", event2.getName());
		PopStackEvent event3 = (PopStackEvent) events.get(2);
		Assert.assertEquals("bar", event3.getName());
		PopStackEvent event4 = (PopStackEvent) events.get(3);
		Assert.assertEquals("foo", event4.getName());
	}

}
