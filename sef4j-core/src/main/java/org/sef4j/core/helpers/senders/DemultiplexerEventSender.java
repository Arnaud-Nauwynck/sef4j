package org.sef4j.core.helpers.senders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.sef4j.core.api.EventSender;

/**
 * De-Multiplexer for unwrapping event, extract key and dispatch unwrapped event to corresponding EventSender
 * 
 * see also MultiplexerDefaults
 * 
 * <PRE>
 *                  +-----------------------------------------------------+
 *                  |                          +------+                   |   
 *                  |                          |key1  | sender1=          | ---> sendEvent(ue)
 *                  |                        / +------+  dispatcher(key1) |
 *                  |                       /                             |
 *  sendEvent(..)   |                      /   +------+                   |
 *         -------->|       -->            --- |key2  | sender2=          | ---> sendEvent(ue)
 *                  | key,ue=unwrap(event) \   +------+  dispatcher(key2) |
 *                  |                       \                             |
 *                  |                        \ +------+                   |
 *                  |                          |key3  | sender3=          | ---> sendEvent(ue)
 *                  |                          +------+  dispatcher(key3) |
 *                  +-----------------------------------------------------+
 * </PRE>
 * 
 */
public class DemultiplexerEventSender<K,T,TDestEvent> implements EventSender<T> {

	public static class UnwrapInfoPair<K,TDestEvent> {
		K key;
		TDestEvent unwrappedEvent;
		
		public UnwrapInfoPair(K key, TDestEvent unwrappedEvent) {
			this.key = key;
			this.unwrappedEvent = unwrappedEvent;
		}
		
	}
	
	protected Function<T,UnwrapInfoPair<K,TDestEvent>> eventUnwrapperFunc;

	protected Function<K,EventSender<TDestEvent>> eventDispatcherFunc;

	// ------------------------------------------------------------------------
	
	public DemultiplexerEventSender(
			Function<T, UnwrapInfoPair<K, TDestEvent>> eventUnwrapperFunc,
			Function<K, EventSender<TDestEvent>> eventDispatcherFunc) {
		this.eventUnwrapperFunc = eventUnwrapperFunc;
		this.eventDispatcherFunc = eventDispatcherFunc;
	}

	// implements EventSender<T>
	// ------------------------------------------------------------------------
	
	@Override
	public void sendEvent(T event) {
		UnwrapInfoPair<K, TDestEvent> unwrapInfo = unwrapEventInfo(event);
		K key = unwrapInfo.key;
		dispatchEvent(key, unwrapInfo.unwrappedEvent);
	}

	@Override
	public void sendEvents(Collection<T> events) {
		Map<K,List<TDestEvent>> key2events = new HashMap<K,List<TDestEvent>>();
		for(T event : events) {
			UnwrapInfoPair<K, TDestEvent> unwrapInfo = unwrapEventInfo(event);
			K key = unwrapInfo.key;
			List<TDestEvent> ls = key2events.get(key);
			if (ls == null) {
				ls = new ArrayList<TDestEvent>();
				key2events.put(key, ls);
			}
			ls.add(unwrapInfo.unwrappedEvent);
		}
		for(Map.Entry<K, List<TDestEvent>> e : key2events.entrySet()) {
			EventSender<TDestEvent> dispatchedSender = eventDispatcherFunc.apply(e.getKey());
			if (dispatchedSender != null) {
				List<TDestEvent> unwrappedEvents = e.getValue();
				dispatchedSender.sendEvents(unwrappedEvents);
			}	
		}
	}

	// internal helper
	// ------------------------------------------------------------------------
	
	protected UnwrapInfoPair<K, TDestEvent> unwrapEventInfo(T event) {
		UnwrapInfoPair<K, TDestEvent> unwrapInfo = eventUnwrapperFunc.apply(event);
		return unwrapInfo;
	}

	protected void dispatchEvent(K key, TDestEvent unwrapEvent) {
		EventSender<TDestEvent> dispatchedSender = eventDispatcherFunc.apply(key);
		if (dispatchedSender != null) {
			dispatchedSender.sendEvent(unwrapEvent);
		}
	}

	protected void dispatchEvents(K key, List<TDestEvent> unwrappedEvents) {
		EventSender<TDestEvent> dispatchedSender = eventDispatcherFunc.apply(key);
		if (dispatchedSender != null) {
			dispatchedSender.sendEvents(unwrappedEvents);
		}
	}

	// override java.lang.Object
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "DemultiplexerEventSender [eventUnwrapperFunc=" + eventUnwrapperFunc + ", eventDispatcherFunc=" + eventDispatcherFunc + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventDispatcherFunc == null) ? 0 : eventDispatcherFunc.hashCode());
		result = prime * result + ((eventUnwrapperFunc == null) ? 0 : eventUnwrapperFunc.hashCode());
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
		DemultiplexerEventSender<K,T,TDestEvent> other = (DemultiplexerEventSender<K,T,TDestEvent>) obj;
		if (eventDispatcherFunc == null) {
			if (other.eventDispatcherFunc != null)
				return false;
		} else if (!eventDispatcherFunc.equals(other.eventDispatcherFunc))
			return false;
		if (eventUnwrapperFunc == null) {
			if (other.eventUnwrapperFunc != null)
				return false;
		} else if (!eventUnwrapperFunc.equals(other.eventUnwrapperFunc))
			return false;
		return true;
	}

}
