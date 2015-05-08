package org.sef4j.core.helpers.ioeventchain;

import org.junit.Test;
import org.sef4j.core.MockEvent;
import org.sef4j.core.MockEvent.MockEventValueContainsPredicate;
import org.sef4j.core.api.ioeventchain.InputEventChain.ListenerHandle;
import org.sef4j.core.helpers.senders.InMemoryEventSender;
import org.sef4j.core.util.factorydef.ObjectByDefRepository.ObjectWithHandle;


public class FilteredInputEventChainTest {

	InMemoryEventSender<MockEvent> mockResult = new InMemoryEventSender<MockEvent>();
	ObjectWithHandle<MockInputEventChain> mockUnderlyingWithHandle = 
			MockInputEventChain.newMockObjectWithHandle();
	MockInputEventChain mockUnderlying = (MockInputEventChain) mockUnderlyingWithHandle.getObject();
	FilteredInputEventChain<MockEvent> sut = new FilteredInputEventChain<MockEvent>(null, "test", 
			mockUnderlyingWithHandle,
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
