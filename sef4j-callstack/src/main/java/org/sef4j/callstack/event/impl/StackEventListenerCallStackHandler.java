package org.sef4j.callstack.event.impl;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.CallStackPushPopHandler;
import org.sef4j.callstack.event.StackEvent.PopStackEvent;
import org.sef4j.callstack.event.StackEvent.ProgressStepStackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;
import org.sef4j.callstack.event.StackEventListener;

/**
 * adapter CallStackPushPopHandler -> EventLogger with StackEvent
 *   onPush() => sendEvent(PushStackEvent), onPop() => sendEvent(PopStackEvent), ...
 */
public class StackEventListenerCallStackHandler extends CallStackPushPopHandler {

	private StackEventListener targetEventListener;
	
	// ------------------------------------------------------------------------
	
	public StackEventListenerCallStackHandler(StackEventListener targetEventListener) {
		this.targetEventListener = targetEventListener;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public void onPush(CallStackElt stackElt) {
		PushStackEvent event = new PushStackEvent(stackElt);
		
		targetEventListener.onEvent(event);
	
		stackElt.onPushAddCallStackPushPopHandler(this);
	}

	@Override
	public void onPop(CallStackElt stackElt) {
		PopStackEvent event = new PopStackEvent(stackElt);
		
		targetEventListener.onEvent(event);
	}

	@Override
	public void onProgressStep(CallStackElt stackElt) {
		ProgressStepStackEvent event = new ProgressStepStackEvent(stackElt);
		
		targetEventListener.onEvent(event);
	}

}
