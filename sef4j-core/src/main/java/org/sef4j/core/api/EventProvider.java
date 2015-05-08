package org.sef4j.core.api;


/**
 * interface for adding/removing EventSender<T> listeners on a object providing events
 * 
 * @param <T>
 */
public interface EventProvider<T> {

	public void addEventListener(EventSender<T> listener);

	public void removeEventListener(EventSender<T> listener);
	
}
