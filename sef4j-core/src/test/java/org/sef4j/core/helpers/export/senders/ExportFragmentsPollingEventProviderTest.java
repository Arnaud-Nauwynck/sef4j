package org.sef4j.core.helpers.export.senders;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sef4j.core.helpers.export.ExportFragment;
import org.sef4j.core.helpers.export.ExportFragmentList;
import org.sef4j.core.helpers.proptree.DummyCount;
import org.sef4j.core.helpers.proptree.changes.DummyCountChangeCollector;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.helpers.senders.InMemoryEventSender;


public class ExportFragmentsPollingEventProviderTest {

	private PropTreeNode rootNode = PropTreeNode.newRoot();
	private PropTreeNode fooNode = rootNode.getOrCreateChild("foo");
	private PropTreeNode fooBarNode = fooNode.getOrCreateChild("bar");

	private DummyCount fooCount = fooNode.getOrCreateProp("dummyCount", DummyCount.FACTORY);
	private DummyCount fooBarCount = fooBarNode.getOrCreateProp("dummyCount", DummyCount.FACTORY);

	private DummyCountChangeCollector dummyCountChangeCollector = new DummyCountChangeCollector(rootNode);
	private InMemoryEventSender<ExportFragmentList<DummyCount>> inMemoryEventSender = new InMemoryEventSender<ExportFragmentList<DummyCount>>();
	
	private ExportFragmentsPollingEventProvider<DummyCount> sut = 
			new ExportFragmentsPollingEventProvider<DummyCount>("test",
					Arrays.asList(dummyCountChangeCollector));
	
	@Before
	public void setup() {
		sut.addEventListener(inMemoryEventSender);
	}
	
	@Test
	public void testPoll() throws Exception {
		// Prepare
		fooCount.incrCount1();
		fooBarCount.incrCount2();
		// Perform
		sut.poll();
		// Post-check
		List<ExportFragmentList<DummyCount>> events = inMemoryEventSender.clearAndGet();
		Assert.assertTrue(events.size() >= 1);
		ExportFragmentList<DummyCount> event0 = events.get(0);
		Map<?,ExportFragment<DummyCount>> changes = event0.getIdentifiableFragments();
		Assert.assertNotNull(changes);
		DummyCount fooChange = changes.get("foo").getValue();
		Assert.assertNotNull(fooChange);
		assertCount(1, 0, fooChange);
		DummyCount fooBarChange = changes.get("foo/bar").getValue();
		Assert.assertNotNull(fooBarChange);
		assertCount(0, 1, fooBarChange);
	}

	private static void assertCount(int expectedCount1, long expectedCount2, DummyCount actual) {
		Assert.assertEquals(expectedCount1, actual.getCount1());
		Assert.assertEquals(expectedCount2, actual.getCount2());
	}

}
