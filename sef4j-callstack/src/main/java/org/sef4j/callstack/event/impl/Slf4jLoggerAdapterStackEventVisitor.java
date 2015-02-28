package org.sef4j.callstack.event.impl;

import java.util.Map;

import org.sef4j.callstack.event.StackEvent.CompoundPopPushStackEvent;
import org.sef4j.callstack.event.StackEvent.PopStackEvent;
import org.sef4j.callstack.event.StackEvent.ProgressStepStackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;
import org.sef4j.callstack.event.StackEventVisitor;
import org.sef4j.callstack.handlers.Slf4jLoggerAdapterCallStackHandler;
import org.slf4j.Logger;

/**
 * adapter StackEventVisitor (Listener) PushEvent/PopEvent -> Slf4j
 * 
 * cf delegate message formatter methods: Slf4jLoggerAdapterCallStackHandler
 * (CallStackHandler -> Slf4j)
 */
public class Slf4jLoggerAdapterStackEventVisitor extends StackEventVisitor {

	private Logger slf4jLogger;
	
	// ------------------------------------------------------------------------
	
	public Slf4jLoggerAdapterStackEventVisitor(Logger slf4jLogger) {
		this.slf4jLogger = slf4jLogger;
	}

	// ------------------------------------------------------------------------

	@Override
	public void acceptPushStackEvent(PushStackEvent pushStackEvent) {
		String name = pushStackEvent.getName();
		Map<String, Object> inheritedProps = pushStackEvent.getInheritedProps();
		Map<String, Object> params = pushStackEvent.getParams();
		int progressExpectedCount = pushStackEvent.getProgressExpectedCount();

		String msg = Slf4jLoggerAdapterCallStackHandler.formatLogMessagePush(name, 
				inheritedProps, params, progressExpectedCount);

		slf4jLogger.info(msg);
	}

	@Override
	public void acceptPopStackEvent(PopStackEvent popStackEvent) {
		String name = popStackEvent.getName();
		long elapsedTime = popStackEvent.getElapsedTime();

		String msg = Slf4jLoggerAdapterCallStackHandler.formatLogMessagePop(name, 
				elapsedTime);

		slf4jLogger.info(msg);
	}

	@Override
	public void acceptProgressStackEvent(ProgressStepStackEvent progressStackEvent) {
		int progressIndex = progressStackEvent.getProgressIndex();
		int progressExpectedCount = progressStackEvent.getProgressExpectedCount();
		String progressMessage = progressStackEvent.getProgressMessage();
		String msg = Slf4jLoggerAdapterCallStackHandler.formatLogMessageProgress(
				progressIndex, progressExpectedCount, progressMessage);
		
		slf4jLogger.info(msg);
	}

	@Override
	public void acceptCompoundStackEvent(CompoundPopPushStackEvent compoundStackEvent) {
		final PopStackEvent[] popEvents = compoundStackEvent.getPopEvents();
		final PushStackEvent[] pushEvents = compoundStackEvent.getPushedEvents();
		final ProgressStepStackEvent[] progressSteps = compoundStackEvent.getLastProgressStepEvents();

		String msg = Slf4jLoggerAdapterCallStackHandler.formatLogMessageCompound(popEvents, pushEvents, progressSteps);
	
		slf4jLogger.info(msg);
	}

}
