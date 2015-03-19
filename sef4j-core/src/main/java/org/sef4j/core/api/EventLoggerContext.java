package org.sef4j.core.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

    private Map<String,EventSender<Object>> appenders = new HashMap<String,EventSender<Object>>();

    private Map<String,PerLoggerContext> perLoggerContexts = new HashMap<String,PerLoggerContext>();
    
    public static class PerLoggerContext {
    	private final String eventLoggerName;
    	Map<String,AppenderRefContext> toAppenderRefs = new HashMap<String,AppenderRefContext>();

    	// parent-child relationship where intermediate node are not always present
    	// example:  "a", "a.b.c"  ... "a.b" is not present,  a.b.c has first ancestor "a"
    	private PerLoggerContext firstAncestor;
    	private Map<String,PerLoggerContext> descendants = new HashMap<String,PerLoggerContext>();
    	
    	// computed field from toAppenderRefs+ recurse on parent PerLoggerContext
    	private EventSender<Object>[] inheritedAppenders;
    	
    	public PerLoggerContext(String eventLoggerName) {
			this.eventLoggerName = eventLoggerName;
		}
    	
    }

    public static class AppenderRefContext {
    	private final String appenderName;
    	boolean addOrRemoveInheritable;
    	// TOADD: EventFilter... => would wrap EventSender per logger?!
    	
		public AppenderRefContext(String appenderName) {
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

    @SuppressWarnings("unchecked")
    public EventLoggerContext() {
    	PerLoggerContext rootLoggerCtx = new PerLoggerContext(ROOT_LOGGER_NAME);
    	rootLoggerCtx.inheritedAppenders = (EventSender<Object>[]) new EventSender<?>[0];
		this.perLoggerContexts.put(ROOT_LOGGER_NAME, rootLoggerCtx);
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

    @SuppressWarnings("unchecked")
    public <T> void addAppender(String appenderName, EventSender<T> eventSender) {
    	synchronized(lock) {
    		if (appenders.get(appenderName) != null) {
    			throw new IllegalArgumentException("appender name '" + appenderName + "' already used");
    		};
            appenders.put(appenderName, (EventSender<Object>) eventSender);
            // nothing to fire event here (cf addLoggerToAppenderRef() instead)
    	}
    }

    public void removeAppender(String appenderName) {
    	synchronized(lock) {
    		EventSender<Object> appender = appenders.remove(appenderName);
    		if (appender == null) {
    			throw new IllegalArgumentException("appender name '" + appenderName + "' not found");
    		};

            // remove all corresponding loggerToAppenderRef + fire events
    		Map<String,PerLoggerContext> loggerToReeval = new TreeMap<String,PerLoggerContext>(); // in sorted order: "a" before "a.b" ...
            for(PerLoggerContext loggerConf : perLoggerContexts.values()) {
            	AppenderRefContext optL2a = loggerConf.toAppenderRefs.get(appenderName);
            	if (optL2a != null) {
            		// removeLoggerToAppenderRef(eventLoggerName, appenderName);
            		loggerConf.toAppenderRefs.remove(appenderName);
            		loggerToReeval.put(loggerConf.eventLoggerName, loggerConf);
            	}
            }
            if (! loggerToReeval.isEmpty()) {
            	for(PerLoggerContext l : loggerToReeval.values()) {
            		reevalInheritedAppendersForDescendentLoggers(l, true);
            	}
            }
    	}
    }

    public void addLoggerToAppenderRef(String eventLoggerName, String appenderName,
    		boolean addOrRemoveInheritable
    		// TOADD: EventFilter...
    		){
    	synchronized(lock) {
    		EventSender<Object> appender = this.appenders.get(appenderName);
    		if (appender == null) {
    			throw new IllegalArgumentException("appender not found");
    		}    		
    		PerLoggerContext loggerCtx = getOrCreateLoggerContext(eventLoggerName);
    		AppenderRefContext toAppenderCtx = loggerCtx.toAppenderRefs.get(appenderName);
    		if (toAppenderCtx != null) {
    			throw new IllegalArgumentException("appender ref already configured for logger");
    		}
    		
    		toAppenderCtx = new AppenderRefContext(appenderName);
    		toAppenderCtx.addOrRemoveInheritable = addOrRemoveInheritable;
    		loggerCtx.toAppenderRefs.put(appenderName, toAppenderCtx);
    	
    		reevalInheritedAppendersForDescendentLoggers(loggerCtx, true);
    	}
    }

    public void removeLoggerToAppenderRef(String eventLoggerName, String appenderName){
    	synchronized(lock) {
    		EventSender<Object> appender = this.appenders.get(appenderName);
    		if (appender == null) {
    			throw new IllegalArgumentException("appender not found");
    		}
    		PerLoggerContext loggerCtx = perLoggerContexts.get(eventLoggerName);
    		if (loggerCtx == null) {
    			throw new IllegalArgumentException("logger not found");
    		}
    		AppenderRefContext toAppenderConf = loggerCtx.toAppenderRefs.get(appenderName);
    		if (toAppenderConf == null) {
    			throw new IllegalArgumentException("appender ref not found for logger");
    		}
    		
    		loggerCtx.toAppenderRefs.remove(appenderName);
    	
    		reevalInheritedAppendersForDescendentLoggers(loggerCtx, true);
    	}
    }

    // SPI, called from EventLoggerFactory
    // ------------------------------------------------------------------------
    
    @SuppressWarnings("unchecked")
    public <E> EventSender<E>[] getInheritedAppendersFor(String eventLoggerName) {
    	synchronized(lock) {
    		PerLoggerContext loggerCxt = findFirstAncestorLoggerContextFor(eventLoggerName);
    		return (EventSender<E>[]) loggerCxt.inheritedAppenders;
    	}
    }

	@SuppressWarnings("unchecked")
    public <E> Map<String, EventSender<E>[]> getInheritedAppendersFor(Set<String> eventLoggerNames) {
		Map<String, EventSender<E>[]> res = new HashMap<String, EventSender<E>[]>();
		synchronized(lock) {
			for(String eventLoggerName : eventLoggerNames) {
	    		PerLoggerContext loggerCxt = findFirstAncestorLoggerContextFor(eventLoggerName);
	    		res.put(eventLoggerName, (EventSender<E>[]) loggerCxt.inheritedAppenders);
			}
    	}
		return res;
	}

	// internal
	// ------------------------------------------------------------------------
	
	private PerLoggerContext getOrCreateLoggerContext(String eventLoggerName) {
		PerLoggerContext loggerCtx = this.perLoggerContexts.get(eventLoggerName);
		if (loggerCtx == null) {
			PerLoggerContext ancestor = findFirstAncestorLoggerContextFor(eventLoggerName);
			
			loggerCtx = new PerLoggerContext(eventLoggerName);
			this.perLoggerContexts.put(eventLoggerName, loggerCtx);
			
			// reeval parent-child ancestor relationships
			
			// transfer appropriate descendants from previous ancestor to new node
			if (ancestor.descendants != null && !ancestor.descendants.isEmpty()) {
				for(Iterator<PerLoggerContext> iter = ancestor.descendants.values().iterator(); iter.hasNext(); ) {
					PerLoggerContext descendant = iter.next();
					if (descendant.eventLoggerName.startsWith(eventLoggerName)) {
						descendant.firstAncestor = loggerCtx;
						loggerCtx.descendants.put(descendant.eventLoggerName, descendant);
						iter.remove();
					}
				}
			}

			// add this loggerCtx in parent
			loggerCtx.firstAncestor = ancestor;
			if (ancestor.descendants == null) {
				ancestor.descendants = new HashMap<String, EventLoggerContext.PerLoggerContext>();
			}
			ancestor.descendants.put(loggerCtx.eventLoggerName, loggerCtx);

		}
		return loggerCtx;
	}

	private PerLoggerContext findFirstAncestorLoggerContextFor(String eventLoggerName) {
		PerLoggerContext res;
		for(String name = eventLoggerName; ; name = parentNameOf(name)) {
			PerLoggerContext tmpres = this.perLoggerContexts.get(name);
			if (tmpres != null) {
				res = tmpres;
				break;
			}
		}
		return res;
	}

	private static String parentNameOf(String loggerName) {
		String res;
		int indexLastDot = loggerName.lastIndexOf('.');
		if (indexLastDot == -1) {
			res = ROOT_LOGGER_NAME;
		} else {
			res = loggerName.substring(0, indexLastDot);
		}
		return res;
	}
	
	@SuppressWarnings("unchecked")
    protected void reevalInheritedAppendersForDescendentLoggers(PerLoggerContext loggerContext, boolean fireChgEvent) {
    	// EventSender[] oldInheritedAppenders = loggerContext.inheritedAppenders;
    	List<EventSender<Object>> tmpAppenders = new ArrayList<EventSender<Object>>();
    	PerLoggerContext ancestorCtx = loggerContext.firstAncestor;
    	if (ancestorCtx != null && ancestorCtx.inheritedAppenders != null) {
    		tmpAppenders.addAll(Arrays.asList(ancestorCtx.inheritedAppenders));
    	}
    	collectAddOrRemoveAppenders(tmpAppenders, loggerContext);
    	EventSender<Object>[] newInheritedAppenders = (EventSender<Object>[]) tmpAppenders.toArray(new EventSender<?>[tmpAppenders.size()]);
    	loggerContext.inheritedAppenders = newInheritedAppenders;
    	boolean fireDescendantChgEvent = false;
    	if (fireChgEvent) {
    		// TOADD OPTIM: may compare for old equals new..
    		fireEventChangeInheritedLoggers(loggerContext.eventLoggerName);
    	}
    	if (loggerContext.descendants != null && !loggerContext.descendants.isEmpty()) {
    		for(PerLoggerContext descendant : loggerContext.descendants.values()) {
    			// *** recurse ***
    			reevalInheritedAppendersForDescendentLoggers(descendant, fireDescendantChgEvent);
    		}
    	}
    }
    
    protected void fireEventChangeInheritedLoggers(String eventLoggerName) {
        for(EventLoggerContextListener listener : contextListeners) {
            try {
                listener.onChangeInheritedLoggers(eventLoggerName);
            } catch(Exception ex) {
                LOG.error("Failed to notify listener.onChangeInheritedLoggers() ... ignore, no rethrow!", ex);
            }
        }
    }

	private void collectAddOrRemoveAppenders(List<EventSender<Object>> resAppenders, PerLoggerContext eventLoggerConf) {
		for(AppenderRefContext l2a : eventLoggerConf.toAppenderRefs.values()) {
			String appenderName = l2a.getAppenderName();
			EventSender<Object> appender = appenders.get(appenderName);
			if (l2a.addOrRemoveInheritable) {
				// add
				resAppenders.add(appender);		
			} else {
				// remove
				resAppenders.remove(appender);
			}
		}
	}

}
