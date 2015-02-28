package org.sef4j.core.api;

/**
 * 
 */
public abstract class EventAppender {

	private final String appenderName;
	
	private boolean started;
	
	// ------------------------------------------------------------------------
	
	public EventAppender(String appenderName) {
		this.appenderName = appenderName;
	}

	// ------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public abstract void handleEvent(Object event);

	// ------------------------------------------------------------------------
	
	public final String getAppenderName() {
		return appenderName;
	}
	
	/** called from owner EventLoggerFactory */ 
	/* pp */ final void setStarted(boolean started) {
		this.started = started;
	}
	
	public final boolean isStarted() {
		return started;
	}

	/** SPI overridable for life-cycle management */
	public void onAttach() {
	}

	/** SPI overridable for life-cycle management */
	public void start() {
	}
	
	/** SPI overridable for life-cycle management */
	public void stop() {
	}
	
	/** SPI overridable for life-cycle management */
	public void onDetach() {
	}

	
}
