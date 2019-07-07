package org.sef4j.core.sources.periodic;

import org.sef4j.api.EventSource;
import org.sef4j.core.appenders.CompositeEventAppender;

public interface PollingEventProvider<T> extends EventSource<T> {

    public void poll();

    // ------------------------------------------------------------------------

    public static abstract class AbstractPollingEventProvider<T> extends CompositeEventAppender<T>
	    implements PollingEventProvider<T> {

	protected String displayName;

	public AbstractPollingEventProvider(String displayName) {
	    this.displayName = displayName;
	}

	public PeriodicTask wrapAsPeriodicTask(PeriodicTask.Builder periodBuilder) {
	    return periodBuilder.withTask(() -> poll()).build();
	}

    }

}
