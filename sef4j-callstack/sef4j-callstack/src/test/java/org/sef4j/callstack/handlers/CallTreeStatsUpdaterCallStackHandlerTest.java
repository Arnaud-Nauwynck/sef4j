package org.sef4j.callstack.handlers;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.dummy.InstrumentedRecurseCallStackFoo;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stattree.CallTreeNode;


public class CallTreeStatsUpdaterCallStackHandlerTest {

	@Test
	public void testOnPush() {
		// cf testOnPushPop_InstrumentedRecurseCallStackFoo()
	}

	@Test
	public void testOnPop() {
		// cf testOnPushPop_InstrumentedRecurseCallStackFoo()
	}
	
	@Test
	public void testOnPushPop_InstrumentedRecurseCallStackFoo() {
		// Prepare
		CallTreeNode root = CallTreeNode.newRoot();
		CallTreeStatsUpdaterCallStackHandler handler = new CallTreeStatsUpdaterCallStackHandler(root);
		InstrumentedRecurseCallStackFoo foo = new InstrumentedRecurseCallStackFoo();
		foo.repeatBazCount = 2;
	
		LocalCallStack.get().curr().addRootCallStackHandler(handler);
		// Perform
		foo.fooRecurseBarBaz();
		
		// Post-check
		LocalCallStack.get().curr().removeRootCallStackHandler(handler);
		CallTreeNode fooNode = root.getChildMap().get("foo");
		Assert.assertNotNull(fooNode);
		CallTreeNode recurseBarNode = fooNode.getChildMap().get("recurseBar");
		Assert.assertNotNull(recurseBarNode);
		CallTreeNode recurseBar1Node = recurseBarNode.getChildMap().get("recurseBar");
		Assert.assertNotNull(recurseBar1Node);
		CallTreeNode barNode = recurseBar1Node.getChildMap().get("bar");		
		Assert.assertNotNull(barNode);
		CallTreeNode bazNode = barNode.getChildMap().get("baz");		
		Assert.assertNotNull(bazNode);
		
		PerfStats bazStats = bazNode.getStats();
		Assert.assertEquals(foo.repeatBazCount, bazStats.getElapsedTimeStats().getSlotsCount());
	}
	
}
