package org.sef4j.callstack.handlers;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.dummy.InstrumentedRecurseCallStackFoo;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.core.api.proptree.PropTreeNode;


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
		PropTreeNode root = PropTreeNode.newRoot();
		CallTreeStatsUpdaterCallStackHandler handler = new CallTreeStatsUpdaterCallStackHandler(root);
		InstrumentedRecurseCallStackFoo foo = new InstrumentedRecurseCallStackFoo();
		foo.repeatBazCount = 2;
	
		LocalCallStack.get().curr().addRootCallStackHandler(handler);
		// Perform
		foo.fooRecurseBarBaz();
		
		// Post-check
		LocalCallStack.get().curr().removeRootCallStackHandler(handler);
		PropTreeNode fooNode = root.getChildMap().get("foo");
		Assert.assertNotNull(fooNode);
		PropTreeNode recurseBarNode = fooNode.getChildMap().get("recurseBar");
		Assert.assertNotNull(recurseBarNode);
		PropTreeNode recurseBar1Node = recurseBarNode.getChildMap().get("recurseBar");
		Assert.assertNotNull(recurseBar1Node);
		PropTreeNode barNode = recurseBar1Node.getChildMap().get("bar");		
		Assert.assertNotNull(barNode);
		PropTreeNode bazNode = barNode.getChildMap().get("baz");		
		Assert.assertNotNull(bazNode);
		
		PerfStats bazStats = bazNode.getOrCreateProp("stats", PerfStats.FACTORY);
		Assert.assertEquals(foo.repeatBazCount, bazStats.getElapsedTimeStats().cumulatedCount());
	}
	
}
