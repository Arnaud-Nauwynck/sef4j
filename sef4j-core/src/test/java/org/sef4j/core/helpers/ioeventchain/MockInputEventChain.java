package org.sef4j.core.helpers.ioeventchain;

import java.util.Collection;

import org.sef4j.core.MockEvent;
import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.util.factorydef.ObjectByDefRepository.ObjectWithHandle;

public class MockInputEventChain extends InputEventChain<MockEvent> {

	protected boolean started = true;
	
	public MockInputEventChain() {
		super(null, "mock");
	}

	public static ObjectWithHandle<MockInputEventChain> newMockObjectWithHandle() {
		MockInputEventChain mock = new MockInputEventChain();
		return new ObjectWithHandle<MockInputEventChain>(null, null, mock);
	}
	
	public void sendEvent(MockEvent event) {
		innerEventProvider.sendEvent(event);
	}

	public void sendEvents(Collection<MockEvent> events) {
		innerEventProvider.sendEvents(events);
	}
	
	@Override
	public boolean isStarted() {
		return started;
	}

	@Override
	public void start() {
		this.started = true;
	}

	@Override
	public void stop() {
		this.started = false;
	}
	
}
