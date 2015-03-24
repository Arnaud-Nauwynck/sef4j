package org.sef4j.core.helpers.exporters;

import java.io.Closeable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for periodically executing a Runnable task
 * 
 */
public class PeriodicTask implements Closeable {

	private static final Logger LOG = LoggerFactory.getLogger(PeriodicTask.class);
	
	private String displayName;
	
	private Runnable delegateRunnableTask;
	
	private int period;
	private TimeUnit periodTimeUnit;
	private ScheduledExecutorService scheduledExecutor;
	
	private ScheduledFuture<?> scheduledFuture;

	private long nextScheduledFutureTime;
	
	private Object lock = new Object();

	private Runnable wrapperRunnableTimerTask = new Runnable() {
		public void run() {
			onTimerTask();
		}
	};

	// ------------------------------------------------------------------------
	
	public PeriodicTask(String displayName, Runnable runnableTask, 
			int period, TimeUnit periodTimeUnit,
			ScheduledExecutorService scheduledExecutor) {
		super();
		this.displayName = displayName;
		this.delegateRunnableTask = runnableTask;
		this.period = period;
		this.periodTimeUnit = periodTimeUnit;
		this.scheduledExecutor = scheduledExecutor;
	}
	
	public void close() {
		synchronized(lock) {
			if (isStarted()) {
				stop();
			}
			// clear all fields
			this.delegateRunnableTask = null;
			this.scheduledExecutor = null;	
		}
	}
	
	// ------------------------------------------------------------------------

	public boolean isStarted() {
		synchronized(lock) {
			return scheduledFuture != null;
		}
	}
	
	public void start() {
		synchronized(lock) {
			if (scheduledFuture == null) {
				LOG.info("start periodic task " + displayName);
				scheduledFuture = scheduledExecutor.scheduleWithFixedDelay(wrapperRunnableTimerTask, 0, period, periodTimeUnit);
			} else {
				LOG.info("periodic task " + displayName + " already started, do nothing");
			}
		}
	}

	public void stop() {
		synchronized(lock) {
			if (scheduledFuture != null) {
				LOG.info("stop periodic task " + displayName);
				scheduledFuture.cancel(false);
				scheduledFuture = null;
				nextScheduledFutureTime = 0;
			} else {
				LOG.info("periodic task " + displayName + " already stopped, do nothing");
			}
		}
	}

	public long getNextScheduledFutureTime() {
		synchronized(lock) {
			return nextScheduledFutureTime;
		}
	}

	public long getNextScheduledInMillis() {
		synchronized(lock) {
			return nextScheduledFutureTime - System.currentTimeMillis();
		}
	}

	protected void onTimerTask() {
		try {
			delegateRunnableTask.run();
		} catch(Exception ex) {
			LOG.warn("Failed to execute periodic task " + displayName + " ... from timer Thread Pool... ignore, no rethrow!", ex);
		}
		synchronized(lock) {
			nextScheduledFutureTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(period, periodTimeUnit);
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		String info;
		synchronized(lock) {
			if (scheduledFuture != null) {
				long nextMillis = nextScheduledFutureTime - System.currentTimeMillis();
				info = "started, next schedule in " + nextMillis + " ms";
			} else {
				info = "stopped";
			}
		}
		return "PeriodicTask [displayName=" + displayName + " " + info + "]";
	}

}
