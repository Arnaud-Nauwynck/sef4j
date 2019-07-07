package org.sef4j.slf4jbridge.event2slf4j;

import org.slf4j.Logger;

@FunctionalInterface
public interface EventToSlf4jLogCaller<T> {

    public void logTo(T event, Logger slf4jLogger);

}