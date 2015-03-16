package org.sef4j.log.slf4j;

import java.util.Collection;

import org.sef4j.core.api.EventSender;


/**
 * adapter EventLogger -> Slf4j
 */
public class Slf4jAdapterEventSender implements EventSender<Object> {
	
    // ------------------------------------------------------------------------
    
	public Slf4jAdapterEventSender() {
	}

    // ------------------------------------------------------------------------

	@Override
	public void sendEvent(Object event) {
		//TODO
		
	}

    @Override
    public void sendEvents(Collection<Object> events) {
        
    }

	
}
