package org.sef4j.core.helpers.senders;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.core.MockEvent;
import org.sef4j.core.helpers.senders.AbstractTransformerEventSender.FuncTransformEventSender;


public class AbstractTransformerEventSenderTest {

	private static class F {
		public final MockEvent wrapped;

		public F(MockEvent wrapped) {
			this.wrapped = wrapped;
		}
	}
	
	protected Function<MockEvent,F> transform = (x) -> new F(x);
	protected InMemoryEventSender<F> targetEventSender = new InMemoryEventSender<F>();
	protected FuncTransformEventSender<MockEvent,F> sut = new FuncTransformEventSender<MockEvent,F>(targetEventSender, transform);
	
	MockEvent e1 = new MockEvent("event1");
	MockEvent e2 = new MockEvent("event2");
	
	@Test
	public void testSendEvent() {
		// Prepare
		// Perform
		sut.sendEvent(e1);
		// Post-check
		List<F> transformedEvents = targetEventSender.clearAndGet();
		Assert.assertEquals(1, transformedEvents.size());
		Assert.assertTrue(transformedEvents.get(0) instanceof F);
		Assert.assertTrue(((F) transformedEvents.get(0)).wrapped == e1);
	}

	@Test
	public void testSendEvents() {
		// Prepare
		// Perform
		sut.sendEvents(Arrays.asList(e1, e2));
		// Post-check
		List<F> transformedEvents = targetEventSender.clearAndGet();
		Assert.assertEquals(2, transformedEvents.size());
		Assert.assertSame(((F) transformedEvents.get(0)).wrapped, e1);
		Assert.assertSame(((F) transformedEvents.get(1)).wrapped, e2);
	}
	
}
