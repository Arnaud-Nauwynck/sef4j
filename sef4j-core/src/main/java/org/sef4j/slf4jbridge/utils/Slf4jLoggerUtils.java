package org.sef4j.slf4jbridge.utils;

import org.slf4j.Logger;

public class Slf4jLoggerUtils {

    public static boolean isEnabled(Logger slf4jLogger, LogLevel logLevel) {
	switch (logLevel) {
	case OFF:
	    return false;
	case TRACE:
	    return slf4jLogger.isTraceEnabled();
	case DEBUG:
	    return slf4jLogger.isDebugEnabled();
	case INFO:
	    return slf4jLogger.isInfoEnabled();
	case WARN:
	    return slf4jLogger.isWarnEnabled();
	case ERROR:
	    return slf4jLogger.isErrorEnabled();
	default:
	    return false;
	}
    }

    public static void logLevelText(Logger slf4jLogger, LogLevel logLevel, String text) {
	switch (logLevel) {
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
	switch (logLevel) {
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
