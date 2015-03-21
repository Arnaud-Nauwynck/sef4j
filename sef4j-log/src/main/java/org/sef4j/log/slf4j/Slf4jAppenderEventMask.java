package org.sef4j.log.slf4j;

/**
 * 
 */
public final class Slf4jAppenderEventMask {
    
	private boolean mask;
    
	private LoggingEventExt richLoggingEventExt;
    
	// ------------------------------------------------------------------------
	
    public Slf4jAppenderEventMask(boolean mask, LoggingEventExt richLoggingEventExt) {
		super();
		this.mask = mask;
		this.richLoggingEventExt = richLoggingEventExt;
	}
    
    // ------------------------------------------------------------------------
    
    public boolean isMask() {
    	return mask;
    }
    
    public LoggingEventExt getRichLoggingEventExt() {
    	return richLoggingEventExt;
    }
    
    
    public Slf4jAppenderEventMask getCopy() {
    	return new Slf4jAppenderEventMask(mask, richLoggingEventExt);
    }

	public void set(Slf4jAppenderEventMask src) {
    	this.mask = src.mask;
    	this.richLoggingEventExt = src.richLoggingEventExt;
    }
	
}