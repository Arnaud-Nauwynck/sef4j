package org.sef4j.core.helpers.senders;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.core.helpers.senders.AbstractTransformerEventSender.FuncTransformEventSender;


public class AbstractTransformerEventSenderTest {

	private static class E {
		public final String value;

		public E(String value) {
			this.value = value;
		}
	}
	private static class F {
		public final E wrapped;

		public F(E wrapped) {
			this.wrapped = wrapped;
		}
	}
	
	protected Function<E,F> transform = (x) -> new F(x);
	protected InMemoryEventSender<F> targetEventSender = new InMemoryEventSender<F>();
	protected FuncTransformEventSender<E,F> sut = new FuncTransformEventSender<E,F>(targetEventSender, transform);
	
	E e1 = new E("event1");
	E e2 = new E("event2");
	
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
