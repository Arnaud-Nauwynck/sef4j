package org.sef4j.core.helpers.ioeventchain;

import java.util.concurrent.TimeUnit;

import org.sef4j.core.api.ioeventchain.DefaultInputEventChainDefs.PeriodicTaskInputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.api.ioeventchain.InputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChainFactory;
import org.sef4j.core.helpers.tasks.PeriodicTask;
import org.sef4j.core.helpers.tasks.PollingEventProvider;
import org.sef4j.core.util.AsyncUtils;
import org.sef4j.core.util.IStartableSupport;
import org.sef4j.core.util.factorydef.ObjectByDefRepositories;
import org.sef4j.core.util.factorydef.ObjectWithHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * InputEventChain adapter for polling on tasks and sending corresponding events
 * 
 */
public class PeriodicTaskInputEventChain<T> extends InputEventChain<T> {

	private static final Logger LOG = LoggerFactory.getLogger(PeriodicTaskInputEventChain.class);
	
	protected ObjectWithHandle<? extends PollingEventProvider<T>> pollingEventProviderHandle;
	
	protected PeriodicTask periodicTask;
	
	// ------------------------------------------------------------------------
	
	public PeriodicTaskInputEventChain(
			PeriodicTaskInputEventChainDef def, String displayName,
			ObjectWithHandle<? extends PollingEventProvider<T>> pollingEventProviderHandle,
			PeriodicTask.Builder pollingPeriodBuilder) {
		super(def, displayName);
		this.pollingEventProviderHandle = pollingEventProviderHandle;
		this.periodicTask = pollingPeriodBuilder.build(() -> poll());
		pollingEventProviderHandle.getObject().addEventListener(innerEventProvider);
	}
	
	@Override
	public void close() {
		super.close();
		assert ! isStarted();
		this.pollingEventProviderHandle.getObject().removeEventListener(innerEventProvider);
		this.pollingEventProviderHandle = null;
		this.periodicTask = null;
	}
	
	// ------------------------------------------------------------------------
	
	public PeriodicTaskInputEventChainDef getDef() {
		return (PeriodicTaskInputEventChainDef) super.getDef(); 
	}
	
	protected IStartableSupport startablePollingEventProvider() {
		if (pollingEventProviderHandle.getObject() instanceof IStartableSupport) {
			return (IStartableSupport) pollingEventProviderHandle.getObject();
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
		pollingEventProviderHandle.getObject().poll();
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "PeriodicTaskInputEventChain [" + displayName + "]";
	}

	// ------------------------------------------------------------------------
	
	public static class Factory<T> extends InputEventChainFactory<T> {
		
		public Factory() {
			super("PeriodicTaskInputEventChain");
		}

		@Override
		public boolean accepts(InputEventChainDef def) {
			return def instanceof PeriodicTaskInputEventChainDef;
		}

		@Override
		public InputEventChain<T> create(InputEventChainDef defObj, ObjectByDefRepositories repositories) {
			PeriodicTaskInputEventChainDef def = (PeriodicTaskInputEventChainDef) defObj;

			PeriodicTask.Builder pollingPeriodBuilder = new PeriodicTask.Builder()
				.withOptionalPeriodicityDef(def.getPeriodicity())
				.withDefaults(30, TimeUnit.SECONDS, AsyncUtils.defaultScheduledThreadPool());

			ObjectWithHandle<?> taskObjHandle = repositories.getOrCreateByDef(def.getTaskDef());
			
			@SuppressWarnings("unchecked")
			ObjectWithHandle<PollingEventProvider<T>> taskHandle = (ObjectWithHandle<PollingEventProvider<T>>) taskObjHandle;
					
			PeriodicTaskInputEventChain<T> res = new PeriodicTaskInputEventChain<T>(def, "PeriodicTaskInputEventChain",
					taskHandle, pollingPeriodBuilder);
			return res;
		}
		
	}
}
