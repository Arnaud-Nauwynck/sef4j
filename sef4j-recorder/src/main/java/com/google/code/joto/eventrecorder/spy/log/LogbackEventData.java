package com.google.code.joto.eventrecorder.spy.log;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Marker;

import com.google.code.joto.eventrecorder.RecordEventSummary;

import ch.qos.logback.classic.spi.StackTraceElementProxy;

/**
 *
 */
public class LogbackEventData implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	public static final String EVENT_TYPE = "slf4j";

	// private String message; .. cf EventSummary
	
	private String formattedMessage; 
	private Object[] argumentArray;

	private StackTraceElementProxy[] stackTraceElements;
	
	private Map<String,String> mdcPropertyMap;  
	
	public Marker marker;

	//-------------------------------------------------------------------------

	public LogbackEventData() {
	}

	//-------------------------------------------------------------------------

	/** helper to extract level from EventSummary/EventData */
	public String getLevel(RecordEventSummary event) {
		return event.getEventSubType();		
	}

	/** helper to set level into EventSummary/EventData */
	public void setLevel(RecordEventSummary event, String p) {
		event.setEventSubType(p);		
	}

//	public String getMessage() {
//		return message;
//	}
//
//	public void setMessage(String message) {
//		this.message = message;
//	}

	public String getFormattedMessage() {
		return formattedMessage;
	}

	public void setFormattedMessage(String p) {
		this.formattedMessage = p;
	}

	public Object[] getArgumentArray() {
		return argumentArray;
	}

	public void setArgumentArray(Object[] argumentArray) {
		this.argumentArray = argumentArray;
	}
	
	public StackTraceElementProxy[] getStackTraceElements() {
		return stackTraceElements;
	}

	public void setStackTraceElements(StackTraceElementProxy[] p) {
		this.stackTraceElements = p;
	}

	public Map<String,String> getMDCPropertyMap() {
		return mdcPropertyMap;
	}

	public void setMDCPropertyMap(Map<String,String> p) {
		this.mdcPropertyMap = p;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}
	
}
