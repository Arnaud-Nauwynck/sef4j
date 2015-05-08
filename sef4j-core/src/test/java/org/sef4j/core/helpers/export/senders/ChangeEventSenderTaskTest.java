package org.sef4j.core.helpers.export.senders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.core.helpers.proptree.DummyCount;
import org.sef4j.core.helpers.proptree.changes.DummyCountChangeCollector;
import org.sef4j.core.helpers.proptree.changes.DummyCountChangesEvent;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.helpers.senders.InMemoryEventSender;
import org.sef4j.core.helpers.tasks.PeriodicTask;


public class ChangeEventSenderTaskTest {

	private PropTreeNode rootNode = PropTreeNode.newRoot();
	private PropTreeNode fooNode = rootNode.getOrCreateChild("foo");
	private PropTreeNode fooBarNode = fooNode.getOrCreateChild("bar");

	private DummyCount fooCount = fooNode.getOrCreateProp("dummyCount", DummyCount.FACTORY);
	private DummyCount fooBarCount = fooBarNode.getOrCreateProp("dummyCount", DummyCount.FACTORY);

	private DummyCountChangeCollector changeCollector = new DummyCountChangeCollector(rootNode);
	private InMemoryEventSender<DummyCountChangesEvent> inMemoryEventSender = new InMemoryEventSender<DummyCountChangesEvent>();
	
	private ChangeEventSenderTask<DummyCount,DummyCountChangesEvent> sut = 
			new ChangeEventSenderTask<DummyCount,DummyCountChangesEvent>(
					new PeriodicTask.Builder().withPeriod(1), // period=1 second 
					new PeriodicTask.Builder().withPeriod(600),
					new EventSenderFragmentsExporter<DummyCount,DummyCountChangesEvent>("", 
						Arrays.asList(changeCollector),
						DummyCountChangesEvent.FACTORY,
						inMemoryEventSender));
	
	@Test
	public void testStartStop() throws Exception {
		// Prepare
		// Perform
		sut.getSendAllPeriodicTask().start();

		fooCount.incrCount1();
		fooBarCount.incrCount2();
		
		Thread.sleep(1500);
		sut.getSendAllPeriodicTask().stop();
		// sut.flush();
		// Post-check
		List<DummyCountChangesEvent> events = inMemoryEventSender.clearAndGet();
		Assert.assertTrue(events.size() >= 1);
		DummyCountChangesEvent event0 = events.get(0);
		Map<?,DummyCount> changes = event0.getChanges();
		Assert.assertNotNull(changes);
		DummyCount fooChange = changes.get("foo");
		Assert.assertNotNull(fooChange);
		assertCount(1, 0, fooChange);
		DummyCount fooBarChange = changes.get("foo/bar");
		Assert.assertNotNull(fooBarChange);
		assertCount(0, 1, fooBarChange);
	}

	private static void assertCount(int expectedCount1, long expectedCount2, DummyCount actual) {
		Assert.assertEquals(expectedCount1, actual.getCount1());
		Assert.assertEquals(expectedCount2, actual.getCount2());
	}

}
