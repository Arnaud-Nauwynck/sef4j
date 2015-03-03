package com.google.code.joto.eventrecorder.spy.log;

import java.util.Date;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.AppenderBase;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;

/**
 *
 */
public class EventStoreWriterLogbackAppender extends AppenderBase<ILoggingEvent> {
	
	private RecordEventWriter eventWriter;
	
	private String eventType = LogbackEventData.EVENT_TYPE;
	
	protected boolean enable = true;

	protected int minimumLevel = Level.ALL_INT;
	
	// -------------------------------------------------------------------------
	
	public EventStoreWriterLogbackAppender(RecordEventWriter eventWriter, String eventType) {
		super();
		this.eventWriter = eventWriter;
		this.eventType = eventType;
	}

	// -------------------------------------------------------------------------

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean p) {
		this.enable = p;
	}
	
	public int getMinimumLevel() {
		return minimumLevel;
	}

	public void setMinimumLevel(int p) {
		this.minimumLevel = p;
	}

	public void setMinimumLevel(Level p) {
		this.minimumLevel = (p != null)? p.toInt() : Level.ALL_INT;
	}

	// implements AppenderBase
	// ------------------------------------------------------------------------
	

	@Override
	public void start() {
		// cf logback 0.9.17 ?
//		if (this.layout == null) {
//			addError("No layout set for the appender named [" + name + "].");
//			return;
//		}

		super.start();
	}


	@Override
	protected void append(ILoggingEvent p) {
		if (!enable || eventWriter == null || !eventWriter.isEnable()) {
			return;
		}
		
		Level logLevel = p.getLevel();
		if (logLevel.toInt() < minimumLevel) {
			return;
		}
		
		RecordEventSummary eventInfo = new RecordEventSummary(-1);

		eventInfo.setEventType(eventType);
		eventInfo.setThreadName(p.getThreadName());
		eventInfo.setEventDate(new Date(p.getTimeStamp()));
		eventInfo.setEventClassName(p.getLoggerName());
		eventInfo.setEventMethodName(logLevel.toString().toLowerCase());

		// use message instead of formattedMessage for header!!
		// => use "... {0} .. {1} .." to compress event summary encoding
		eventInfo.setEventMethodDetail(p.getMessage()); 
		
		if (!eventWriter.isEnable(eventInfo)) {
			return;
		}

		LogbackEventData eventData = new LogbackEventData();
		eventData.setLevel(eventInfo, logLevel.toString());
		eventData.setFormattedMessage(p.getFormattedMessage());
		eventData.setArgumentArray(p.getArgumentArray());

		if (p.getThrowableProxy() != null) {
			IThrowableProxy throwableProxy = p.getThrowableProxy();
			// throwableProxy.getClassName()
			// throwableProxy.getMessage()
			StackTraceElementProxy[] traceElts = throwableProxy.getStackTraceElementProxyArray();
			eventData.setStackTraceElements(traceElts);
		}
		
		Map<String,String> mdcPropertyMap = p.getMDCPropertyMap();
		if (mdcPropertyMap != null && !mdcPropertyMap.isEmpty()) {
			eventData.setMDCPropertyMap(mdcPropertyMap);
		}
		
		
		eventWriter.addEvent(eventInfo , eventData, null);
	}

}

