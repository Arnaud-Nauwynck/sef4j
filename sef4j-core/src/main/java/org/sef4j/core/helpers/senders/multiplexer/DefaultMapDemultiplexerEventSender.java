package org.sef4j.core.helpers.senders.multiplexer;

import java.util.HashMap;
import java.util.Map;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.util.CopyOnWriteUtils;

/**
 * default implementation of AbstractDemultiplexerEventSender
 * using in-memory map dispatcher + default MultiplexedEvent<K,T> wrapper event
 * 
 * @param <K>
 * @param <T>
 */
public class DefaultMapDemultiplexerEventSender<K,T> 
	extends AbstractDemultiplexerEventSender<K,MultiplexedEvent<K,T>,T> {

	/** copy-on-write field, using immutable Map */
	protected Map<K,EventSender<T>> dispatcherMap = new HashMap<K,EventSender<T>>();
	protected Object lock = new Object();
	
	protected EventSender<T> defaultEventSender;
	
	// ------------------------------------------------------------------------ 
	
	public DefaultMapDemultiplexerEventSender() {
	}

	public DefaultMapDemultiplexerEventSender(Map<K,EventSender<T>> dispatcherMap) {
		this.dispatcherMap = dispatcherMap;
	}
	
	
	// ------------------------------------------------------------------------ 

	@Override
	protected UnwrapInfoPair<K,T> unwrapEventInfo(MultiplexedEvent<K,T> event) {
		return new UnwrapInfoPair<K,T>(event.getKey(), event.getWrappedEvent());
	}

	@Override
	protected EventSender<T> eventSenderDispatcherFor(K key) {
		EventSender<T> res = dispatcherMap.get(key);
		if (res == null) {
			res = defaultEventSender;
		}
		return res;
	}
	
	// ------------------------------------------------------------------------
	
	public EventSender<T> getDefaultEventSender() {
		return defaultEventSender;
	}

	public void setDefaultEventSender(EventSender<T> defaultEventSender) {
		this.defaultEventSender = defaultEventSender;
	}

	public EventSender<T> getEventSenderDispatcher(K key) {
		return this.dispatcherMap.get(key);
	}

	public void putEventSenderDispatcher(K key, EventSender<T> eventSender) {
		synchronized(lock) {
			this.dispatcherMap = CopyOnWriteUtils.newWithPut(dispatcherMap, key, eventSender);
		}
	}

	public void removeEventSenderDispatcher(K key, EventSender<T> eventSender) {
		synchronized(lock) {
			this.dispatcherMap = CopyOnWriteUtils.newWithRemove(dispatcherMap, key);
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "DefaultMapDemultiplexerEventSender["
				+ "dispatcherMap=" + dispatcherMap 
				+ ", defaultEventSender=" + defaultEventSender 
				+ "]";
	}
	
}