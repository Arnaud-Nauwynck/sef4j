package org.sef4j.core.helpers.ioeventchain;

import org.junit.Test;
import org.sef4j.core.MockEvent;
import org.sef4j.core.MockEvent.MockEventValueContainsPredicate;
import org.sef4j.core.MockEventSender;
import org.sef4j.core.api.ioeventchain.InputEventChain.ListenerHandle;


public class FilterInputEventChainTest {

	MockEventSender<MockEvent> mockResult = new MockEventSender<MockEvent>();

	MockInputEventChain mockUnderlying = new MockInputEventChain();
	FilterInputEventChain<MockEvent> sut = new FilterInputEventChain<MockEvent>("test", 
			mockUnderlying,
			MockEventValueContainsPredicate.CONTAINS_1);

	@Test
	public void testRegisterEventListener_underlyingSendEvent_unregisterEventListener() {	
		// Prepare
		ListenerHandle<MockEvent> subscr = sut.registerEventListener(mockResult);
		// Perform
		mockUnderlying.sendEvent(MockEvent.E1);
		mockUnderlying.sendEvent(MockEvent.E2);
		
		sut.unregisterEventListener(subscr);
		// Post-check
		// receive events: calls myEventCallback.sendEvents(..)
		mockResult.assertSameClearAndGet(MockEvent.E1);

		// Perform
		mockUnderlying.sendEvent(MockEvent.E1);
		// Post-check
		mockResult.assertSameClearAndGet();
	}

}
