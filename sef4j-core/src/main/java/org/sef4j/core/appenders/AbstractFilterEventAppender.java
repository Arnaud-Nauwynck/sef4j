package org.sef4j.core.appenders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.sef4j.api.EventAppender;

public abstract class AbstractFilterEventAppender<T> implements EventAppender<T> {

    protected EventAppender<T> target;

    // ------------------------------------------------------------------------

    protected AbstractFilterEventAppender(EventAppender<T> target) {
	this.target = target;
    }

    // ------------------------------------------------------------------------

    @Override
    public void sendEvent(T event) {
	if (acceptEvent(event)) {
	    target.sendEvent(event);
	}
    }

    @Override
    public void sendEvents(Collection<T> events) {
	List<T> filteredEvents = new ArrayList<T>(events.size());
	for (T event : events) {
	    if (acceptEvent(event)) {
		filteredEvents.add(event);
	    }
	}
	if (!filteredEvents.isEmpty()) {
	    if (filteredEvents.size() > 1) {
		target.sendEvents(filteredEvents);
	    } else {
		target.sendEvent(filteredEvents.get(0));
	    }
	}
    }

    protected abstract boolean acceptEvent(T event);

    // ------------------------------------------------------------------------

    public static class PredicateFilterEventSender<T> extends AbstractFilterEventAppender<T> {

	private Predicate<T> predicate;

	public PredicateFilterEventSender(EventAppender<T> target, Predicate<T> predicate) {
	    super(target);
	    this.predicate = predicate;
	}

	@Override
	protected boolean acceptEvent(T event) {
	    return predicate.test(event);
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
	    return "PredicateFilterEventSender [predicate=" + predicate + ", target=" + target + "]";
	}

    }

}
