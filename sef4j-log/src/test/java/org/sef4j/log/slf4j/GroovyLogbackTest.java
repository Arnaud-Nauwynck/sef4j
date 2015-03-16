package org.sef4j.log.slf4j;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.core.api.EventSender;
import org.sef4j.core.helpers.appenders.InMemoryEventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

public class GroovyLogbackTest {

    
    private static final Logger LOG = LoggerFactory.getLogger(GroovyLogbackTest.class);
    
    @Test
    public void testLog() {
        LOG.info("test");
        
        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("ROOT");
        Appender<ILoggingEvent> appender = rootLogger.getAppender("EventLoggerAppender");
                
        EventLoggerAdapterAppender eventAppender = (EventLoggerAdapterAppender) appender;
        EventSender eventSender = eventAppender.getTargetEventSender();
        InMemoryEventSender inMemoryEventSender = (InMemoryEventSender) eventSender;
        Assert.assertNotNull(inMemoryEventSender);
    }
}
