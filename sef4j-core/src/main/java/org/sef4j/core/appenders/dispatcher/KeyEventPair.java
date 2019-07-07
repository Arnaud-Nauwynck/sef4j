package org.sef4j.core.appenders.dispatcher;

import java.io.Serializable;

/**
 * immutable class for Pair<key,wrappedEvent>
 * 
 * @param <K>
 * @param <T>
 */
public class KeyEventPair<K, T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final K key;
    private final T wrappedEvent;

    public KeyEventPair(K key, T wrappedEvent) {
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
	KeyEventPair<K, T> other = (KeyEventPair<K, T>) obj;
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