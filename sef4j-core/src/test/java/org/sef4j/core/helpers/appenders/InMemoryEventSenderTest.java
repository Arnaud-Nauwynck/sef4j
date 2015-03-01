package org.sef4j.core.helpers.appenders;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class InMemoryEventSenderTest {

	private InMemoryEventSender sut;

	@Before
	public void setup() {
		sut = new InMemoryEventSender();
	}

	@Test
	public void testHandleEvent() {
		// Prepare
		Object event0 = new Object();
		Object event1 = new Object();
		// Perform
		sut.sendEvent(event0);
		sut.sendEvent(event1);
		// Post-check
		List<Object> res = sut.clearAndGet();
		Assert.assertEquals(2, res.size());
		Assert.assertSame(event0, res.get(0));
		Assert.assertSame(event1, res.get(1));
	}

}
