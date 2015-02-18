package org.sef4j.core.api;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * similar to slf4j org.slf4j.Logger, but for events ...
 */
public class EventLogger {
	
	private final Logger LOG;
	
	private final String eventLoggerName;

	/**
	 * copy-on-write array of inherited appenders (from parent EventLogger + own appender)
	 * this is managed from class EventLoggerContext, from configuration(at startup / re-init of context)
	 */
	private EventAppender[] inheritedAppenders; 

	// ------------------------------------------------------------------------
	
	/* package protected, created and managed from EventLoggerContext */
	/* pp */ EventLogger(String eventLoggerName, EventAppender[] inheritedAppenders) {
		this.eventLoggerName = eventLoggerName;
		this.LOG = LoggerFactory.getLogger(eventLoggerName);
		this.inheritedAppenders = inheritedAppenders;
	}

	public static EventLogger getEventLogger(String eventLoggerName) {
		return EventLoggerFactory.getEventLogger(eventLoggerName);
	}

	public static EventLogger getEventLogger(Class<?> clss) {
		return EventLoggerFactory.getEventLogger(clss.getName());
	}

	/*pp*/ void configureInheritedAppenders(EventAppender[] inheritedLoggerAppenders) {
		this.inheritedAppenders = inheritedLoggerAppenders;
	}
	
	// ------------------------------------------------------------------------


	public String getEventLoggerName() {
		return eventLoggerName;
	}

	
	public void sendEvent(Object event) {
		final EventAppender[] appenders = inheritedAppenders; 
		final int len = appenders.length;
		for (int i = 0; i < len; i++) {
			try {
				appenders[i].handleEvent(event);
			} catch(Exception ex) {
				LOG.error("Failed to handleEvent on eventLogger '" + eventLoggerName + "' to appender " + appenders[i]);
			}
		}
	}

	public void sendEvents(Collection<Object> events) {
		for(Object event : events) {
			sendEvent(event);
		}
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "EventLogger[" + eventLoggerName + "]";
	}

}
