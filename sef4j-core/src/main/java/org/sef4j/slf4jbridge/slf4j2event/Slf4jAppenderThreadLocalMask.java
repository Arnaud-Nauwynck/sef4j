package org.sef4j.slf4jbridge.slf4j2event;

import org.sef4j.slf4jbridge.LoggingEventExt;

/**
 * ThreadLocal mask support for replacing current slf4j logged event by richer
 * event or mask it
 * 
 */
public final class Slf4jAppenderThreadLocalMask {

    /**
     * flag+event to replace the current Slf4j logEvent being converted on the
     * thread
     */
    public static final class Slf4jAppenderEventMask {
	/** when true, slf4j log events are ignored */
	private boolean mask = false;
	private LoggingEventExt richLoggingEventExt;
	private Slf4jToLoggingEventExtMapper eventMapper;
	
	public boolean isMask() {
	    return mask;
	}
	public LoggingEventExt getRichLoggingEventExt() {
	    return richLoggingEventExt;
	}
	public Slf4jToLoggingEventExtMapper getEventMapper() {
	    return eventMapper;
	}
	
    }

    private static final ThreadLocal<Slf4jAppenderEventMask> threadLocalAppenderMask = new ThreadLocal<Slf4jAppenderEventMask>() {
	@Override
	protected Slf4jAppenderEventMask initialValue() {
	    return new Slf4jAppenderEventMask();
	}
    };

    // ------------------------------------------------------------------------

    /* private to force all static */
    private Slf4jAppenderThreadLocalMask() {
    }

    // ------------------------------------------------------------------------

    public static Slf4jAppenderEventMask curr() {
	return threadLocalAppenderMask.get();
    }

    /**
     * typical code usage:
     * 
     * <pre>
     * LoggingEventExt richEvent = new LoggingEventExt(); 
     *   // .. fill with name-values ... 
     * executeLogWithEnrichedLogEventExt(richEvent, () -> log.info("poor formatted text log..."));
     * </pre>
     * 
     */
    public static void logWithEnrichedLogEventExt(
	    LoggingEventExt enrichedLogEventExt,
	    Runnable logAction
	    ) {
	Slf4jAppenderEventMask threadMask = threadLocalAppenderMask.get();
	LoggingEventExt prevEvent = threadMask.richLoggingEventExt;
	threadMask.richLoggingEventExt = enrichedLogEventExt;
	try {
	    logAction.run();
	} finally {
	    threadMask.richLoggingEventExt = prevEvent;
	}
    }

}
