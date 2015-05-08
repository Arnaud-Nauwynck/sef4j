package org.sef4j.core.api.logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.logger.EventLoggerContext.EventLoggerContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * similar to slf4j org.slf4j.LoggerFactory, but for event ...
 * 
 * This class is a factory and the container manager for all created EventLogger elements.
 * This class uses EventLoggerContext to determine which EventSenders to attach to EventLoggers.
 * The EventContext may be changed at runtime for a EventLoggerFactory
 * 
 * it is preferable to use this class and EventLogger injected by an IOC container
 * ... But you can also use default static instance, store in static singleton EventLoggerFactoryStaticBinder
 *  
 */
public class EventLoggerFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(EventLoggerFactory.class);

	@SuppressWarnings("unchecked")
    private static final EventSender<Object>[] EMPTY_EVENT_SENDER_ARRAY = (EventSender<Object>[]) new EventSender<?>[0];
	
	private EventLoggerContext eventLoggerContext;
	
	private Object lock = new Object();
	
	private Map<String,EventLogger> eventLoggers = new HashMap<String,EventLogger>();
	
	private EventLoggerContextListener innerEventLoggerContextListener = new EventLoggerContextListener() {
        @Override
        public void onChangeInheritedLoggers(String eventLoggerName) {
        	onEventLoggerContext_ChangeInheritedLoggers(eventLoggerName);
        }
    };
    
	// ------------------------------------------------------------------------
	
	public EventLoggerFactory(EventLoggerContext eventLoggerContext) {
		this.eventLoggerContext = eventLoggerContext;
		this.eventLoggerContext.addContextListener(innerEventLoggerContextListener);
	}

	// API
	// ------------------------------------------------------------------------

	public EventLogger getEventLogger(String eventLoggerName) {
		EventLogger res;
		synchronized(lock) {
			res = eventLoggers.get(eventLoggerName);
		}
		if (res == null) {
			EventSender<Object>[] inheritedAppenders = safeGetInheritedAppenderFor(eventLoggerName);
			synchronized(lock) {
				res = eventLoggers.get(eventLoggerName);
				if (res == null) {
					res = new EventLogger(this, eventLoggerName, inheritedAppenders);
					eventLoggers.put(eventLoggerName, res);
				}
			}
		}
		return res;
	}
	
	// SPI
	// ------------------------------------------------------------------------
	
	public void resetContext(EventLoggerContext newContext) {
		if (newContext == null) throw new IllegalArgumentException();
		LOG.info("EventLoggerFactory.resetContext() ...");
		synchronized(lock) {
			// unconfigure from previous context
			if (this.eventLoggerContext != null) {
				doCleanupForEventContext();
			}
			
			// do set singleton context
			this.eventLoggerContext = newContext;
			
			// reconfigure for new context
			doInitializeForEventContext();
			
		}// end synchronized
		LOG.info("... done EventLoggerFactory.resetContext()");
	}

	// internal
	// ------------------------------------------------------------------------
	
	private void doInitializeForEventContext() {
		// => recompute, attach all appenders on all already created eventLoggers
	    if (!eventLoggers.isEmpty()) {
			LOG.info("attach appenders for " + eventLoggers.size() + " existing eventLogger(s)");
			doReconfigureAppenderForLoggers(eventLoggers);
	    }
		this.eventLoggerContext.addContextListener(innerEventLoggerContextListener);
	}

	private void doCleanupForEventContext() {
	    this.eventLoggerContext.removeContextListener(innerEventLoggerContextListener);
	    if (!eventLoggers.isEmpty()) {
		    LOG.info("detach appenders for " + eventLoggers.size() + " existing eventLogger(s)");
			// => detach all appenders on all eventLoggers
			for(EventLogger eventLogger : eventLoggers.values()) {
				eventLogger.configureInheritedAppenders(EMPTY_EVENT_SENDER_ARRAY);
			}
	    }
	}
	
    protected EventSender<Object>[] safeGetInheritedAppenderFor(String eventLoggerName) {
		EventSender<Object>[] inheritedAppenders;
		try {
			inheritedAppenders = eventLoggerContext.getInheritedAppendersFor(eventLoggerName);
		} catch(Exception ex) {
			inheritedAppenders = EMPTY_EVENT_SENDER_ARRAY;
			LOG.error("Failed to compute inherited appenders for " + eventLoggerName + "! .. use empty, no rethrow ex", ex);
			// ignore, no rethrow!
		}
		return inheritedAppenders;
	}

	protected Map<String,EventSender<Object>[]> safeGetInheritedAppenderFor(Set<String> eventLoggerNames) {
		Map<String,EventSender<Object>[]> res;
		try {
			res = eventLoggerContext.getInheritedAppendersFor(eventLoggerNames);		
		} catch(Exception ex) {
			// should not occurs... but fallback to safe loop
			res = new HashMap<String,EventSender<Object>[]>();
			for(String eventLoggerName : eventLoggerNames) {
				res.put(eventLoggerName, safeGetInheritedAppenderFor(eventLoggerName));
			}
		}
		return res;
	}

	protected void onEventLoggerContext_ChangeInheritedLoggers(String parentEventLoggerName) {
		synchronized(lock) {
			// collect descendant loggerNames
			Map<String,EventLogger> descendantLoggers = new HashMap<String,EventLogger>();
			for(EventLogger eventLogger : eventLoggers.values()) {
				String name = eventLogger.getEventLoggerName();
				if (name.startsWith(parentEventLoggerName)
						&& (name.length() == parentEventLoggerName.length()
						|| name.charAt(parentEventLoggerName.length()) == '.')) {
					// matching descendant
					descendantLoggers.put(name, eventLogger);
				}
			}
			// reconfigure descendant loggers
			doReconfigureAppenderForLoggers(descendantLoggers);
		}
	}
	
	private void doReconfigureAppenderForLoggers(Map<String,EventLogger> loggers) {
		Map<String,EventSender<Object>[]> appendersPerLogger = safeGetInheritedAppenderFor(loggers.keySet());
		for(EventLogger eventLogger : loggers.values()) {
			String eventLoggerName = eventLogger.getEventLoggerName();
			EventSender<Object>[] appenders = appendersPerLogger.get(eventLoggerName);
			if (appenders == null) {
				// should not occur
				appenders = safeGetInheritedAppenderFor(eventLoggerName);
			}
			eventLogger.configureInheritedAppenders(appenders);
		}
	}

}
