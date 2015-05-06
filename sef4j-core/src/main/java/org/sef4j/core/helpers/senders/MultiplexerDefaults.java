package org.sef4j.core.helpers.senders;

import java.io.Serializable;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.sef4j.core.helpers.senders.DemultiplexerEventSender.UnwrapInfoPair;

/**
 * utilitie helper for EventSender Multiplexer / Demultiplexed / MultiplexedEvent
 *
 */
public final class MultiplexerDefaults {

	/* private to force all static utilities */
	private MultiplexerDefaults() {}
	
	/**
	 * immutable class for multiplexed event = Pair<key,wrappedEvent>
	 * 
	 * @param <K>
	 * @param <T>
	 */
	public static class MultiplexedEvent<K,T> implements Serializable {
	
		private static final long serialVersionUID = 1L;

		private final K key;
		private final T wrappedEvent;
		
		public MultiplexedEvent(K key, T wrappedEvent) {
			this.key = key;
			this.wrappedEvent = wrappedEvent;
		}

		public K getKey() {
			return key;
		}

		public T getWrappedEvent() {
			return wrappedEvent;
		}

		@Override
		public String toString() {
			return "MultiplexedEvent[" + key + ", " + wrappedEvent + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((key == null) ? 0 : key.hashCode());
			result = prime * result + ((wrappedEvent == null) ? 0 : wrappedEvent.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			MultiplexedEvent<K,T> other = (MultiplexedEvent<K,T>) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;
			if (wrappedEvent == null) {
				if (other.wrappedEvent != null)
					return false;
			} else if (!wrappedEvent.equals(other.wrappedEvent))
				return false;
			return true;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public static class MultiplexerFunc<K,T> implements BiFunction<K,T,MultiplexedEvent<K,T>> {

		@Override
		public MultiplexedEvent<K,T> apply(K key, T event) {
			return new MultiplexedEvent<K,T>(key, event);
		}
		
	}

	// ------------------------------------------------------------------------
	
	public static class UnwrapDemultiplexerFunc<K,T> implements Function<MultiplexedEvent<K,T>,UnwrapInfoPair<K,T>> {

		@Override
		public UnwrapInfoPair<K,T> apply(MultiplexedEvent<K, T> event) {
			return new UnwrapInfoPair<K,T>(event.getKey(), event.getWrappedEvent());
		}
		
	}

	// ------------------------------------------------------------------------
	
}
