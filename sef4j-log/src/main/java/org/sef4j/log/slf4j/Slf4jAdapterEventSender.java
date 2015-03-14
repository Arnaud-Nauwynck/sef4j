package org.sef4j.log.slf4j;

import java.util.Collection;

import org.sef4j.core.api.EventSender;


/**
 * adapter EventLogger -> Slf4j
 */
public class Slf4jAdapterEventSender<T> implements EventSender<T> {
	
	public Slf4jAdapterEventSender() {
	}

	@Override
	public void sendEvent(T event) {
		//TODO
		
	}

	public void sendEvents(Collection<T> events) {
		for(T event : events) {
			sendEvent(event);
		}
	}

}
