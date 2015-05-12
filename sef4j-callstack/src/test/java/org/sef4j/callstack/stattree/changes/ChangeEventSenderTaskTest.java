package org.sef4j.callstack.stattree.changes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.core.helpers.export.ExportFragment;
import org.sef4j.core.helpers.export.ExportFragmentList;
import org.sef4j.core.helpers.export.senders.ExportFragmentsPollingEventProvider;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.helpers.senders.InMemoryEventSender;


public class ChangeEventSenderTaskTest {

	private PropTreeNode rootNode = PropTreeNode.newRoot();
	private PropTreeNode fooNode = rootNode.getOrCreateChild("foo");
	private PropTreeNode fooBarNode = fooNode.getOrCreateChild("bar");

	private PerfStats fooStats = fooNode.getOrCreateProp("stats", PerfStats.FACTORY);
	private PerfStats fooBarStats = fooBarNode.getOrCreateProp("stats", PerfStats.FACTORY);

	private BasicStatIgnorePendingChangeCollector perfStatsChangeCollector = 
			new BasicStatIgnorePendingChangeCollector(rootNode);
	private InMemoryEventSender<ExportFragmentList<PerfStats>> inMemoryEventSender = new InMemoryEventSender<ExportFragmentList<PerfStats>>();

	private ExportFragmentsPollingEventProvider<PerfStats> sut = 
			new ExportFragmentsPollingEventProvider<PerfStats>("test",
							Arrays.asList(perfStatsChangeCollector));
	
	@Before
	public void setup() {
		sut.addEventListener(inMemoryEventSender);
	}
	
	@Test
	public void testStartStop() throws Exception {
		// Prepare
		// Perform
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
		
		sut.poll();
		// Post-check
		List<ExportFragmentList<PerfStats>> events = inMemoryEventSender.clearAndGet();
		Assert.assertTrue(events.size() >= 1);
		Map<?, ExportFragment<PerfStats>> changes = events.get(0).getIdentifiableFragments();
		Assert.assertNotNull(changes);
		PerfStats fooChange = changes.get("foo").getValue();
		Assert.assertNotNull(fooChange);
		assertStats(1, fooElapsedTime1, fooChange);
		PerfStats fooBarChange = changes.get("foo/bar").getValue();
		Assert.assertNotNull(fooBarChange);
		assertStats(1, fooBarElapsedTime1, fooBarChange);
	}

	private static void assertStats(int expectedCount, long expectedSumElapsedTime, PerfStats actual) {
		Assert.assertEquals(expectedCount, actual.getElapsedTimeStats().cumulatedCount());
		Assert.assertEquals(expectedSumElapsedTime, actual.getElapsedTimeStats().cumulatedSum());
	}

}
