package org.sef4j.core.appenders.dispatcher;

import java.util.HashMap;
import java.util.Map;

import org.sef4j.api.EventAppender;
import org.sef4j.core.utils.CopyOnWriteUtils;

/**
 * default implementation of AbstractDemultiplexerEventSender using in-memory
 * map dispatcher + default MultiplexedEvent<K,T> wrapper event
 * 
 * @param <K>
 * @param <T>
 */
public class DefaultMapDispatcherEventAppender<K, T> extends AbstractDispatcherEventAppender<K, KeyEventPair<K, T>, T> {

    /** copy-on-write field, using immutable Map */
    protected Map<K, EventAppender<T>> dispatcherMap = new HashMap<K, EventAppender<T>>();
    protected Object lock = new Object();

    protected EventAppender<T> defaultEventAppender;

    // ------------------------------------------------------------------------

    public DefaultMapDispatcherEventAppender() {
    }

    public DefaultMapDispatcherEventAppender(Map<K, EventAppender<T>> dispatcherMap) {
	this.dispatcherMap = dispatcherMap;
    }

    // ------------------------------------------------------------------------

    @Override
    protected UnwrapInfoPair<K, T> unwrapEventInfo(KeyEventPair<K, T> event) {
	return new UnwrapInfoPair<K, T>(event.getKey(), event.getWrappedEvent());
    }

    @Override
    protected EventAppender<T> eventSenderDispatcherFor(K key) {
	EventAppender<T> res = dispatcherMap.get(key);
	if (res == null) {
	    res = defaultEventAppender;
	}
	return res;
    }

    // ------------------------------------------------------------------------

    public EventAppender<T> getDefaultEventSender() {
	return defaultEventAppender;
    }

    public void setDefaultEventSender(EventAppender<T> defaultEventSender) {
	this.defaultEventAppender = defaultEventSender;
    }

    public EventAppender<T> getEventSenderDispatcher(K key) {
	return this.dispatcherMap.get(key);
    }

    public void putEventSenderDispatcher(K key, EventAppender<T> eventSender) {
	synchronized (lock) {
	    this.dispatcherMap = CopyOnWriteUtils.newWithPut(dispatcherMap, key, eventSender);
	}
    }

    public void removeEventSenderDispatcher(K key, EventAppender<T> eventSender) {
	synchronized (lock) {
	    this.dispatcherMap = CopyOnWriteUtils.newWithRemove(dispatcherMap, key);
	}
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
	return "DefaultMapDemultiplexerEventSender[" + "dispatcherMap=" + dispatcherMap + ", defaultEventSender="
		+ defaultEventAppender + "]";
    }

}