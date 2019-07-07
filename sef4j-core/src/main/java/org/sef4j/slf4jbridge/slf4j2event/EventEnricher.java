package org.sef4j.slf4jbridge.slf4j2event;

import org.sef4j.slf4jbridge.LoggingEventExt;

public interface EventEnricher {
    
    public void enrich(LoggingEventExt.Builder eventBuilder);

}
