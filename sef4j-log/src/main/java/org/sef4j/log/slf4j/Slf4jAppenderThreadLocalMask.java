package org.sef4j.log.slf4j;

import org.sef4j.log.slf4j.LoggerExt.LogLevel;
import org.slf4j.Logger;

/**
 * ThreadLocal mask support for replacing current slf4j logged event by richer event or mask it
 * 
 * @see EventSenderFromSlf4jAppender
 */
public final class Slf4jAppenderThreadLocalMask {

    private static final ThreadLocal<Slf4jAppenderEventMask> threadLocalAppenderMask = new ThreadLocal<Slf4jAppenderEventMask>() {
        @Override
        protected Slf4jAppenderEventMask initialValue() {
            return new Slf4jAppenderEventMask(false, null);
        }
    };
    
	// ------------------------------------------------------------------------

    /* private to force all static */
    private Slf4jAppenderThreadLocalMask() {
	}
    
    // ------------------------------------------------------------------------
    
	public static Slf4jAppenderEventMask currEventMask() {
		return threadLocalAppenderMask.get();
	}
	
    /**
	 * typical code usage:
	 * <pre>
	 * {@code
	 * 
	 * LoggingEventExt event = new LoggingEventExt(); // <= fill with rich name-values ... 
	 * 
	 * LoggingEventExt prev = Slf4jAppenderThreadLocalMask.pushTmpMaskWithReplaceRichEvent(richEvent);
	 * try {
	 *     log.info("formatted text...");
	 * } finally {
	 *     EventLoggerAdapterAppender.popTmpUnmask(prev);
	 * }
	 * 
	 * }</pre>
	 * @param event
	 * @return previous mask to restore to <code>popTmpUnmask()</code>
	 */
	public static Slf4jAppenderEventMask pushTmpMaskWithReplaceRichEvent(Slf4jAppenderEventMask mask) {
	    Slf4jAppenderEventMask threadMask = threadLocalAppenderMask.get();
	    Slf4jAppenderEventMask prev = threadMask.getCopy();
	    threadMask.set(mask);
	    return prev;
	}

	/** alias for <code>pushTmpMaskWithReplaceRichEvent(new Slf4jAppenderEventMask(true, event))</code> */
	public static Slf4jAppenderEventMask pushTmpMaskWithReplaceRichEvent(LoggingEventExt event) {
		return pushTmpMaskWithReplaceRichEvent(new Slf4jAppenderEventMask(true, event));
	}

	public static void popTmpUnmask(Slf4jAppenderEventMask prev) {
	    Slf4jAppenderEventMask threadMask = threadLocalAppenderMask.get();
	    threadMask.set(prev);
	}
	
	// utility method for <code>pushMask..(); try { log...(); } finally { popTmpUnmask(); } 
	// ------------------------------------------------------------------------
	
	public static void maskLogLevelText(Slf4jAppenderEventMask tmpMmask, Logger slf4jLogger, LogLevel logLevel, String text) {
		Slf4jAppenderEventMask prevMask = Slf4jAppenderThreadLocalMask.pushTmpMaskWithReplaceRichEvent(tmpMmask);
		try {
			Slf4jLoggerUtil.logLevelText(slf4jLogger, logLevel, text);
		} finally {
			Slf4jAppenderThreadLocalMask.popTmpUnmask(prevMask);
		}
	}

	public static void maskLogLevelTextEx(Slf4jAppenderEventMask tmpMmask, Logger slf4jLogger, LogLevel logLevel, String text, Throwable ex) {
		Slf4jAppenderEventMask prevMask = Slf4jAppenderThreadLocalMask.pushTmpMaskWithReplaceRichEvent(tmpMmask);
		try {
			Slf4jLoggerUtil.logLevelTextException(slf4jLogger, logLevel, text, ex);
		} finally {
			Slf4jAppenderThreadLocalMask.popTmpUnmask(prevMask);
		}
	}

}
