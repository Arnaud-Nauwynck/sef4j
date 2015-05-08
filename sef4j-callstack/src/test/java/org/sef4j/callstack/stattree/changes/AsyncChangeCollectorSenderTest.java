package org.sef4j.callstack.stattree.changes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.core.helpers.export.senders.ChangeEventSenderTask;
import org.sef4j.core.helpers.export.senders.EventSenderFragmentsExporter;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.helpers.senders.InMemoryEventSender;
import org.sef4j.core.helpers.tasks.PeriodicTask;


public class AsyncChangeCollectorSenderTest {

	private PropTreeNode rootNode = PropTreeNode.newRoot();
	private PropTreeNode fooNode = rootNode.getOrCreateChild("foo");
	private PropTreeNode fooBarNode = fooNode.getOrCreateChild("bar");

	private PerfStats fooStats = fooNode.getOrCreateProp("stats", PerfStats.FACTORY);
	private PerfStats fooBarStats = fooBarNode.getOrCreateProp("stats", PerfStats.FACTORY);

	private BasicStatIgnorePendingChangeCollector changeCollector = 
			new BasicStatIgnorePendingChangeCollector(rootNode);
	private InMemoryEventSender<PerfStatsChangesEvent> inMemoryEventSender = new InMemoryEventSender<PerfStatsChangesEvent>();
	
	private ChangeEventSenderTask<PerfStats,PerfStatsChangesEvent> sut = 
			new ChangeEventSenderTask<PerfStats,PerfStatsChangesEvent>(
					new PeriodicTask.Builder().withPeriod(1), // period=1 second 
					new PeriodicTask.Builder().withPeriod(600),
					new EventSenderFragmentsExporter<PerfStats,PerfStatsChangesEvent>(
							"", 
							Arrays.asList(changeCollector),
							PerfStatsChangesEvent.FACTORY,
							inMemoryEventSender));
	
	@Test
	public void testStartStop() throws Exception {
		// Prepare
		// Perform
		sut.getSendAllPeriodicTask().start();

		long fooBarElapsedTime1 = 12L;
		long fooElapsedTime1 = 13L;
		{
			long startTime1 = 123L;
			fooStats.addPending(startTime1);

			{
				long startTimeBar1 = 124L;
				fooBarStats.addPending(startTimeBar1);

				long fooBarEndTime1 = startTime1 + fooBarElapsedTime1;
				fooBarStats.incrAndRemovePending(startTime1, startTime1, startTime1,
						fooBarEndTime1, fooBarEndTime1, fooBarEndTime1);
			}

			long endTime1 = startTime1 + fooElapsedTime1;
			fooStats.incrAndRemovePending(startTime1, startTime1, startTime1,
					endTime1, endTime1, endTime1);
		}
		
		Thread.sleep(1500);
		sut.getSendAllPeriodicTask().stop();
		
		// sut.flush();
		// Post-check
		List<PerfStatsChangesEvent> events = inMemoryEventSender.clearAndGet();
		Assert.assertTrue(events.size() >= 1);
		PerfStatsChangesEvent event0 = events.get(0);
		Map<?, PerfStats> changes = event0.getChanges();
		Assert.assertNotNull(changes);
		PerfStats fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertStats(1, fooElapsedTime1, fooChange);
		PerfStats fooBarChange = changes.get("foo/bar");
		Assert.assertNotNull(fooBarChange);
		assertStats(1, fooBarElapsedTime1, fooBarChange);
	}

	private static void assertStats(int expectedCount, long expectedSumElapsedTime, PerfStats actual) {
		Assert.assertEquals(expectedCount, actual.getElapsedTimeStats().cumulatedCount());
		Assert.assertEquals(expectedSumElapsedTime, actual.getElapsedTimeStats().cumulatedSum());
	}

}
