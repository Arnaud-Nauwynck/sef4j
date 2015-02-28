package org.sef4j.callstack.handlers;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.dummy.InstrumentedRecurseCallStackFoo;
import org.sef4j.callstack.event.StackEvent;
import org.sef4j.callstack.event.StackEvent.PopStackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;
import org.sef4j.callstack.event.impl.InMemoryStackEventListener;
import org.sef4j.callstack.event.impl.StackEventListenerCallStackHandler;


public class MaxDepthCallStackHandlerFilterTest {

	private InMemoryStackEventListener inMemoryListener = new InMemoryStackEventListener();
	private MaxDepthCallStackHandlerFilter sut = new MaxDepthCallStackHandlerFilter(
			new StackEventListenerCallStackHandler(inMemoryListener), 1);
	
	@Test
	public void testOnPush() {
		// Prepare
		InstrumentedRecurseCallStackFoo foo = new InstrumentedRecurseCallStackFoo();
		LocalCallStack.currThreadStackElt().addRootCallStackHandler(sut);
		// Perform
		foo.fooBar();
		// Post-check
		LocalCallStack.currThreadStackElt().removeRootCallStackHandler(sut);
		List<StackEvent> filteredEvents = inMemoryListener.clearAndGet();
		Assert.assertEquals(2, filteredEvents.size());
		PushStackEvent pushFoo = (PushStackEvent) filteredEvents.get(0);
		PopStackEvent popFoo = (PopStackEvent) filteredEvents.get(1);
		Assert.assertEquals("foo", pushFoo.getName());
		Assert.assertEquals("foo", popFoo.getName());
	}

	@Test
	public void testOnPush_fooRecurse() {
		// Prepare
		InstrumentedRecurseCallStackFoo foo = new InstrumentedRecurseCallStackFoo();
		foo.recurseBarLevel = 4;
		LocalCallStack.currThreadStackElt().addRootCallStackHandler(sut);
		// Perform
		foo.fooRecurseBarBaz();
		// Post-check
		LocalCallStack.currThreadStackElt().removeRootCallStackHandler(sut);
		List<StackEvent> filteredEvents = inMemoryListener.clearAndGet();
		Assert.assertEquals(2, filteredEvents.size());
		PushStackEvent pushFoo = (PushStackEvent) filteredEvents.get(0);
		PopStackEvent popFoo = (PopStackEvent) filteredEvents.get(1);
		Assert.assertEquals("foo", pushFoo.getName());
		Assert.assertEquals("foo", popFoo.getName());
	}

}
