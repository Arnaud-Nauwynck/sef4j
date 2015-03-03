package com.google.code.joto.eventrecorder;

import com.google.code.joto.eventrecorder.RecordEventStoreChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.StartRecordingEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.StopRecordingEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.TruncateRecordEventStoreEvent;

/**
 * default empty implementation for RecordEventChangeVisitor
 */
public class DefaultRecordEventChangeVisitor implements RecordEventChangeVisitor {

	@Override
	public void caseAddEvent(AddRecordEventStoreEvent p) {
		// do nothing
	}

	@Override
	public void caseTruncateEvent(TruncateRecordEventStoreEvent p) {
		// do nothing
	}

	@Override
	public void caseStartRecording(StartRecordingEvent p) {
		// do nothing
	}

	@Override
	public void caseStopRecording(StopRecordingEvent p) {
		// do nothing
	}
	
}
