package org.sef4j.core.appenders.dispatcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sef4j.api.EventAppender;

/**
 * Multiplexer for sending wrapped event with key to underlying EventSender
 * 
 * see also MultiplexerDefaults
 * 
 * <PRE>
 *                  +--------------------------------------------------+
 *  sendEvent(..)   |  +------+                                        |
 *         -------->|  |key1  |  --> multiplexSend(key1, ..)  -->      |
 *                  |  +------+         wrappedEvent=              \   |
 *                  |                        wrapEvent(key,event)   \  |
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
public abstract class AbstractWrapperDispatcherEventAppender<K, TSrcEvent, TDestEvent> {

    protected EventAppender<TDestEvent> target;

    // ------------------------------------------------------------------------

    public AbstractWrapperDispatcherEventAppender(EventAppender<TDestEvent> target) {
	this.target = target;
    }

    // ------------------------------------------------------------------------

    public MultiplexedPerKeyEventSender<K, TSrcEvent, TDestEvent> eventSenderFor(K key) {
	return new MultiplexedPerKeyEventSender<K, TSrcEvent, TDestEvent>(this, key);
    }

    public void multiplexSendEvent(K key, TSrcEvent event) {
	TDestEvent wrappedEvent = wrapEvent(key, event);
	target.sendEvent(wrappedEvent);
    }

    public void multiplexSendEvents(K key, Collection<TSrcEvent> events) {
	List<TDestEvent> wrappedEvents = wrapEvents(key, events);
	target.sendEvents(wrappedEvents);
    }

    // ------------------------------------------------------------------------

    protected abstract TDestEvent wrapEvent(K key, TSrcEvent event);

    protected List<TDestEvent> wrapEvents(K key, Collection<TSrcEvent> events) {
	List<TDestEvent> res = new ArrayList<TDestEvent>(events.size());
	for (TSrcEvent e : events) {
	    TDestEvent de = wrapEvent(key, e);
	    res.add(de);
	}
	return res;
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
	return "MultiplexerEventSender[target=" + target + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((target == null) ? 0 : target.hashCode());
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
	AbstractWrapperDispatcherEventAppender<K, TSrcEvent, TDestEvent> other = (AbstractWrapperDispatcherEventAppender<K, TSrcEvent, TDestEvent>) obj;
	if (target == null) {
	    if (other.target != null)
		return false;
	} else if (!target.equals(other.target))
	    return false;
	return true;
    }

    // ------------------------------------------------------------------------

    /**
     * Multiplexed per Key EventSender
     * 
     * @param <K>
     * @param <TSrcEvent>
     * @param <TDestEvent>
     */
    public static class MultiplexedPerKeyEventSender<K, TSrcEvent, TDestEvent> implements EventAppender<TSrcEvent> {

	protected final AbstractWrapperDispatcherEventAppender<K, TSrcEvent, TDestEvent> to;
	protected final K key;

	public MultiplexedPerKeyEventSender(AbstractWrapperDispatcherEventAppender<K, TSrcEvent, TDestEvent> to,
		K key) {
	    this.to = to;
	    this.key = key;
	}

	public void sendEvent(TSrcEvent event) {
	    to.multiplexSendEvent(key, event);
	}

	public void sendEvents(Collection<TSrcEvent> events) {
	    to.multiplexSendEvents(key, events);
	}

	@Override
	public String toString() {
	    return "MultiplexedEventSender[" + key + ", to=" + to + "]";
	}

	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((key == null) ? 0 : key.hashCode());
	    result = prime * result + ((to == null) ? 0 : to.hashCode());
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
	    MultiplexedPerKeyEventSender<K, TSrcEvent, TDestEvent> other = (MultiplexedPerKeyEventSender<K, TSrcEvent, TDestEvent>) obj;
	    if (key == null) {
		if (other.key != null)
		    return false;
	    } else if (!key.equals(other.key))
		return false;
	    if (to == null) {
		if (other.to != null)
		    return false;
	    } else if (!to.equals(other.to))
		return false;
	    return true;
	}

    }

}
