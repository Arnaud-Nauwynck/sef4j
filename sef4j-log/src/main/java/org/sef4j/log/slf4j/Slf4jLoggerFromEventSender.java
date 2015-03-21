package org.sef4j.log.slf4j;

import java.util.Collection;

import org.sef4j.core.api.EventSender;
import org.slf4j.Logger;


/**
 * adapter EventSender -> Slf4j
 */
public class Slf4jLoggerFromEventSender<T> implements EventSender<T> {
	
	private Logger slf4jLogger;
	
	/**
	 * converter for evnt object to method call of <code>slf4jLogger.loXX(YY, ZZ)</code>
	 */
//	private TypeHierarchyToObjectMap<T>
	
    // ------------------------------------------------------------------------
    
	public Slf4jLoggerFromEventSender() {
	}

    // ------------------------------------------------------------------------

	@Override
	public void sendEvent(T event) {
		//TODO
		
	}

    @Override
    public void sendEvents(Collection<T> events) {
        for(T event : events) {
        	
        }
    }

	
}
