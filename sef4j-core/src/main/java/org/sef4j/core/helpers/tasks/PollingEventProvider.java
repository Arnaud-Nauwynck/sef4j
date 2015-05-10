package org.sef4j.core.helpers.tasks;

import org.sef4j.core.api.EventProvider;
import org.sef4j.core.helpers.senders.DefaultEventProvider;

public interface PollingEventProvider<T> extends EventProvider<T> {
	
	public void poll();
	

	// ------------------------------------------------------------------------
	
	public static abstract class AbstractPollingEventProvider<T> extends DefaultEventProvider<T> implements PollingEventProvider<T> {

		protected String displayName;
		
		public AbstractPollingEventProvider(String displayName) {
			this.displayName = displayName;
		}

		public PeriodicTask wrapAsPeriodicTask(PeriodicTask.Builder periodBuilder) {
			return periodBuilder.withTask(() -> poll()).build();
		}
		
	}
	
}
