package org.sef4j.log.slf4j.slf4j2event;


/**
 * 
 */
public final class Slf4jAppenderEventMask {
    
	private boolean mask;
    
	private LoggingEventExt richLoggingEventExt;

	public Slf4jToLoggingEventExtMapper eventMapper;
    
	// ------------------------------------------------------------------------
	
    public Slf4jAppenderEventMask(boolean mask, LoggingEventExt richLoggingEventExt, Slf4jToLoggingEventExtMapper eventMapper) {
		this.mask = mask;
		this.richLoggingEventExt = richLoggingEventExt;
		this.eventMapper = eventMapper;
	}
    
    // ------------------------------------------------------------------------
    
    public boolean isMask() {
    	return mask;
    }
    
    public LoggingEventExt getRichLoggingEventExt() {
    	return richLoggingEventExt;
    }
    
    public Slf4jToLoggingEventExtMapper getEventMapper() {
		return eventMapper;
	}

	public Slf4jAppenderEventMask getCopy() {
    	return new Slf4jAppenderEventMask(mask, richLoggingEventExt, eventMapper);
    }

	public void set(Slf4jAppenderEventMask src) {
    	this.mask = src.mask;
    	this.richLoggingEventExt = src.richLoggingEventExt;
    }
	
}