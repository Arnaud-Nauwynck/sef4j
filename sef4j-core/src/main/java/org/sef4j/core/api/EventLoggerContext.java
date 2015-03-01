package org.sef4j.core.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class EventLoggerContext {

    public static interface EventLoggerContextListener {
        //        public void onStop();
        //        public void onStart();
        public void onChangeInheritedLoggers(String eventLoggerName);
    }


    private static final Logger LOG = LoggerFactory.getLogger(EventLoggerContext.class);

    public static final String ROOT_LOGGER_NAME = "";
    
    private Object lock = new Object();
    private List<EventLoggerContextListener> contextListeners = new ArrayList<EventLoggerContextListener>();

    private Map<String,EventSender> appenders = new HashMap<String,EventSender>();

    private Map<String,EventLoggerConf> loggerConfs = new HashMap<String,EventLoggerConf>();
    
    public static class EventLoggerConf {
    	private final String eventLoggerName;
    	Map<String,EventLoggerToAppenderConf> toAppenderRefs = new HashMap<String,EventLoggerToAppenderConf>();
		
    	public EventLoggerConf(String eventLoggerName) {
			this.eventLoggerName = eventLoggerName;
		}
    	
    }

    public static class EventLoggerToAppenderConf {
    	private final String appenderName;
    	boolean addOrRemoveInheritable;
    	// TOADD: EventFilter... => would wrap EventSender per logger?!
    	
		public EventLoggerToAppenderConf(String appenderName) {
			this.appenderName = appenderName;
		}
		
		public String getAppenderName() {
			return appenderName;
		}

		public boolean getAddOrRemoveInheritable() {
			return addOrRemoveInheritable;
		}

		public void setAddOrRemoveInheritable(boolean addOrRemoveInheritable) {
			this.addOrRemoveInheritable = addOrRemoveInheritable;
		}
    	
    	
    }

    // ------------------------------------------------------------------------

    public EventLoggerContext() {
    }

    // ------------------------------------------------------------------------

    public void addContextListener(EventLoggerContextListener listener) {
        synchronized(lock) {
            contextListeners.add(listener);
        }
    }

    public void removeContextListener(EventLoggerContextListener listener) {
        synchronized(lock) {
            contextListeners.remove(listener);
        }
    }

    protected void fireEventChangeInheritedLoggers(String eventLoggerName) {
        synchronized(lock) {
            for(EventLoggerContextListener listener : contextListeners) {
                try {
                    listener.onChangeInheritedLoggers(eventLoggerName);
                } catch(Exception ex) {
                    LOG.error("Failed to notify listener.onChangeInheritedLoggers() ... ignore, no rethrow!", ex);
                }
            }
        }
    }

    public void addAppender(String appenderName, EventSender eventSender) {
    	synchronized(lock) {
    		if (appenders.get(appenderName) != null) {
    			throw new IllegalArgumentException("appender name '" + appenderName + "' already used");
    		};
            appenders.put(appenderName, eventSender);
            // nothing to fire event here (cf addLoggerToAppenderRef() instead)
    	}
    }

    public void removeAppender(String appenderName) {
    	synchronized(lock) {
    		EventSender appender = appenders.remove(appenderName);
    		if (appender == null) {
    			throw new IllegalArgumentException("appender name '" + appenderName + "' not found");
    		};

            // remove all corresponding loggerToAppenderRef + fire events
            for(EventLoggerConf loggerConf : loggerConfs.values()) {
            	EventLoggerToAppenderConf optL2a = loggerConf.toAppenderRefs.get(appenderName);
            	if (optL2a != null) {
            		// removeLoggerToAppenderRef(eventLoggerName, appenderName);
            		loggerConf.toAppenderRefs.remove(appenderName);
            		
            		fireEventChangeInheritedLoggers(loggerConf.eventLoggerName);
            	}
            }
    	}
    }

    public void addLoggerToAppenderRef(String eventLoggerName, String appenderName,
    		boolean addOrRemoveInheritable
    		// TOADD: EventFilter...
    		){
    	synchronized(lock) {
    		EventSender appender = this.appenders.get(appenderName);
    		if (appender == null) {
    			throw new IllegalArgumentException("appender not found");
    		}    		
    		EventLoggerConf eventLoggerConf = getOrCreateLoggerConf(eventLoggerName);
    		EventLoggerToAppenderConf toAppenderConf = eventLoggerConf.toAppenderRefs.get(appenderName);
    		if (toAppenderConf != null) {
    			throw new IllegalArgumentException("appender ref already configured for logger");
    		}
    		
    		toAppenderConf = new EventLoggerToAppenderConf(appenderName);
    		toAppenderConf.addOrRemoveInheritable = addOrRemoveInheritable;
    		eventLoggerConf.toAppenderRefs.put(appenderName, toAppenderConf);
    	
    		fireEventChangeInheritedLoggers(eventLoggerName);
    	}
    }

    public void removeLoggerToAppenderRef(String eventLoggerName, String appenderName){
    	synchronized(lock) {
    		EventSender appender = this.appenders.get(appenderName);
    		if (appender == null) {
    			throw new IllegalArgumentException("appender not found");
    		}
    		EventLoggerConf eventLoggerConf = getOrCreateLoggerConf(eventLoggerName);
    		EventLoggerToAppenderConf toAppenderConf = eventLoggerConf.toAppenderRefs.get(appenderName);
    		if (toAppenderConf == null) {
    			throw new IllegalArgumentException("appender ref not found for logger");
    		}
    		
    		eventLoggerConf.toAppenderRefs.remove(appenderName);
    	
    		fireEventChangeInheritedLoggers(eventLoggerName);
    	}
    }

    
	private EventLoggerConf getOrCreateLoggerConf(String eventLoggerName) {
		EventLoggerConf eventLoggerConf = this.loggerConfs.get(eventLoggerName);
		if (eventLoggerConf == null) {
			eventLoggerConf = new EventLoggerConf(eventLoggerName);
			this.loggerConfs.put(eventLoggerName, eventLoggerConf);
		}
		return eventLoggerConf;
	}
    

    // SPI, called from EventLoggerFactory
    // ------------------------------------------------------------------------
    
    public EventSender[] getInheritedAppendersFor(String eventLoggerName) {
    	Map<String,EventSender> appenders = new LinkedHashMap<String,EventSender>();
    	synchronized(lock) {
    		recursiveGetInheritedAppendersFor(eventLoggerName, appenders);
    	}
        return appenders.values().toArray(new EventSender[appenders.size()]);
    }

	protected void recursiveGetInheritedAppendersFor(String eventLoggerName, Map<String, EventSender> appenders2) {
		int dotIndex = eventLoggerName.lastIndexOf('.');
		if (dotIndex != -1) {
			String parentLoggerName = eventLoggerName.substring(0, dotIndex-1);
			
			// *** recurse ***
			recursiveGetInheritedAppendersFor(parentLoggerName, appenders);
			
			EventLoggerConf eventLoggerConf = this.loggerConfs.get(eventLoggerName);
			if (eventLoggerConf != null) {
				// override inheritable
				for(EventLoggerToAppenderConf l2a : eventLoggerConf.toAppenderRefs.values()) {
					String appenderName = l2a.getAppenderName();
					if (l2a.addOrRemoveInheritable) {
						// add
						EventSender appender = appenders.get(appenderName);
						appenders.put(appenderName, appender);		
					} else {
						// remove
						appenders.remove(appenderName);
					}
				}
			}// else, no override for eventLogger
		} else {
			// root
			EventLoggerConf rootLoggerConf = this.loggerConfs.get(ROOT_LOGGER_NAME);
			for(EventLoggerToAppenderConf l2a : rootLoggerConf.toAppenderRefs.values()) {
				String appenderName = l2a.getAppenderName();
				if (l2a.addOrRemoveInheritable) {
					// add
					EventSender appender = appenders.get(appenderName);
					appenders.put(appenderName, appender);		
				} else {
					// remove (should not occurs ... no effect anyway!)
					appenders.remove(appenderName);
				}
			}
		}
	}
    
}
