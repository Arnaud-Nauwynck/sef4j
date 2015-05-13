package org.sef4j.core.helpers.senders.multiplexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sef4j.core.api.EventSender;

public class DefaultMultiplexerPerKeyEventSender<K,T> implements EventSender<T> {

	protected K key;
	protected EventSender<MultiplexedEvent<K, T>> delegate;
	
	public DefaultMultiplexerPerKeyEventSender(K key, EventSender<MultiplexedEvent<K, T>> delegate) {
		this.key = key;
		this.delegate = delegate;
	}

	@Override
	public void sendEvent(T event) {
		MultiplexedEvent<K, T> wrappedEvent = new MultiplexedEvent<K, T>(key, event);
		delegate.sendEvent(wrappedEvent);
	}

	@Override
	public void sendEvents(Collection<T> events) {
		List<MultiplexedEvent<K,T>> wrappedEvents = new ArrayList<MultiplexedEvent<K,T>>(events.size());
		for(T event : events) {
			wrappedEvents.add(new MultiplexedEvent<K, T>(key, event));
		}
		delegate.sendEvents(wrappedEvents);		
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "DefaultMultiplexerPerKeyEventSender[" + key + ", delegate=" + delegate + "]";
	}
	
}
