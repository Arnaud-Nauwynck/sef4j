package org.sef4j.core.api;

import java.util.Collection;

public interface EventSender<T> {

	public void sendEvent(T event);
	
	public void sendEvents(Collection<T> events);

}
