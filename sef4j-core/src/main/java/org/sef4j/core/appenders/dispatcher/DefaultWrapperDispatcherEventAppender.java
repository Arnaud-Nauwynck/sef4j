package org.sef4j.core.appenders.dispatcher;

import org.sef4j.api.EventAppender;

/**
 * default implementation of AbstractMultiplexerEventSender using class
 * MultiplexedEvent<K,T> (=Pair<key,Event>) as wrapped event type
 *
 * TODO deprecated??? see also DefaultMultiplexerPerKeyEventSender<K,T>
 *
 * @param <K>
 * @param <T>
 */
public class DefaultWrapperDispatcherEventAppender<K, T>
	extends AbstractWrapperDispatcherEventAppender<K, T, KeyEventPair<K, T>> {

    // ------------------------------------------------------------------------

    public DefaultWrapperDispatcherEventAppender(EventAppender<KeyEventPair<K, T>> target) {
	super(target);
    }

    // ------------------------------------------------------------------------

    @Override
    protected KeyEventPair<K, T> wrapEvent(K key, T event) {
	return new KeyEventPair<K, T>(key, event);
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
	return "DefaultMultiplexerEventSender[target=" + target + "]";
    }

}
