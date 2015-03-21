package org.sef4j.log.slf4j.slf4j2event;

import org.sef4j.core.api.EventSender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Adapter Slf4j Appender -> EventSender
 * convert from slf4j <code>append(ILoggingEvent)</code> -> to rich <code>sendEvent(LoggingEventExt)</code>
 * 
 * this class transform the logging event ILoggingEvent into a richer event LoggingEventExt, 
 * by adding information from LocalCallStack : path + parameters + inherited properties 
 * 
 * When a richer name-value is already provided (with temporary MASK-UNMASK) from LoggerExt class, 
 * this will be used instead (i.e. raw log event is masked) 
 * 
 * @see Slf4jAppenderThreadLocalMask
 */
public class EventSenderSlf4jAppender extends AppenderBase<ILoggingEvent> {
    
	private EventSender<LoggingEventExt> targetEventSender;
	
	private Slf4jToLoggingEventExtMapper eventMapper = new Slf4jToLoggingEventExtMapper();
	
	// ------------------------------------------------------------------------
	
	public EventSenderSlf4jAppender(EventSender<LoggingEventExt> targetEventSender) {
		this.targetEventSender = targetEventSender;
	}
	
	public EventSender<LoggingEventExt> getTargetEventSender() {
	    return targetEventSender;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	protected void append(ILoggingEvent slf4jEvent) {
	    LoggingEventExt evt;
	    Slf4jAppenderEventMask threadMask = Slf4jAppenderThreadLocalMask.currEventMask();
	    if (threadMask.isMask()) {
	        evt = threadMask.getRichLoggingEventExt();   
	        // may be null => nothing converted to log!
	        if (evt == null && threadMask.eventMapper != null) {
	        	evt = threadMask.eventMapper.slf4jEventToEvent(slf4jEvent);
	        }
	    } else {
	        evt = eventMapper.slf4jEventToEvent(slf4jEvent);
	    }
	    
		// *** delegate to ***
		if (evt != null) {
			targetEventSender.sendEvent(evt);
		}
	}

}
