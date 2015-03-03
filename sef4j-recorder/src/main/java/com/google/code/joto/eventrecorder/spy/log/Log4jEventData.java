package com.google.code.joto.eventrecorder.spy.log;

import java.io.Serializable;
import java.util.Map;

import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 *
 */
public class Log4jEventData implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	public static final String EVENT_TYPE = "log4j";
	
//	private String unformattedMessage; 
//	private Object[] argumentArray;

	
	private Throwable throwable;
	// set only when throwable is not serializable? ... otherwise redundant with Throwable...
	private String[] throwableStrRep;
	
	/** cf mdc?? */
	private Map<String,String> properties;  
	
//	private String[] ndcStack;
	/** idem NDC ... to be converted as String[] ??? */ 
	private String ndcStrRep;
	
	
	//-------------------------------------------------------------------------

	public Log4jEventData() {
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

	/** helper to extract level from EventSummary/EventData */
	public String getFormattedMessage(RecordEventSummary event) {
		return event.getEventMethodDetail();		
	}

	/** helper to set level into EventSummary/EventData */
	public void setFormattedMessage(RecordEventSummary event, String p) {
		event.setEventMethodDetail(p);		
	}

	public Throwable getThrowable() {
		return throwable;
	}

//	public String getUnformattedMessage() {
//		return unformattedMessage;
//	}
//
//	public void setUnformattedMessage(String unformattedMessage) {
//		this.unformattedMessage = unformattedMessage;
//	}
//
//	public Object[] getArgumentArray() {
//		return argumentArray;
//	}
//
//	public void setArgumentArray(Object[] argumentArray) {
//		this.argumentArray = argumentArray;
//	}

	public void setThrowable(Throwable p) {
		this.throwable = p;
	}
	
	public String[] getThrowableStrRep() {
		return throwableStrRep;
	}

	public void setThrowableStrRep(String[] p) {
		this.throwableStrRep = p;
	}

	public Map<String,String> getProperties() {
		return properties;
	}
	
	public void setProperties(Map<String,String> p) {
		this.properties = p;
	}

//	public String[] getNdcStack() {
//		return ndcStack;
//	}
//
//	public void setNdcStack(String[] p) {
//		this.ndcStack = p;
//	}

	public String getNdcStrRep() {
		return ndcStrRep;
	}

	public void setNdcStrRep(String p) {
		this.ndcStrRep = p;
	}

}
