package org.sef4j.core.api;


/**
 * interface for setting/Clearing a single EventSender<T> listener on a object providing events
 * 
 * @param <T>
 */
public interface SingleListenerEventProvider<T> {

	public void setEventListener(EventSender<T> listener);

	public void clearEventListener();
	
}
