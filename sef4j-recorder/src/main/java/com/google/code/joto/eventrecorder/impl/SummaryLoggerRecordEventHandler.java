package com.google.code.joto.eventrecorder.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;

import com.google.code.joto.eventrecorder.RecordEventChangeVisitor;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.StartRecordingEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.StopRecordingEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.TruncateRecordEventStoreEvent;

/**
 * a simple RecordEventChangeVisitor for logging events to slf4j
 */
public class SummaryLoggerRecordEventHandler implements RecordEventChangeVisitor {

	private Logger logger;

	private final DateFormat displayDateFormat = new SimpleDateFormat("HH:mm:ss");

	// -------------------------------------------------------------------------
	
	public SummaryLoggerRecordEventHandler(Logger logger) {
		super();
		this.logger = logger;
	}

	// -------------------------------------------------------------------------

	@Override
	public void caseAddEvent(AddRecordEventStoreEvent p) {
		if (logger.isInfoEnabled()) {
			RecordEventSummary e = p.getEventSummary();
			String msg = "RecordEvent#" + e.getEventId()
				+ " " + displayDateFormat.format(e.getEventDate())
				+ " " + e.getEventType() + " " + e.getEventSubType() 
				+ " " + e.getEventMethodName() 
				+ ((e.getEventMethodDetail() != null)? e.getEventMethodDetail() : "")
				;
			logger.info(msg);
		}
	}

	@Override
	public void caseStartRecording(StartRecordingEvent p) {
		logger.info("start recording");
	}

	@Override
	public void caseStopRecording(StopRecordingEvent p) {
		logger.info("stop recording");
	}

	@Override
	public void caseTruncateEvent(TruncateRecordEventStoreEvent p) {
		logger.info("truncate recorded events " + p.getFromEventId() + "<= ...< " + p.getToEventId());
	}

	// override java.lang.Object
	// -------------------------------------------------------------------------

	@Override
	public String toString() {
		return "SummaryLoggerRecordEventHandler[]";
	}
	
}
