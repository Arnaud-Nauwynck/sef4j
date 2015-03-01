package org.sef4j.callstack.event.impl;

import java.util.Collection;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.sef4j.callstack.event.StackEvent;
import org.sef4j.callstack.event.StackEvent.CompoundPopPushStackEvent;
import org.sef4j.callstack.event.StackEventListener;
import org.sef4j.core.helpers.AsyncUtils;

/**
 * listener of StackEvent, that bufferize events 
 * and asynchronously (default: every 15 seconds) flush compound event to target EventLogger
 */
public class AsyncCompoundPushPopSenderListener extends StackEventListener {

	private ScheduledExecutorService scheduledExecutor;
	private long period;
	
	private CompoundPopPushStackEventBuilder compoundEventBuffer;
	private CompoundPushPopEventToEventListenerTask task;
	private ScheduledFuture<Void> scheduledTask;
	
	// ------------------------------------------------------------------------

	public AsyncCompoundPushPopSenderListener(StackEventListener targetEventListener) {
		this(AsyncUtils.defaultScheduledThreadPool(), 15, targetEventListener);
	}
	
	public AsyncCompoundPushPopSenderListener(ScheduledExecutorService scheduledExecutor, long period,
			StackEventListener targetEventListener) {
		this.scheduledExecutor = scheduledExecutor;
		this.period = period;
		this.compoundEventBuffer = new CompoundPopPushStackEventBuilder();
		this.task = new CompoundPushPopEventToEventListenerTask(compoundEventBuffer, targetEventListener);
	}
	
	// ------------------------------------------------------------------------

	@Override
	public void onEvent(StackEvent event) {
		event.visit(compoundEventBuffer);
	}

	@Override
	public void onEvents(Collection<StackEvent> events) {
		for(StackEvent e : events) {
			e.visit(compoundEventBuffer);
		}
	}

	/** flush compound events without waiting next scheduled period */
	public void flush() {
		task.run();
	}
	
	// start/stop life-cycle management
	// ------------------------------------------------------------------------
	
	public boolean isStarted() {
		return scheduledTask != null;
	}

	@SuppressWarnings("unchecked")
	public void start() {
		if (this.scheduledTask != null) {
			return; // already started
		}
		this.scheduledTask = (ScheduledFuture<Void>) 
				scheduledExecutor.scheduleAtFixedRate(task, 0, period, TimeUnit.SECONDS);
	}
	
	public void stop() {
		if (this.scheduledTask == null) {
			return; // already stopped
		}
		ScheduledFuture<Void> tmp = this.scheduledTask;
		this.scheduledTask = null;
		tmp.cancel(false);
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * task for taking CompoundStackEvent from buffer, and sending to targetListener
	 */
	public static class CompoundPushPopEventToEventListenerTask implements Runnable {

		private CompoundPopPushStackEventBuilder compoundEventBuffer;

		private StackEventListener targetEventListener;
		
		// ------------------------------------------------------------------------
		
		public CompoundPushPopEventToEventListenerTask(CompoundPopPushStackEventBuilder compoundEventBuffer,
				StackEventListener targetEventListener) {
			this.compoundEventBuffer = compoundEventBuffer;
			this.targetEventListener = targetEventListener;
		}

		// ------------------------------------------------------------------------

		@Override
		public void run() {
			CompoundPopPushStackEvent eventOrNull = compoundEventBuffer.clearAndBuildOrNull();
			if (eventOrNull != null) {
				targetEventListener.onEvent(eventOrNull);
			}
		}
		
	}

}
