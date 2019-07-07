package org.sef4j.slf4jbridge;

import org.sef4j.slf4jbridge.slf4j2event.Slf4jToLoggingEventExtMapper;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * factory for LoggerExt
 *
 */
public class LoggerExtFactory {

    private ILoggerFactory slf4jLoggerFactory;
    private Slf4jToLoggingEventExtMapper slf4jToEventExtMapper;

    public LoggerExtFactory(ILoggerFactory slf4jLoggerFactory, Slf4jToLoggingEventExtMapper slf4jToEventExtMapper) {
	this.slf4jLoggerFactory = slf4jLoggerFactory;
	this.slf4jToEventExtMapper = slf4jToEventExtMapper;
    }

    public LoggerExt create(String name) {
	Logger slf4jLogger = slf4jLoggerFactory.getLogger(name);
	return wrap(slf4jLogger);
    }

    public LoggerExt create(Class<?> clss) {
	Logger slf4jLogger = slf4jLoggerFactory.getLogger(clss.getName());
	return wrap(slf4jLogger);
    }

    public LoggerExt wrap(Logger slf4jLogger) {
	return new LoggerExt(slf4jLogger, slf4jToEventExtMapper);
    }

    public Slf4jToLoggingEventExtMapper getSlf4jToEventExtMapper() {
        return slf4jToEventExtMapper;
    }
    
}
