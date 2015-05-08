package org.sef4j.core.api.logger;

import java.util.Collection;

import org.sef4j.core.api.EventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * similar to slf4j org.slf4j.Logger, but for events ...
 * <p/>
 * 
 * this class is owned by eventLoggerFactory : life-cycle start()/stop() is managed by owner, 
 * instances are re-configured if context change, but instance are never "disposed" once used.<BR/>
 *
 * 
 * When choosing to use the default StaticBinder singleton, it is safe to use code like this:
 * <code>
 *    public static final EventLogger EVENT_LOGGER = EventLogger.getStatic(..);
 *    // .. which is simply an alias for EventLoggerFactoryStaticBinder.getInstance().getEventLogger(..);
 * </code>
 * because EventLogger are still "owned" by the default static EventLoggerFactory, and may be reconfigured at runtime with new appenders
 * 
 */
public final class EventLogger implements EventSender<Object> {
	
	private final Logger LOG;
	
	private final EventLoggerFactory eventLoggerFactory;
	
	private final String eventLoggerName;

	/**
	 * copy-on-write array of inherited appenders (from parent EventLogger + own appender)
	 * this is managed from class EventLoggerContext, from configuration(at startup / re-init of context)
	 */
	private EventSender<Object>[] inheritedAppenders; 

	// ------------------------------------------------------------------------
	
	/* package protected, created and managed from EventLoggerContext */
	/* pp */ EventLogger(EventLoggerFactory eventLoggerFactory, String eventLoggerName, EventSender<Object>[] inheritedAppenders) {
		this.eventLoggerFactory = eventLoggerFactory;
		this.eventLoggerName = eventLoggerName;
		this.LOG = LoggerFactory.getLogger(eventLoggerName);
		this.inheritedAppenders = inheritedAppenders;
	}

	public static EventLogger getStatic(String eventLoggerName) {
		return EventLoggerFactoryStaticBinder.getInstance().getEventLogger(eventLoggerName);
	}

	public static EventLogger getStatic(Class<?> clss) {
		return getStatic(clss.getName());
	}

	/*pp*/ void configureInheritedAppenders(EventSender<Object>[] inheritedLoggerAppenders) {
		this.inheritedAppenders = inheritedLoggerAppenders;
	}
	
	// ------------------------------------------------------------------------
	
	public EventLoggerFactory getEventLoggerFactory() {
		return eventLoggerFactory;
	}

	public String getEventLoggerName() {
		return eventLoggerName;
	}
	

	public void sendEvent(Object event) {
		final EventSender<Object>[] appenders = inheritedAppenders; 
		final int len = appenders.length;
		for (int i = 0; i < len; i++) {
			try {
				appenders[i].sendEvent(event);
			} catch(Exception ex) {
				LOG.error("Failed to sendEvent on eventLogger '" + eventLoggerName + "' to appender " + appenders[i] 
						+ ", ex:" + ex.getMessage() + " ... ignore, no rethrow!");
			}
		}
	}

	public void sendEvents(Collection<Object> events) {
		final EventSender<Object>[] appenders = inheritedAppenders; 
		final int len = appenders.length;
		for (int i = 0; i < len; i++) {
			try {
				appenders[i].sendEvents(events);
			} catch(Exception ex) {
				LOG.error("Failed to sendEvents on eventLogger '" + eventLoggerName + "' to appender " + appenders[i] 
						+ ", ex:" + ex.getMessage() + " ... ignore, no rethrow!");
			}
		}
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "EventLogger[" + eventLoggerName + "]";
	}

}
