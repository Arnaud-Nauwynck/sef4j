package com.google.code.joto.eventrecorder;

import java.io.Serializable;

/**
 *
 */
public final class RecordEventData implements Serializable {

    /** internal for java.io.Serializable */
    private static final long serialVersionUID = 1L;

    private RecordEventSummary eventSummary;
    
    private Object objectData;
    
    // ------------------------------------------------------------------------

	public RecordEventData(RecordEventSummary eventSummary, Object objectData) {
		this.eventSummary = eventSummary;
		this.objectData = objectData;
	}

	// ------------------------------------------------------------------------

	public int getEventId() {
		return eventSummary.getEventId();
	}
	
	public RecordEventSummary getEventSummary() {
		return eventSummary;
	}

	public Object getObjectData() {
		return objectData;
	}

	// ------------------------------------------------------------------------
	
	/*pp*/ void setEventSummary(RecordEventSummary p) {
		this.eventSummary = p;
	}
	
}
