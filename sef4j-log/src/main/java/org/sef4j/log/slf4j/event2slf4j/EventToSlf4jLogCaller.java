package org.sef4j.log.slf4j.event2slf4j;

import org.slf4j.Logger;

public interface EventToSlf4jLogCaller<T> {
	
	public void logTo(T event, Logger slf4jLogger);

}