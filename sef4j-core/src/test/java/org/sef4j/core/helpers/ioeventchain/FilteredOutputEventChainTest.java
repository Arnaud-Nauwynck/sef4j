package org.sef4j.core.helpers.ioeventchain;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.core.MockEvent;
import org.sef4j.core.MockEvent.MockEventValueContainsPredicate;
import org.sef4j.core.api.ioeventchain.OutputEventChain.SenderHandle;


public class FilteredOutputEventChainTest {

	MockOutputEventChain mockUnderlying = new MockOutputEventChain();
	FilteredOutputEventChain<MockEvent> sut = new FilteredOutputEventChain<MockEvent>(null, "test", 
			mockUnderlying, MockEventValueContainsPredicate.CONTAINS_1);

	@Test
	public void testRegisterSender_sendEvent_unregisterSender() {
		SenderHandle<MockEvent> sender = sut.registerSender();
		sender.sendEvent(MockEvent.E1);
		sender.sendEvent(MockEvent.E2);
		sut.unregisterSender(sender);
		
		List<MockEvent> ls = mockUnderlying.clearAndGet();
		Assert.assertEquals(1, ls.size());
		Assert.assertSame(MockEvent.E1, ls.get(0));
	}

}
