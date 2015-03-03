package com.google.code.joto.eventrecorder;

import com.google.code.joto.eventrecorder.RecordEventStoreChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.StartRecordingEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.StopRecordingEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.TruncateRecordEventStoreEvent;

/**
 * Visitor design-pattern for RecordEventStoreChange sub-class hierarchy
 * 
 * Note that you can also use the generic listener RecordEventListener (design-pattern Observer) 
 */
public interface RecordEventChangeVisitor {
	
	public void caseAddEvent(AddRecordEventStoreEvent p);
	
	public void caseTruncateEvent(TruncateRecordEventStoreEvent p);
	public void caseStartRecording(StartRecordingEvent p);
	public void caseStopRecording(StopRecordingEvent p);
	
}