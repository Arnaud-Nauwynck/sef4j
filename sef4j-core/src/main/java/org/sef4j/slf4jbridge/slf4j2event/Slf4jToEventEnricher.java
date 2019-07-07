package org.sef4j.slf4jbridge.slf4j2event;

import org.sef4j.slf4jbridge.LoggingEventExt;

import ch.qos.logback.classic.spi.ILoggingEvent;

public interface Slf4jToEventEnricher {

    public void enrich(LoggingEventExt.Builder eventBuilder, ILoggingEvent slf4jEvent);

}
