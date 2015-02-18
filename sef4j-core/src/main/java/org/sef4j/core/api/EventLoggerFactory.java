package org.sef4j.core.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sef4j.core.api.EventLoggerContext.EventLoggerContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * similar to slf4j org.slf4j.LoggerFactory, but for event ...
 * 
 * This class is a factory and the container manager for all created EventLogger elements.
 * This class uses EventLoggerContext to attach EventAppenders to EventLoggers.
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
	
	private Set<String> pendingInitEventLoggers = new HashSet<String>();

	private Map<String,EventAppender> eventAppenders = new HashMap<String,EventAppender>();

	private EventLoggerContextListener innerEventLoggerContextListener = new EventLoggerContextListener() {
        @Override
        public void onChangeInheritedLoggers(String eventLoggerName) {
            // TODO Auto-generated method stub
            
        }
    };
    
	// ------------------------------------------------------------------------
	
	public EventLoggerFactory(EventLoggerContext eventLoggerContext) {
		this.eventLoggerContext = eventLoggerContext;
		this.eventLoggerContext.addListener(innerEventLoggerContextListener);
	}

	// API
	// ------------------------------------------------------------------------

	public EventLogger getEventLogger(String eventLoggerName) {
		return getEventLogger_nonBlockingRead(eventLoggerName);
		// return getEventLogger_naiveBlockingRead(eventLoggerName);
	}
	
	// SPI
	// ------------------------------------------------------------------------
	
	public void resetContext(EventLoggerContext newContext) {
		if (newContext == null) throw new IllegalArgumentException();
		LOG.info("EventLoggerFactory.resetContext() ...");
		synchronized(lock) {
			// step 1: wait all pending creations!
			while(!pendingInitEventLoggers.isEmpty()) {
				LOG.info("wait for " + pendingInitEventLoggers.size() + " pending EventLogger(s) creation");
				try {
					lock.wait();
				} catch (InterruptedException e) {
				}
			}
			
			// step 2: unconfigure from previous context
			if (this.eventLoggerContext != null) {
				cleanupForEventContext();
			}
			
			// do set singleton context
			this.eventLoggerContext = newContext;
			
			// step 3: reconfigure for new context
			initializeForEventContext();
			
		}// end synchronized
		LOG.info("... done EventLoggerFactory.resetContext()");
	}

	private void initializeForEventContext() {
		// => recompute, attach+start all appenders on all already created eventLoggers
		for(EventLogger eventLogger : eventLoggers.values()) {
			String eventLoggerName = eventLogger.getEventLoggerName();
			EventAppender[] appenders = safeGetAndStartInheritedAppenderFor(eventLoggerName);
			eventLogger.configureInheritedAppenders(appenders);
		}
		// start new appenders.. cf done in safeGetAndStart..()
		this.eventLoggerContext.addListener(innerEventLoggerContextListener);
	}

	private void cleanupForEventContext() {
	    this.eventLoggerContext.removeListener(innerEventLoggerContextListener);
	    LOG.info("detach appenders for " + eventLoggers.size() + " existing eventLogger(s)");
		// => detach+stop all appenders on all eventLoggers
		EventAppender[] emptyAppenders = new EventAppender[0];  
		for(EventLogger eventLogger : eventLoggers.values()) {
			eventLogger.configureInheritedAppenders(emptyAppenders);
		}
		// stop old appenders
		LOG.info("stop " + eventAppenders.size() + " existing appender(s)");
		for(EventAppender appender : eventAppenders.values()) {
			if (appender.isStarted()) {
				try {
					appender.stop();
					appender.setStarted(false);
				} catch(Exception ex) {
					LOG.error("Failed to stop appender '" + appender.getAppenderName() + "' .. ignore, no rethrow!", ex);
					// appender.setStarted(false); // may mark anyway as (tryed-)stopped ?? 
				}
			}
		}
	}
	
	// internal
	// ------------------------------------------------------------------------
	
	protected EventLogger getEventLogger_naiveBlockingRead(String eventLoggerName) {
		EventLogger res;
		synchronized(lock) {
			res = eventLoggers.get(eventLoggerName);
			// for simplicity... block all during create .. cf other implementation 
			if (res == null) {
				res = doCreateEventLogger(eventLoggerName);
				eventLoggers.put(eventLoggerName, res);
			}
		}
		return res;
	}
	
	protected EventLogger getEventLogger_nonBlockingRead(String eventLoggerName) {
		EventLogger res;
		boolean pendingInit = false;
		synchronized(lock) {
			res = eventLoggers.get(eventLoggerName);
			// for simplicity... could compute and create EventLogger within shared blocking lock... 
			if (res == null) {
				pendingInit = ! pendingInitEventLoggers.add(eventLoggerName);
			}
		}
		if (res == null) {
			if (pendingInit) {
				// do create.. then notify created for other threads
				res = doCreateEventLogger(eventLoggerName);
				synchronized(lock) {
					pendingInitEventLoggers.remove(eventLoggerName);
					eventLoggers.put(eventLoggerName, res);
					lock.notifyAll();
				}
			} else {
				// wait for created by other thread..
				while(true) {
					synchronized(lock) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
						}
						res = eventLoggers.get(eventLoggerName);
					}
				}
			}
		}
		return res;
	}


	protected EventLogger doCreateEventLogger(String eventLoggerName) {
		EventAppender[] inheritedAppenders = safeGetAndStartInheritedAppenderFor(eventLoggerName);
		return new EventLogger(this, eventLoggerName, inheritedAppenders);
	}

	protected EventAppender[] safeGetAndStartInheritedAppenderFor(String eventLoggerName) {
		EventAppender[] inheritedAppenders;
		try {
			inheritedAppenders = eventLoggerContext.getInheritedAppendersFor(eventLoggerName);
		} catch(Exception ex) {
			inheritedAppenders = new EventAppender[0]; 
			LOG.error("Failed to compute inherited appenders for " + eventLoggerName + "! .. use empty, no rethrow ex", ex);
			// ignore, no rethrow!
		}
		
		// register Appenders if not already seen, and start() it
		for(EventAppender appender : inheritedAppenders) {
			String appenderName = appender.getAppenderName(); 
			// jdk version>=8:  eventAppenders.putIfAbsent(appenderName, appender);
			if (eventAppenders.get(appenderName) == null) {
				eventAppenders.put(appenderName, appender);
				
				LOG.info("start appender '" + appenderName + "'");
				try {
					appender.start();
					appender.setStarted(true);
				} catch(Exception ex) {
					appender.setStarted(false);
					LOG.error("Failed to start appender '" + appenderName + "' ... ignore, no rethrow!", ex);
					// ignore, no rethrow!
				}
			}
		}
		
		return inheritedAppenders;
	}

}
