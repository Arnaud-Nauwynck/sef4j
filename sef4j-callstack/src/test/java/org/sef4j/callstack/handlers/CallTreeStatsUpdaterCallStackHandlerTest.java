package org.sef4j.callstack.handlers;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.callstack.dummy.InstrumentedRecurseCallStackFoo;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;


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
		String cName = org.sef4j.callstack.dummy.InstrumentedRecurseCallStackFoo.class.getName();
		LocalCallStack.get().curr().removeRootCallStackHandler(handler);
		PropTreeNode fooNode = root.getChildMap().get(cName + ":foo");
		Assert.assertNotNull(fooNode);
		PropTreeNode recurseBarNode = fooNode.getChildMap().get(cName + ":recurseBar");
		Assert.assertNotNull(recurseBarNode);
		PropTreeNode recurseBar1Node = recurseBarNode.getChildMap().get(cName + ":recurseBar");
		Assert.assertNotNull(recurseBar1Node);
		PropTreeNode barNode = recurseBar1Node.getChildMap().get(cName + ":bar");		
		Assert.assertNotNull(barNode);
		PropTreeNode bazNode = barNode.getChildMap().get(cName + ":baz");		
		Assert.assertNotNull(bazNode);
		
		PerfStats bazStats = bazNode.getPropOrNull("stats", PerfStats.class);
		Assert.assertEquals(foo.repeatBazCount, bazStats.getElapsedTimeStats().cumulatedCount());
	}
	
}
