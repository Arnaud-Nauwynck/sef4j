package org.sef4j.core.helpers.senders.multiplexer;

import org.sef4j.core.api.EventSender;

/**
 * default implementation of AbstractMultiplexerEventSender
 * using class MultiplexedEvent<K,T> (=Pair<key,Event>) as wrapped event type
 *
 *TODO deprecated??? see also DefaultMultiplexerPerKeyEventSender<K,T>
 *
 * @param <K>
 * @param <T>
 */
public class DefaultWrapperMultiplexerEventSender<K,T> 
	extends AbstractWrapperMultiplexerEventSender<K,T,MultiplexedEvent<K,T>> {

	// ------------------------------------------------------------------------
	
	public DefaultWrapperMultiplexerEventSender(EventSender<MultiplexedEvent<K,T>> target) {
		super(target);
	}

	// ------------------------------------------------------------------------

	@Override
	protected MultiplexedEvent<K, T> wrapEvent(K key, T event) {
		return new MultiplexedEvent<K,T>(key, event);
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "DefaultMultiplexerEventSender[target=" + target + "]";
	}

}
