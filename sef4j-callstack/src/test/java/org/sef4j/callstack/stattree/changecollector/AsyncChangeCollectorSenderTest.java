package org.sef4j.callstack.stattree.changecollector;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stattree.CallTreeNode;
import org.sef4j.core.helpers.AsyncUtils;
import org.sef4j.core.helpers.appenders.InMemoryEventSender;


public class AsyncChangeCollectorSenderTest {

	private CallTreeNode rootNode = CallTreeNode.newRoot();
	private CallTreeNode fooNode = rootNode.getOrCreateChild("foo");
	private CallTreeNode fooBarNode = fooNode.getOrCreateChild("bar");

	private PerfStats fooStats = fooNode.getStats();
	private PerfStats fooBarStats = fooBarNode.getStats();

	private BasicStatIgnorePendingChangeCollector changeCollector = 
			new BasicStatIgnorePendingChangeCollector(rootNode);
	private InMemoryEventSender inMemoryEventSender = new InMemoryEventSender();
	
	private AsyncChangeCollectorSender<PerfStats,PerfStatsChangesEvent> sut = 
			new AsyncChangeCollectorSender<PerfStats,PerfStatsChangesEvent>(
					AsyncUtils.defaultScheduledThreadPool(), 1, // period=1 second 
					changeCollector,
					PerfStatsChangesEvent.FACTORY,
					inMemoryEventSender);
	
	@Test
	public void testStartStop() throws Exception {
		// Prepare
		// Perform
		sut.start();

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
		sut.stop();
		// sut.flush();
		// Post-check
		List<Object> events = inMemoryEventSender.clearAndGet();
		Assert.assertTrue(events.size() >= 1);
		PerfStatsChangesEvent event0 = (PerfStatsChangesEvent) events.get(0);
		Map<String, PerfStats> changes = event0.getChanges();
		Assert.assertNotNull(changes);
		PerfStats fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertStats(1, fooElapsedTime1, fooChange);
		PerfStats fooBarChange = changes.get("foo/bar");
		Assert.assertNotNull(fooBarChange);
		assertStats(1, fooBarElapsedTime1, fooBarChange);
	}

	private static void assertStats(int expectedCount, long expectedSumElapsedTime, PerfStats actual) {
		Assert.assertEquals(expectedCount, actual.getElapsedTimeStats().getSlotsCount());
		Assert.assertEquals(expectedSumElapsedTime, actual.getElapsedTimeStats().getSlotsSum());
	}

}
