package org.sef4j.callstack.stattree.changecollector;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stattree.CallTreeNode;
import org.sef4j.core.api.EventSender;
import org.sef4j.core.helpers.AsyncUtils;

/**
 * @Deprecated TODO ... 
 * see combination of PeriodicTask + FragmentsProvidersExporter + CallTreeValueChangeExportFragmentsProvider
 * => transform class to builder utility helper
 * 
 * 
 * periodic collector of tree node change, to publish to EventSender
 * <p/>
 * 
 * typical usage:<BR/>
 * publish pending counts changes per CallTreeNode every 30 secondes<BR/>
 * and publish PerStats statistics changes every 5 minutes.<BR/>
 * As such, a typical EventSender backend will be a Http POST json message publisher for InfluxDB/Graphite
 * 
 * @param T: template type for value change, example: PerfStats, PendingCount
 * @param E: template type for change event, example: PerfStatsChangesEvent, PendingCountChangesEvent
 */
public class AsyncChangeCollectorSender<T,E> {
	
	private ScheduledExecutorService scheduledExecutor;
	private long period;
	private ScheduledFuture<Void> scheduledTask;

	private ChangeCollectorToEventSenderTask<T,E> task;
	
	// ------------------------------------------------------------------------

	public AsyncChangeCollectorSender(
			ScheduledExecutorService scheduledExecutor, long period,
			AbstractCallTreeValueChangeCollector<T> changeCollector,
			Function<Map<String,T>,E> changesToEventBuilder,
			EventSender<E> targetEventSender) {
		this(scheduledExecutor, period, 
				new ChangeCollectorToEventSenderTask<T,E>(
						changeCollector, changesToEventBuilder, targetEventSender));
	}
	
	public AsyncChangeCollectorSender(
			ScheduledExecutorService scheduledExecutor, long period,
			ChangeCollectorToEventSenderTask<T,E> task) {
		this.scheduledExecutor = scheduledExecutor;
		this.period = period;
		this.task = task;
	}

	public static class Builder<T,E> {
		public static final int DEFAULT_PERIOD_SECONDS = 3*60;

		private ScheduledExecutorService scheduledExecutor;
		private long period = DEFAULT_PERIOD_SECONDS;
		private ChangeCollectorToEventSenderTask<T,E> task;
		private AbstractCallTreeValueChangeCollector<T> taskChangeCollector;
		private Function<Map<String,T>,E> taskChangesToEventBuilder;
		private EventSender<E> taskTargetEventSender;

		public AsyncChangeCollectorSender<T,E> build() {
			if (task == null) {
				task = new ChangeCollectorToEventSenderTask<T,E>(taskChangeCollector, taskChangesToEventBuilder, taskTargetEventSender);
			}
			return new AsyncChangeCollectorSender<T,E>(scheduledExecutor, period, task);
		}

		public Builder<T,E> withScheduledExecutor(ScheduledExecutorService scheduledExecutor) {
			this.scheduledExecutor = scheduledExecutor;
			return this;
		}

		public Builder<T,E> withPeriod(long period) {
			this.period = period;
			return this;
		}

		public Builder<T,E> withTask(ChangeCollectorToEventSenderTask<T, E> task) {
			this.task = task;
			return this;
		}

		public Builder<T,E> withTaskChangeCollector(AbstractCallTreeValueChangeCollector<T> taskChangeCollector) {
			this.taskChangeCollector = taskChangeCollector;
			return this;
		}

		public Builder<T,E> withTaskChangesToEventBuilder(Function<Map<String, T>, E> taskChangesToEventBuilder) {
			this.taskChangesToEventBuilder = taskChangesToEventBuilder;
			return this;
		}

		public Builder<T,E> withTaskTargetEventSender(EventSender<E> taskTargetEventSender) {
			this.taskTargetEventSender = taskTargetEventSender;
			return this;
		}

		public static Builder<PerfStats,PerfStatsChangesEvent> newBuilderPerfStatChange(CallTreeNode srcRoot) {
			BasicStatIgnorePendingChangeCollector changeCollector = 
					new BasicStatIgnorePendingChangeCollector(srcRoot);
			return new Builder<PerfStats,PerfStatsChangesEvent>()
					.withScheduledExecutor(AsyncUtils.defaultScheduledThreadPool())
					.withPeriod(5*60)
					.withTaskChangeCollector(changeCollector)
					.withTaskChangesToEventBuilder(PerfStatsChangesEvent.FACTORY);
		}

		public static Builder<PendingPerfCount,PendingPerfCountChangesEvent> newBuilderPendingCountChange(CallTreeNode srcRoot) {
			PendingCountChangeCollector changeCollector = 
					new PendingCountChangeCollector(srcRoot);
			return new Builder<PendingPerfCount,PendingPerfCountChangesEvent>()
					.withScheduledExecutor(AsyncUtils.defaultScheduledThreadPool())
					.withPeriod(30)
					.withTaskChangeCollector(changeCollector)
					.withTaskChangesToEventBuilder(PendingPerfCountChangesEvent.FACTORY);
		}

	}
	
	
	// ------------------------------------------------------------------------

	/** flush events without waiting next scheduled period */
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
	 * task for collecting change and sending
	 */
	public static class ChangeCollectorToEventSenderTask<TValue,E> implements Runnable {

		private AbstractCallTreeValueChangeCollector<TValue> changeCollector;
		private Function<Map<String,TValue>,E> changesToEventBuilder;
		private EventSender<E> targetEventSender;
		
		// ------------------------------------------------------------------------

		public ChangeCollectorToEventSenderTask(AbstractCallTreeValueChangeCollector<TValue> changeCollector,
				Function<Map<String, TValue>, E> changesToEventBuilder, EventSender<E> targetEventSender) {
			super();
			this.changeCollector = changeCollector;
			this.changesToEventBuilder = changesToEventBuilder;
			this.targetEventSender = targetEventSender;
		}

		// ------------------------------------------------------------------------

		@Override
		public void run() {
			Map<String, TValue> changes = changeCollector.markAndCollectChanges();
			if (changes != null && !changes.isEmpty()) {
				E changeEvent = changesToEventBuilder.apply(changes);
				targetEventSender.sendEvent(changeEvent);
			}
		}

	}

}
