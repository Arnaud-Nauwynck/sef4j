package org.sef4j.core.helpers.appenders;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class InMemoryEventSenderTest {

    private static class E {}
	private InMemoryEventSender<E> sut;

	@Before
	public void setup() {
		sut = new InMemoryEventSender<E>();
	}

	@Test
	public void testHandleEvent() {
		// Prepare
		E event0 = new E();
		E event1 = new E();
		// Perform
		sut.sendEvent(event0);
		sut.sendEvent(event1);
		// Post-check
		List<E> res = sut.clearAndGet();
		Assert.assertEquals(2, res.size());
		Assert.assertSame(event0, res.get(0));
		Assert.assertSame(event1, res.get(1));
	}

}
