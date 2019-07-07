package org.sef4j.api;

import java.util.Collection;

public interface EventAppender<T> {

    public void sendEvent(T event);

    default public void sendEvents(Collection<T> events) {
	for (T event : events) {
	    sendEvent(event);
	}
    }
}
