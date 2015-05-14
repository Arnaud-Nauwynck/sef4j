package org.sef4j.core.helpers.ioeventchain;

import java.util.concurrent.TimeUnit;

import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.api.ioeventchain.InputEventChainFactory;
import org.sef4j.core.helpers.tasks.PeriodicTask;
import org.sef4j.core.helpers.tasks.PeriodicTaskInputEventChainDef;
import org.sef4j.core.helpers.tasks.PollingEventProvider;
import org.sef4j.core.util.AsyncUtils;
import org.sef4j.core.util.IStartableSupport;
import org.sef4j.core.util.factorydef.DependencyObjectCreationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InputEventChain adapter for polling on tasks and sending corresponding events
 * 
 */
public class PeriodicTaskInputEventChain<T> extends InputEventChain<T> {

	private static final Logger LOG = LoggerFactory.getLogger(PeriodicTaskInputEventChain.class);
	
	protected PollingEventProvider<T> pollingEventProvider;
	
	protected PeriodicTask periodicTask;
	
	// ------------------------------------------------------------------------
	
	public PeriodicTaskInputEventChain(
			String displayName,
			PollingEventProvider<T> pollingEventProvider,
			PeriodicTask.Builder pollingPeriodBuilder) {
		super(displayName);
		this.pollingEventProvider = pollingEventProvider;
		this.periodicTask = pollingPeriodBuilder.build(() -> poll());
		pollingEventProvider.addEventListener(innerEventProvider);
	}
	
	@Override
	public void close() {
		super.close();
		assert ! isStarted();
		this.pollingEventProvider.removeEventListener(innerEventProvider);
		this.pollingEventProvider = null;
		this.periodicTask = null;
	}
	
	// ------------------------------------------------------------------------
	
	protected IStartableSupport startablePollingEventProvider() {
		if (pollingEventProvider instanceof IStartableSupport) {
			return (IStartableSupport) pollingEventProvider;
		}
		return null;
	}
	
	@Override
	public boolean isStarted() {
		boolean res = periodicTask.isStarted();
		IStartableSupport pollingStartable = startablePollingEventProvider();
		if (pollingStartable != null) {
			res = pollingStartable.isStarted();
		}
		return res;
	}

	@Override
	public void start() {
		if (isStarted()) {
			return;
		}
		LOG.debug("start " + displayName);
		IStartableSupport pollingStartable = startablePollingEventProvider();
		if (pollingStartable != null) {
			pollingStartable.start();
		}
		periodicTask.start();
	}

	@Override
	public void stop() {
		if (! isStarted()) {
			return;
		}
		LOG.debug("stop " + displayName);
		IStartableSupport pollingStartable = startablePollingEventProvider();
		if (pollingStartable != null) {
			pollingStartable.stop();
		}
		periodicTask.stop();
	}

	public void poll() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("poll " + displayName);
		}
		pollingEventProvider.poll();
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "PeriodicTaskInputEventChain [" + displayName + "]";
	}

	// ------------------------------------------------------------------------
	
	public static class Factory<T> 
		extends InputEventChainFactory<PeriodicTaskInputEventChainDef,PeriodicTaskInputEventChain<T>> {
		
		public Factory() {
			super("PeriodicTaskInputEventChain", PeriodicTaskInputEventChainDef.class);
		}

		@Override
		public PeriodicTaskInputEventChain<T> create(
				PeriodicTaskInputEventChainDef def, 
				DependencyObjectCreationContext ctx) {
			PeriodicTask.Builder pollingPeriodBuilder = new PeriodicTask.Builder()
				.withOptionalPeriodicityDef(def.getPeriodicity())
				.withDefaults(30, TimeUnit.SECONDS, AsyncUtils.defaultScheduledThreadPool());

			PollingEventProvider<T> task = ctx.getOrCreateDependencyByDef("task", def.getTaskDef());
								
			String displayName = ctx.getCurrObjectDisplayName();
			PeriodicTaskInputEventChain<T> res = new PeriodicTaskInputEventChain<T>(displayName,
					task, pollingPeriodBuilder);
			return res;
		}
		
	}
}
