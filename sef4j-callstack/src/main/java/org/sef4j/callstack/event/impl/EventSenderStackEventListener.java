package org.sef4j.callstack.event.impl;

import org.sef4j.callstack.event.StackEvent;
import org.sef4j.callstack.event.StackEventListener;
import org.sef4j.core.api.EventLogger;

/**
 * adapter for StackEventListener -> EventLogger
 * (downcast typed StackEvent into untyped Object event)
 */
public class EventLoggerStackEventListener extends StackEventListener {

	private EventLogger targetEventLogger;
	
	// ------------------------------------------------------------------------
	
	public EventLoggerStackEventListener(EventLogger targetEventLogger) {
		this.targetEventLogger = targetEventLogger;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public void onEvent(StackEvent event) {
		targetEventLogger.sendEvent(event);
	}

}
