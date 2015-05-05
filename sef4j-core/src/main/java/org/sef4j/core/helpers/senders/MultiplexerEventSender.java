package org.sef4j.core.helpers.senders;

import java.util.HashMap;
import java.util.Map;

import org.sef4j.core.api.EventSender;

/**
 * 
 * <PRE>
 *                  +--------------------------------------------------+
 *  sendEvent(..)   |  +------+                                        |
 *         -------->|  |key1  |  -->  multiplexSend(key1, ..) -->      |
 *                  |  +------+                                    \   |
 *                  |                                               \  |
 *  sendEvent(..)   |  +------+                                      \ |   target.sendEvent(wrappedEvent)
 *         -------->|  |key2  |  --> multiplexSend(key2, ..)  -->  --- |  --------->  
 *                  |  +------+                                      / |
 *                  |                                               /  |
 *  sendEvent(..)   |  +------+                                    /   |
 *         -------->|  |key3  |  --> multiplexSend(key3, ..)  -->      |
 *                  |  +------+                                        |
 *                  +--------------------------------------------------+
 * </PRE>
 * 
 * @param <T>
 */
public class MultiplexerEventSender<K,TSrcEvent,TDestEvent> {

	private Map<K,MultiplexedEntry<K,TSrcEvent,TDestEvent>> multiplexedEventSenders = new HashMap<K,MultiplexedEntry<K,TSrcEvent,TDestEvent>>();
	
	private Object lock = new Object();
	
	protected static class MultiplexedEntry<K,TSrcEvent,TDestEvent> {
		private final K key;
		private EventSender<TSrcEvent> srcEventSender;
		
		public MultiplexedEntry(K key) {
			this.key = key;
		}
		
	}
	
	// ------------------------------------------------------------------------

	public MultiplexerEventSender() {
	}

	// ------------------------------------------------------------------------

	// TODO
	
}
