package org.sef4j.callstack.stattree.changes;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stattree.changes.PendingCountChangeCollector;
import org.sef4j.core.api.proptree.PropTreeNode;


public class PendingCountChangeCollectorTest {

	private PropTreeNode rootNode = PropTreeNode.newRoot();
	private PropTreeNode fooNode = rootNode.getOrCreateChild("foo");
	private PropTreeNode fooBarNode = fooNode.getOrCreateChild("bar");
	// private CallTreeNode fooBarBazNode = fooBarNode.getOrCreateChild("baz");

	private PendingPerfCount fooPendings = fooNode.getOrCreateProp("stats", PerfStats.FACTORY).getPendingCounts();
	private PendingPerfCount fooBarPendings = fooBarNode.getOrCreateProp("stats", PerfStats.FACTORY).getPendingCounts();
	// private PendingPerfCount fooBarBazPendings = fooBarBazNode.getStats().getPendingCounts();

	private PendingCountChangeCollector sut = new PendingCountChangeCollector(rootNode);
	
	@Test
	public void testMarkAndCollectChanges_fooAdd1_chg_fooAdd2_chg_fooRemove1_chg_fooRemove2() {
		// Prepare
		long startTime1 = 123L;
		fooPendings.addPending(startTime1);
		// Perform
		Map<String, PendingPerfCount> changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		PendingPerfCount fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(1, startTime1, fooChange);

		// Prepare
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());
		
		// Prepare
		long startTime2 = 234L;
		fooPendings.addPending(startTime2);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(2, startTime1+startTime2, fooChange);

		// Prepare
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());

		// Prepare
		fooPendings.removePending(startTime1);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(1, startTime2, fooChange);

		// Prepare
		fooPendings.removePending(startTime2);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(0, 0, fooChange);
	}


	@Test
	public void testMarkAndCollectChanges_fooAdd1_fooAdd2_chg_fooRemove1_chg_fooRemove2() {
		// Prepare
		long startTime1 = 123L;
		fooPendings.addPending(startTime1);
		long startTime2 = 234L;
		fooPendings.addPending(startTime2);
		// Perform
		Map<String, PendingPerfCount> changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		PendingPerfCount fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(2, startTime1+startTime2, fooChange);

		// Prepare
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());
		
		// Prepare
		fooPendings.removePending(startTime1);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(1, startTime2, fooChange);

		// Prepare
		fooPendings.removePending(startTime2);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(0, 0, fooChange);
	}

	@Test
	public void testMarkAndCollectChanges_fooAdd1_barAdd1_fooAdd2_chg_fooBarRemove1_chg_fooRemove2_chg() {
		// Prepare  
		// fooAdd1_barAdd1_fooAdd2
		long startTime1 = 123L;
		fooPendings.addPending(startTime1);
		long startTimeBar1 = 124L;
		fooBarPendings.addPending(startTimeBar1);
		long startTime2 = 234L;
		fooPendings.addPending(startTime2);
		// src status= 1->foo/bar, 2->foo
		// Perform
		Map<String, PendingPerfCount> changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(2, changes.size());
		PendingPerfCount fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(2, startTime1+startTime2, fooChange);
		PendingPerfCount fooBarChange = changes.get("foo/bar");
		Assert.assertNotNull(fooBarChange);
		assertPendingCounts(1, startTimeBar1, fooBarChange);

		// Prepare redo chg
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());
		
		// Prepare fooBarRemove1_chg
		fooBarPendings.removePending(startTimeBar1);
		// Perform
		// src  status= 1->foo,     2->foo  (foo.pendingCount=2, foo/bar.pendingCount=0)
		// prev status= 1->foo/bar, 2->foo  (foo.pendingCount=2, foo/bar.pendingCount=1)
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		Assert.assertNull(changes.get("foo")); // stil pendingCount=2
		fooBarChange = changes.get("foo/bar");
		Assert.assertNotNull(fooBarChange);
		assertPendingCounts(0, 0, fooBarChange);
		
		// Prepare fooRemove2_chg
		fooPendings.removePending(startTime2);
		// Perform
		// src  status= 1->foo              (foo.pendingCount=1, foo/bar.pendingCount=0)
		// prev status= 1->foo,     2->foo  (foo.pendingCount=2, foo/bar.pendingCount=0)
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		Assert.assertNull(changes.get("foo/bar"));
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(1, startTime1, fooChange);
		
	}


	@Test
	public void testMarkAndCollectChanges_fooAdd1_barAdd1_fooAdd2_chg_fooBarRemove1_fooRemove2_fooRemove1_chg() {
		// Prepare  
		// fooAdd1_barAdd1_fooAdd2_chg
		long startTime1 = 123L;
		fooPendings.addPending(startTime1);
		long startTimeBar1 = 124L;
		fooBarPendings.addPending(startTimeBar1);
		long startTime2 = 234L;
		fooPendings.addPending(startTime2);
		// src status= 1->foo/bar, 2->foo
		// Perform
		Map<String, PendingPerfCount> changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(2, changes.size());
		PendingPerfCount fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(2, startTime1+startTime2, fooChange);
		PendingPerfCount fooBarChange = changes.get("foo/bar");
		Assert.assertNotNull(fooBarChange);
		assertPendingCounts(1, startTimeBar1, fooBarChange);

		// Prepare redo chg
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());
		
		// Prepare fooBarRemove1_fooRemove2_fooRemove1_chg
		fooBarPendings.removePending(startTimeBar1);
		fooPendings.removePending(startTime2);
		fooPendings.removePending(startTime1);
		// Perform
		// src  status=                     (foo.pendingCount=0, foo/bar.pendingCount=0)
		// prev status= 1->foo/bar, 2->foo  (foo.pendingCount=2, foo/bar.pendingCount=1)
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(2, changes.size());
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		fooBarChange = changes.get("foo/bar");
		Assert.assertNotNull(fooBarChange);
		assertPendingCounts(0, 0, fooChange);
		assertPendingCounts(0, 0, fooBarChange);		
	}


	
	@Test
	public void testMarkAndCollectChanges_withSelfPropExtractor() {
		// replace default copy storage... sut = new PendingCountChangeCollector(rootNode);
		sut = new PendingCountChangeCollector(rootNode,
				rootNode, // <= store on self node!  CallTreeNode.newRoot(),
				PendingCountChangeCollector.DEFAULT_PENDING_SRC_COPY_EXTRACTOR,
				PendingCountChangeCollector.createGetOrCreatePropPendingExtractor("propPending")
				);
		
		// Prepare
		long startTime1 = 123L;
		fooPendings.addPending(startTime1);
		// Perform
		Map<String, PendingPerfCount> changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		PendingPerfCount fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(1, startTime1, fooChange);

		// Prepare
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());
		
		// Prepare
		long startTime2 = 234L;
		fooPendings.addPending(startTime2);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(2, startTime1+startTime2, fooChange);

		// Prepare
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());

		// Prepare
		fooPendings.removePending(startTime1);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(1, startTime2, fooChange);

		// Prepare
		fooPendings.removePending(startTime2);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertPendingCounts(0, 0, fooChange);
	}


	private static void assertPendingCounts(int expectedCount, long expectedSumStartTime, PendingPerfCount actual) {
		Assert.assertEquals(expectedCount, actual.getPendingCount());
		Assert.assertEquals(expectedSumStartTime, actual.getPendingSumStartTime());	
	}
}
