package org.sef4j.api;

import java.util.Collection;

public abstract class EventAppenderBase<T> implements EventAppender<T> {

    @Override
    public void sendEvents(Collection<T> events) {
	for (T event : events) {
	    sendEvent(event);
	}
    }

}
