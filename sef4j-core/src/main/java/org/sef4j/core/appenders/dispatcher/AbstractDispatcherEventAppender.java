package org.sef4j.core.appenders.dispatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sef4j.api.EventAppender;

/**
 * De-Multiplexer EventSender: unwrap event, extract key and dispatch unwrapped
 * event to corresponding EventSender
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
public abstract class AbstractDispatcherEventAppender<K, T, TDestEvent> implements EventAppender<T> {

    public static class UnwrapInfoPair<K, TDestEvent> {
	K key;
	TDestEvent unwrappedEvent;

	public UnwrapInfoPair(K key, TDestEvent unwrappedEvent) {
	    this.key = key;
	    this.unwrappedEvent = unwrappedEvent;
	}

    }

    // ------------------------------------------------------------------------

    protected AbstractDispatcherEventAppender() {
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
	Map<K, List<TDestEvent>> key2events = new HashMap<K, List<TDestEvent>>();
	for (T event : events) {
	    UnwrapInfoPair<K, TDestEvent> unwrapInfo = unwrapEventInfo(event);
	    K key = unwrapInfo.key;
	    List<TDestEvent> ls = key2events.get(key);
	    if (ls == null) {
		ls = new ArrayList<TDestEvent>();
		key2events.put(key, ls);
	    }
	    ls.add(unwrapInfo.unwrappedEvent);
	}
	for (Map.Entry<K, List<TDestEvent>> e : key2events.entrySet()) {
	    K key = e.getKey();
	    EventAppender<TDestEvent> dispatchedSender = eventSenderDispatcherFor(key);
	    if (dispatchedSender != null) {
		List<TDestEvent> unwrappedEvents = e.getValue();
		dispatchedSender.sendEvents(unwrappedEvents);
	    }
	}
    }

    // ------------------------------------------------------------------------

    protected abstract UnwrapInfoPair<K, TDestEvent> unwrapEventInfo(T event);

    protected abstract EventAppender<TDestEvent> eventSenderDispatcherFor(K key);

    protected void dispatchEvent(K key, TDestEvent unwrapEvent) {
	EventAppender<TDestEvent> dispatchedSender = eventSenderDispatcherFor(key);
	if (dispatchedSender != null) {
	    dispatchedSender.sendEvent(unwrapEvent);
	}
    }

    protected void dispatchEvents(K key, List<TDestEvent> unwrappedEvents) {
	EventAppender<TDestEvent> dispatchedSender = eventSenderDispatcherFor(key);
	if (dispatchedSender != null) {
	    dispatchedSender.sendEvents(unwrappedEvents);
	}
    }

}
