package com.google.code.joto.eventrecorder.spy.log;

import java.util.Date;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;
import com.google.code.joto.util.io.SerializableUtil;

/**
 * Spi Extension of EventRecorderStore, for storing log4j events as events 
 *
 */
public class EventStoreWriterLog4jAppender extends AppenderSkeleton {

	private RecordEventWriter eventWriter;
	
	private String eventType = Log4jEventData.EVENT_TYPE;
	// private String eventSubType => used for logEvent message severity 

	
	// -------------------------------------------------------------------------
	
	public EventStoreWriterLog4jAppender(RecordEventWriter eventWriter, String eventType) {
		super();
		this.eventWriter = eventWriter;
		this.eventType = eventType;
	}

	// -------------------------------------------------------------------------

	@Override
	public void close() {
		eventWriter = null;
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

	@Override
	protected void append(LoggingEvent p) {
		if (eventWriter == null || !eventWriter.isEnable()) {
			return;
		}
		RecordEventSummary eventInfo = new RecordEventSummary(-1);

		eventInfo.setEventType(eventType);
		eventInfo.setEventSubType(p.getLevel().toString());

		eventInfo.setThreadName(p.getThreadName());
		eventInfo.setEventDate(new Date(p.getTimeStamp()));
		eventInfo.setEventMethodName(p.getLoggerName());
		
		eventInfo.setEventMethodDetail(p.getRenderedMessage());
		
		if (!eventWriter.isEnable(eventInfo)) {
			return;
		}

		// filla additionnal LoggingEvent values in eventData
		Log4jEventData eventData = new Log4jEventData();

		if (p.getThrowableInformation() != null) {
			Throwable throwable = p.getThrowableInformation().getThrowable();
			if (SerializableUtil.checkSerializable(throwable)) {
				eventData.setThrowable(throwable);
			} else {
				// set only when throwable is not serializable? ... otherwise redundant with Throwable...
				String[] throwableStrRep = p.getThrowableStrRep();
				eventData.setThrowableStrRep(throwableStrRep);
			}
		}
		
		@SuppressWarnings("unchecked")
		Map<String,String> properties = p.getProperties();
		if (properties != null && !properties.isEmpty()) {
			eventData.setProperties(properties);
		}
		
		String ndc = p.getNDC();
		if (ndc != null) {
			// TODO?? NDC. should be / be-converted to String[] ??
			eventData.setNdcStrRep(ndc);
		}
		
		eventWriter.addEvent(eventInfo , eventData, null);
		
	}
	
}
