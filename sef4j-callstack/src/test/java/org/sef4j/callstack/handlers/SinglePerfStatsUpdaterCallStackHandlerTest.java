package org.sef4j.callstack.handlers;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.dummy.InstrumentedRecurseCallStackFoo;
import org.sef4j.callstack.stats.PerfStats;


public class SinglePerfStatsUpdaterCallStackHandlerTest {

	PerfStats perfStats = new PerfStats();
	private SinglePerfStatsUpdaterCallStackHandler sut = new SinglePerfStatsUpdaterCallStackHandler(perfStats);
	
	@Test
	public void testOnPush() {
		// cf test_InstrumentedFoo
	}

	@Test
	public void testOnPop() {
		// cf test_InstrumentedFoo
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
		Assert.assertEquals(1, perfStats.getElapsedTimeStats().cumulatedCount());
	}
	
}
