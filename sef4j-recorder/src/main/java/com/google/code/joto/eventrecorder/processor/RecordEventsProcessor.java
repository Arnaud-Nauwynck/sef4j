package com.google.code.joto.eventrecorder.processor;

import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 * a simple interface for defining a treatment on multiple events
 */
public interface RecordEventsProcessor {

	public boolean needEventObjectData();
	
	public void processEvent(RecordEventSummary event, Object eventObjectData);
	
}
