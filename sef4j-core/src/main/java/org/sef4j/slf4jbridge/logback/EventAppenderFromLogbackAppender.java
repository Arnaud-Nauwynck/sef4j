package org.sef4j.slf4jbridge.logback;

import org.sef4j.api.EventAppender;
import org.sef4j.slf4jbridge.LoggingEventExt;
import org.sef4j.slf4jbridge.slf4j2event.Slf4jAppenderThreadLocalMask;
import org.sef4j.slf4jbridge.slf4j2event.Slf4jAppenderThreadLocalMask.Slf4jAppenderEventMask;
import org.sef4j.slf4jbridge.slf4j2event.Slf4jToLoggingEventExtMapper;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Adapter logback Appender -> EventAppender 
 * <code>append(ILoggingEvent)</code> -> to <code>sendEvent(LoggingEventExt)</code>
 * 
 * this class transform the logging event ILoggingEvent into a richer event
 * LoggingEventExt, by adding information from LocalCallStack : path +
 * parameters + inherited properties
 * 
 * When a richer name-value is already provided (with temporary MASK-UNMASK)
 * from LoggerExt class, this will be used instead (i.e. raw log event is
 * masked)
 * 
 */
public class EventAppenderFromLogbackAppender extends AppenderBase<ILoggingEvent> {

    private EventAppender<LoggingEventExt> target;

    private Slf4jToLoggingEventExtMapper eventMapper = new Slf4jToLoggingEventExtMapper();

    // ------------------------------------------------------------------------
    
    public EventAppenderFromLogbackAppender() {
    }

    public EventAppenderFromLogbackAppender(EventAppender<LoggingEventExt> target, Slf4jToLoggingEventExtMapper eventMapper) {
	this.target = target;
	this.eventMapper = eventMapper;
    }

    public EventAppender<LoggingEventExt> getTarget() {
	return target;
    }

    public void setTarget(EventAppender<LoggingEventExt> target) {
	this.target = target;
    }
    
    public Slf4jToLoggingEventExtMapper getEventMapper() {
	return eventMapper;
    }

    public void setEventMapper(Slf4jToLoggingEventExtMapper eventMapper) {
	this.eventMapper = eventMapper;
    }
    
    
    // ------------------------------------------------------------------------

    @Override
    protected void append(ILoggingEvent slf4jEvent) {
	Slf4jAppenderEventMask threadMask = Slf4jAppenderThreadLocalMask.curr();
	if (threadMask.isMask()) {
	    return; // event are ignored
	}
	LoggingEventExt evt = threadMask.getRichLoggingEventExt();
	if (evt == null) {
	    Slf4jToLoggingEventExtMapper overridenEventMapper = threadMask.getEventMapper();
	    if (overridenEventMapper == null) {
		overridenEventMapper = eventMapper;
	    }
	    evt = overridenEventMapper.logbackEventToEventExt(slf4jEvent);
	}

	// *** delegate to ***
	if (evt != null) {
	    target.sendEvent(evt);
	}
    }

}
