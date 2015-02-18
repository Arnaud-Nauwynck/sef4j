package org.sef4j.core.api;

import java.util.ArrayList;
import java.util.List;

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

    private Object lock = new Object();
    private List<EventLoggerContextListener> listeners = new ArrayList<EventLoggerContextListener>();

    // ------------------------------------------------------------------------

    public EventLoggerContext() {
    }

    // ------------------------------------------------------------------------

    public void addListener(EventLoggerContextListener listener) {
        synchronized(lock) {
            listeners.add(listener);
        }
    }

    public void removeListener(EventLoggerContextListener listener) {
        synchronized(lock) {
            listeners.remove(listener);
        }
    }

    protected void fireEventChangeInheritedLoggers(String eventLoggerName) {
        synchronized(lock) {
            for(EventLoggerContextListener listener : listeners) {
                try {
                    listener.onChangeInheritedLoggers(eventLoggerName);
                } catch(Exception ex) {
                    LOG.error("Failed to notify listener.onChangeInheritedLoggers() ... ignore, no rethrow!", ex);
                }
            }
        }
    }

    public EventAppender[] getInheritedAppendersFor(String eventLoggerName) {
        List<EventAppender> appenders = new ArrayList<EventAppender>();
        String[] pathElts = eventLoggerName.split("\\.");
        StringBuilder currName = new StringBuilder();
        for(String pathElt : pathElts) {
            
        }
        return appenders.toArray(new EventAppender[appenders.size()]);
    }

    // ------------------------------------------------------------------------

    
    
}
