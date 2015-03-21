package org.sef4j.log.slf4j;

import org.slf4j.Logger;

import ch.qos.logback.classic.Level;

public class Slf4jLoggerUtil {

    public static LogLevel slf4jLevelToLogLevel(ch.qos.logback.classic.Level slf4jLevel) {
        switch(slf4jLevel.toInt()) {
        case Level.OFF_INT: return LogLevel.OFF;
        case Level.ERROR_INT: return LogLevel.ERROR;
        case Level.WARN_INT: return LogLevel.WARN;
        case Level.INFO_INT: return LogLevel.INFO;
        case Level.DEBUG_INT: return LogLevel.DEBUG;
        case Level.TRACE_INT: return LogLevel.TRACE;
        case Level.ALL_INT: return LogLevel.TRACE; // not a level.. use TRACE
        default: return LogLevel.TRACE;
        }
    }
    
	public static boolean isEnabled(Logger slf4jLogger, LogLevel logLevel) {
		switch(logLevel) {
		case OFF: return false;
		case TRACE: return slf4jLogger.isTraceEnabled();
		case DEBUG: return slf4jLogger.isDebugEnabled();
		case INFO: return slf4jLogger.isInfoEnabled();
		case WARN: return slf4jLogger.isWarnEnabled();
		case ERROR: return slf4jLogger.isErrorEnabled();
		default: return false;
		}
	}
	

	
    public static void logLevelText(Logger slf4jLogger, LogLevel logLevel, String text) {
    	switch(logLevel) {
        case OFF:
            break;
        case TRACE:
            slf4jLogger.trace(text);
            break;
        case DEBUG:
            slf4jLogger.debug(text);
            break;
        case INFO:
            slf4jLogger.info(text);
            break;
        case WARN:
            slf4jLogger.warn(text);
            break;
        case ERROR:
            slf4jLogger.error(text);
            break;
        default:
            break;
        }
    }
    

    
    public static void logLevelTextException(Logger slf4jLogger, LogLevel logLevel, String text, Throwable ex) {
        switch(logLevel) {
		case OFF:
			break;
		case TRACE:
			slf4jLogger.trace(text, ex);
			break;
		case DEBUG:
			slf4jLogger.debug(text, ex);
			break;
		case INFO:
			slf4jLogger.info(text, ex);
			break;
		case WARN:
			slf4jLogger.warn(text, ex);
			break;
		case ERROR:
			slf4jLogger.error(text, ex);
			break;
		default:
			break;
		}
    }
    
}
