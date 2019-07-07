package org.sef4j.slf4jbridge.logback;

import org.sef4j.slf4jbridge.utils.LogLevel;
import org.slf4j.Logger;

import ch.qos.logback.classic.Level;

public class Sef4jLogbackUtils {

    public static LogLevel slf4jLevelToLogLevel(ch.qos.logback.classic.Level slf4jLevel) {
	switch (slf4jLevel.toInt()) {
	case Level.OFF_INT:
	    return LogLevel.OFF;
	case Level.ERROR_INT:
	    return LogLevel.ERROR;
	case Level.WARN_INT:
	    return LogLevel.WARN;
	case Level.INFO_INT:
	    return LogLevel.INFO;
	case Level.DEBUG_INT:
	    return LogLevel.DEBUG;
	case Level.TRACE_INT:
	    return LogLevel.TRACE;
	case Level.ALL_INT:
	    return LogLevel.TRACE; // not a level.. use TRACE
	default:
	    return LogLevel.TRACE;
	}
    }

}
