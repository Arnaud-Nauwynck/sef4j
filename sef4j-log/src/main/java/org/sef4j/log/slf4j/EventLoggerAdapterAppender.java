package org.sef4j.log.slf4j;

import org.sef4j.core.api.EventSender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Adapter Slf4j -> EventLogger
 * 
 * slf4j logger appender, to transform log events into structured event to send to EventLogger
 * 
 * this class transform the logging event ILoggingEvent into a richer event LoggingEventExt, 
 * by adding information from LocalCallStack : path + parameters + inherited properties 
 * 
 * When a richer name-value is already provided (with temporary MASK-UNMASK) from LoggerExt class, this will be used instead 
 */
public class EventLoggerAdapterAppender extends AppenderBase<ILoggingEvent> {

    private static class LoggerExtMask {
        LoggingEventExt richLoggingEventExt;
    }
    
    private static final ThreadLocal<LoggerExtMask> threadLocalLoggerExtMask = new ThreadLocal<LoggerExtMask>() {
        @Override
        protected LoggerExtMask initialValue() {
            return new LoggerExtMask();
        }
    };
    
    
	private EventSender<LoggingEventExt> targetEventSender;
	
	// ------------------------------------------------------------------------
	
	public EventLoggerAdapterAppender(EventSender<LoggingEventExt> targetEventSender) {
		this.targetEventSender = targetEventSender;
	}
	
	public EventSender<LoggingEventExt> getTargetEventSender() {
	    return targetEventSender;
	}
	
	// Thread Local MASK-UNMASK support for ignoring sl4f raw message, and use enriched LoggingEventExt replacement
	// ------------------------------------------------------------------------

    /**
	 * typical code usage:
	 * <pre>
	 * {@code
	 * 
	 * LoggingEventExt event = new LoggingEventExt(); // <= fill with rich name-values ... 
	 * 
	 * LoggingEventExt prev = EventLoggerAdapterAppender.pushTmpMaskWithReplaceRichEvent(richEvent);
	 * try {
	 *     log.info("formatted text...");
	 * } finally {
	 *     EventLoggerAdapterAppender.popTmpUnmask(prev);
	 * }
	 * 
	 * }</pre>
	 * @param event
	 * @return
	 */
	public static LoggingEventExt pushTmpMaskWithReplaceRichEvent(LoggingEventExt event) {
	    LoggerExtMask threadMask = threadLocalLoggerExtMask.get();
	    LoggingEventExt prev = threadMask.richLoggingEventExt;
	    threadMask.richLoggingEventExt = event;
	    return prev;
	}
	
	public static void popTmpUnmask(LoggingEventExt prev) {
	    LoggerExtMask threadMask = threadLocalLoggerExtMask.get();
	    threadMask.richLoggingEventExt = prev;
	}
	
	
	// ------------------------------------------------------------------------
	
	@Override
	protected void append(ILoggingEvent slf4jEvent) {
	    LoggingEventExt evt;
	    LoggerExtMask threadMask = threadLocalLoggerExtMask.get();
	    if (threadMask.richLoggingEventExt != null) {
	        evt = threadMask.richLoggingEventExt;   
	    } else {
	        evt = LoggingEventExtUtil.slf4jEventToEvent(slf4jEvent);
	    }
	    
		// *** delegate to ***
		targetEventSender.sendEvent(evt);
	}


}
