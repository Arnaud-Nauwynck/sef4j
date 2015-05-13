package org.sef4j.core.helpers.ioeventchain;

import java.util.List;

import org.sef4j.core.MockEvent;
import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.ioeventchain.OutputEventChain;
import org.sef4j.core.helpers.senders.InMemoryEventSender;

public class MockOutputEventChain extends OutputEventChain<MockEvent> {

	protected InMemoryEventSender<MockEvent> innerSender = new InMemoryEventSender<MockEvent>();
	protected boolean started = true;
	
	public MockOutputEventChain() {
		super("mock");
	}

	public List<MockEvent> clearAndGet() {
		return innerSender.clearAndGet();
	}

	@Override
	protected EventSender<MockEvent> getInnerEventSender() {
		return innerSender;
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
