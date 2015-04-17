package org.sef4j.callstack.stattree.changes;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stattree.changes.BasicStatIgnorePendingChangeCollector;
import org.sef4j.core.api.proptree.PropTreeNode;


public class BasicStatIgnorePendingChangeCollectorTest {

	private PropTreeNode rootNode = PropTreeNode.newRoot();
	private PropTreeNode fooNode = rootNode.getOrCreateChild("foo");
	private PropTreeNode fooBarNode = fooNode.getOrCreateChild("bar");
	// private CallTreeNode fooBarBazNode = fooBarNode.getOrCreateChild("baz");

	private PerfStats fooStats = fooNode.getOrCreateProp("stats", PerfStats.FACTORY);
	private PerfStats fooBarStats = fooBarNode.getOrCreateProp("stats", PerfStats.FACTORY);
	// private PerfStats fooBarBazStats = fooBarBazNode.getStats();

	private BasicStatIgnorePendingChangeCollector sut = new BasicStatIgnorePendingChangeCollector(rootNode);
	
	@Test
	public void testMarkAndCollectChanges_fooAdd1_chg_fooAdd2_chg_fooRemove1_chg_fooRemove2() {
		// Prepare
		long startTime1 = 123L;
		fooStats.addPending(startTime1);
		// Perform
		Map<String, PerfStats> changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());

		// Prepare
		long startTime2 = 234L;
		fooStats.addPending(startTime2);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());

		// Prepare
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());

		// Prepare
		long elapsedTime1 = 1L;
		long endTime1 = startTime1 + elapsedTime1;
		fooStats.incrAndRemovePending(startTime1, startTime1, startTime1,
				endTime1, endTime1, endTime1);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		PerfStats fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertStats(1, elapsedTime1, fooChange);

		// Prepare
		long elapsedTime2 = 234L;
		long endTime2 = startTime2 + elapsedTime2;
		fooStats.incrAndRemovePending(startTime2, startTime2, startTime2,
				endTime2, endTime2, endTime2);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertStats(2, elapsedTime1+elapsedTime2, fooChange);
	}


	@Test
	public void testMarkAndCollectChanges_fooAdd1_fooAdd2_chg_fooRemove1_chg_fooRemove2() {
		// Prepare
		long startTime1 = 123L;
		fooStats.addPending(startTime1);
		long startTime2 = 234L;
		fooStats.addPending(startTime2);
		// Perform
		Map<String, PerfStats> changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());
		
		// Prepare
		long elapsedTime1 = 1L;
		long endTime1 = startTime1 + elapsedTime1;
		fooStats.incrAndRemovePending(startTime1, startTime1, startTime1,
				endTime1, endTime1, endTime1);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		PerfStats fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertStats(1, elapsedTime1, fooChange);

		// Prepare
		long elapsedTime2 = 234L;
		long endTime2 = startTime2 + elapsedTime2;
		fooStats.incrAndRemovePending(startTime2, startTime2, startTime2,
				endTime2, endTime2, endTime2);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertStats(2, elapsedTime1+elapsedTime2, fooChange);
	}

	@Test
	public void testMarkAndCollectChanges_fooAdd1_barAdd1_fooAdd2_chg_fooBarRemove1_chg_fooRemove2_chg() {
		// Prepare  
		// fooAdd1_barAdd1_fooAdd2
		long startTime1 = 123L;
		fooStats.addPending(startTime1);
		long startTimeBar1 = 124L;
		fooBarStats.addPending(startTimeBar1);
		long startTime2 = 234L;
		fooStats.addPending(startTime2);
		// src status= 1->foo/bar, 2->foo
		// Perform
		Map<String, PerfStats> changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());
		
		// Prepare fooBarRemove1_chg
		long fooBarElapsedTime1 = 1L;
		long fooBarEndTime1 = startTime1 + fooBarElapsedTime1;
		fooBarStats.incrAndRemovePending(startTime1, startTime1, startTime1,
				fooBarEndTime1, fooBarEndTime1, fooBarEndTime1);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		PerfStats fooBarChange = changes.get("foo/bar");
		assertStats(1, fooBarElapsedTime1, fooBarChange);
		
		// Prepare fooRemove2_chg
		long elapsedTime2 = 234L;
		long endTime2 = startTime2 + elapsedTime2;
		fooStats.incrAndRemovePending(startTime2, startTime2, startTime2,
				endTime2, endTime2, endTime2);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		PerfStats fooChange = changes.get("foo");
		assertStats(1, elapsedTime2, fooChange);

		// Prepare fooRemove1_chg
		long elapsedTime1 = 123L;
		long endTime1 = startTime1 + elapsedTime1;
		fooStats.incrAndRemovePending(startTime1, startTime1, startTime1,
				endTime1, endTime1, endTime1);
		// Perform
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(1, changes.size());
		fooChange = changes.get("foo");
		assertStats(2, elapsedTime1+elapsedTime2, fooChange);
	}


	@Test
	public void testMarkAndCollectChanges_fooAdd1_barAdd1_fooAdd2_chg_fooBarRemove1_fooRemove2_fooRemove1_chg() {
		// Prepare  
		// fooAdd1_barAdd1_fooAdd2_chg
		long startTime1 = 123L;
		fooStats.addPending(startTime1);
		long startTimeBar1 = 124L;
		fooBarStats.addPending(startTimeBar1);
		long startTime2 = 234L;
		fooStats.addPending(startTime2);
		// src status= 1->foo/bar, 2->foo
		// Perform
		Map<String, PerfStats> changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(0, changes.size());
		
		// Prepare fooBarRemove1_fooRemove2_fooRemove1_chg
		long fooBarElapsedTime1 = 1L;
		long fooBarEndTime1 = startTime1 + fooBarElapsedTime1;
		fooBarStats.incrAndRemovePending(startTime1, startTime1, startTime1,
				fooBarEndTime1, fooBarEndTime1, fooBarEndTime1);

		long fooElapsedTime1 = 1L;
		long fooEndTime1 = startTime1 + fooElapsedTime1;
		fooStats.incrAndRemovePending(startTime1, startTime1, startTime1,
				fooEndTime1, fooEndTime1, fooEndTime1);

		long fooElapsedTime2 = 234L;
		long fooEndTime2 = startTime2 + fooElapsedTime2;
		fooStats.incrAndRemovePending(startTime2, startTime2, startTime2,
				fooEndTime2, fooEndTime2, fooEndTime2);

		// Perform
		// src  status=                     (foo.pendingCount=0, foo/bar.pendingCount=0)
		// prev status= 1->foo/bar, 2->foo  (foo.pendingCount=2, foo/bar.pendingCount=1)
		changes = sut.markAndCollectChanges();
		// Post-check
		Assert.assertEquals(2, changes.size());
		PerfStats fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		PerfStats  fooBarChange = changes.get("foo/bar");
		Assert.assertNotNull(fooBarChange);
		assertStats(2, fooElapsedTime1+fooElapsedTime2, fooChange);
		assertStats(1, fooBarElapsedTime1, fooBarChange);		
	}

	
	private static void assertStats(int expectedCount, long expectedSumElapsedTime, PerfStats actual) {
		Assert.assertEquals(expectedCount, actual.getElapsedTimeStats().cumulatedCount());
		Assert.assertEquals(expectedSumElapsedTime, actual.getElapsedTimeStats().cumulatedSum());
	}

}
