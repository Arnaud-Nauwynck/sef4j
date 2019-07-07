package org.sef4j.api;

/**
 * interface for adding/removing EventAppender<T> listeners on a object
 * providing events
 * 
 * @param <T>
 */
public interface EventSource<T> {

    public void addEventListener(EventAppender<T> listener);

    public void removeEventListener(EventAppender<T> listener);

}
