package org.sef4j.core.api;

import java.util.HashMap;
import java.util.Map;

import org.sef4j.core.api.EventLoggerContext.EventLoggerContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * similar to slf4j org.slf4j.LoggerFactory, but for event ...
 * 
 * This class is a factory and the container manager for all created EventLogger elements.
 * This class uses EventLoggerContext to attach EventSenders to EventLoggers.
 * The EventContext may be changed at runtime for a EventLoggerFactory
 * 
 * it is preferable to use this class and EventLogger injected by an IOC container
 * ... But you can also use default static instance, store in static singleton EventLoggerFactoryStaticBinder
 *  
 */
public class EventLoggerFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(EventLoggerFactory.class);
	
	private EventLoggerContext eventLoggerContext;
	
	private Object lock = new Object();
	
	private Map<String,EventLogger> eventLoggers = new HashMap<String,EventLogger>();
	
	private EventLoggerContextListener innerEventLoggerContextListener = new EventLoggerContextListener() {
        @Override
        public void onChangeInheritedLoggers(String eventLoggerName) {
            // TODO Auto-generated method stub
            
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
			EventSender[] inheritedAppenders = safeGetInheritedAppenderFor(eventLoggerName);
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
				cleanupForEventContext();
			}
			
			// do set singleton context
			this.eventLoggerContext = newContext;
			
			// reconfigure for new context
			initializeForEventContext();
			
		}// end synchronized
		LOG.info("... done EventLoggerFactory.resetContext()");
	}

	private void initializeForEventContext() {
		// => recompute, attach all appenders on all already created eventLoggers
	    if (!eventLoggers.isEmpty()) {
			LOG.info("attach appenders for " + eventLoggers.size() + " existing eventLogger(s)");
			for(EventLogger eventLogger : eventLoggers.values()) {
				String eventLoggerName = eventLogger.getEventLoggerName();
				EventSender[] appenders = safeGetInheritedAppenderFor(eventLoggerName);
				eventLogger.configureInheritedAppenders(appenders);
			}
	    }
		this.eventLoggerContext.addContextListener(innerEventLoggerContextListener);
	}

	private void cleanupForEventContext() {
	    this.eventLoggerContext.removeContextListener(innerEventLoggerContextListener);
	    if (!eventLoggers.isEmpty()) {
		    LOG.info("detach appenders for " + eventLoggers.size() + " existing eventLogger(s)");
			// => detach all appenders on all eventLoggers
			EventSender[] emptyAppenders = new EventSender[0];  
			for(EventLogger eventLogger : eventLoggers.values()) {
				eventLogger.configureInheritedAppenders(emptyAppenders);
			}
	    }
	}
	
	// internal
	// ------------------------------------------------------------------------
	
	protected EventSender[] safeGetInheritedAppenderFor(String eventLoggerName) {
		EventSender[] inheritedAppenders;
		try {
			inheritedAppenders = eventLoggerContext.getInheritedAppendersFor(eventLoggerName);
		} catch(Exception ex) {
			inheritedAppenders = new EventSender[0];
			LOG.error("Failed to compute inherited appenders for " + eventLoggerName + "! .. use empty, no rethrow ex", ex);
			// ignore, no rethrow!
		}
		return inheritedAppenders;
	}

}
