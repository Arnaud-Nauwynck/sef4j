package org.sef4j.log.slf4j;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

public class BasicConfigureTstHelper {

	public static LoggerContext newLoggerContextWithAppender(
			String loggerName,
			String appenderName,
			Appender<ILoggingEvent> appender) {
		LoggerContext lc = new LoggerContext();
		configureAddAppender(lc, loggerName, appenderName, appender);
		lc.start();
		return lc;
	}
	
	public static void configureAddAppender(LoggerContext lc, 
			String loggerName,
			String appenderName,
			Appender<ILoggingEvent> appender) {
	    if (appenderName != null) {
	    	appender.setName(appenderName);
	    }
	    appender.setContext(lc);
	    appender.start();
	    
	    Logger logger = lc.getLogger(loggerName != null? loggerName : Logger.ROOT_LOGGER_NAME);
	    logger.addAppender(appender);
	  }

}
