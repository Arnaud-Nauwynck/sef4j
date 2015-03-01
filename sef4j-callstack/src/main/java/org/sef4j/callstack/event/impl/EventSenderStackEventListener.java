package org.sef4j.callstack.event.impl;

import org.sef4j.callstack.event.StackEvent;
import org.sef4j.callstack.event.StackEventListener;
import org.sef4j.core.api.EventSender;

/**
 * adapter for StackEventListener -> EventSender
 * (downcast typed StackEvent into untyped Object event)
 */
public class EventSenderStackEventListener extends StackEventListener {

	private EventSender targetEventSender;
	
	// ------------------------------------------------------------------------
	
	public EventSenderStackEventListener(EventSender targetEventSender) {
		this.targetEventSender = targetEventSender;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public void onEvent(StackEvent event) {
		targetEventSender.sendEvent(event);
	}

}
