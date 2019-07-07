package org.sef4j.core.appenders.dispatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sef4j.api.EventAppender;

public class DefaultDispatcherPerKeyEventAppender<K, T> implements EventAppender<T> {

    protected K key;
    protected EventAppender<KeyEventPair<K, T>> delegate;

    public DefaultDispatcherPerKeyEventAppender(K key, EventAppender<KeyEventPair<K, T>> delegate) {
	this.key = key;
	this.delegate = delegate;
    }

    @Override
    public void sendEvent(T event) {
	KeyEventPair<K, T> wrappedEvent = new KeyEventPair<K, T>(key, event);
	delegate.sendEvent(wrappedEvent);
    }

    @Override
    public void sendEvents(Collection<T> events) {
	List<KeyEventPair<K, T>> wrappedEvents = new ArrayList<KeyEventPair<K, T>>(events.size());
	for (T event : events) {
	    wrappedEvents.add(new KeyEventPair<K, T>(key, event));
	}
	delegate.sendEvents(wrappedEvents);
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
	return "DefaultMultiplexerPerKeyEventSender[" + key + ", delegate=" + delegate + "]";
    }

}
