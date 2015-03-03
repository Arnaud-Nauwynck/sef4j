package com.google.code.joto.eventrecorder;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 */
public class RecordEventSummary implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	private final int eventId;
	private Date eventDate;
	private String threadName;
	private String eventType;
	private String eventSubType;
	private String eventClassName;
	private String eventMethodName;
	private String eventMethodDetail;

	private int correlatedEventId; // request eventId from response object 

	private long internalEventStoreDataAddress;
	
	// ------------------------------------------------------------------------

	public RecordEventSummary() {
		this(-1);
	}

	public RecordEventSummary(int eventId) {
		this.eventId = eventId;
	}

	public RecordEventSummary(int eventId, Date eventDate, String threadName, 
			String eventType, String eventSubType,
			String eventClassName,
			String eventMethodName,
			String eventMethodDetail, 
			int internalEventStoreDataAddress) {
		super();
		this.eventId = eventId;
		this.eventDate = eventDate;
		this.threadName = threadName;
		this.eventType = eventType;
		this.eventSubType = eventSubType;
		this.eventClassName = eventClassName;
		this.eventMethodName = eventMethodName;
		this.eventMethodDetail = eventMethodDetail;
		this.internalEventStoreDataAddress = internalEventStoreDataAddress;
	}

	public RecordEventSummary(int eventId, RecordEventSummary src) {
		this.eventId = eventId;
		this.eventDate = src.eventDate;
		this.threadName = src.threadName;
		this.eventType = src.eventType;
		this.eventSubType = src.eventSubType;
		this.eventClassName = src.eventClassName;
		this.eventMethodName = src.eventMethodName;
		this.eventMethodDetail = src.eventMethodDetail;
		this.correlatedEventId = src.correlatedEventId;
		this.internalEventStoreDataAddress = src.internalEventStoreDataAddress;
	}
	
	public static RecordEventSummary snewDefault(
			String eventType, String eventSubType, 
			String eventClassName,
			String eventMethodName) {
		RecordEventSummary e = new RecordEventSummary(-1);
		e.setEventDate(new Date());
		e.setThreadName(Thread.currentThread().getName());
		e.setEventType(eventType);
		e.setEventSubType(eventSubType);
		e.setEventClassName(eventClassName);
		e.setEventMethodName(eventMethodName);
		return e;
	}
	
	// ------------------------------------------------------------------------

	public int getEventId() {
		return eventId;
	}
	
	public Date getEventDate() {
		return eventDate;
	}
	
	public String getThreadName() {
		return threadName;
	}

	public void setThreadName(String p) {
		this.threadName = p;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String p) {
		this.eventType = p;
	}

	public String getEventSubType() {
		return eventSubType;
	}

	public void setEventSubType(String p) {
		this.eventSubType = p;
	}
	
	public String getEventClassName() {
		return eventClassName;
	}

	public void setEventClassName(String p) {
		this.eventClassName = p;
	}

	public String getEventMethodName() {
		return eventMethodName;
	}

	public void setEventMethodName(String p) {
		this.eventMethodName = p;
	}

	public String getEventMethodDetail() {
		return eventMethodDetail;
	}

	public void setEventMethodDetail(String p) {
		this.eventMethodDetail = p;
	}
	
	public int getCorrelatedEventId() {
		return correlatedEventId;
	}

	public void setCorrelatedEventId(int p) {
		this.correlatedEventId = p;
	}

	public long getInternalEventStoreDataAddress() {
		return internalEventStoreDataAddress;
	}

	public void setInternalEventStoreDataAddress(long p) {
		this.internalEventStoreDataAddress = p;
	}

	
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "RecordEventHandle[" 
			+ eventId 
			+ " " + eventType 
			+ ((eventSubType != null)? " " + eventSubType : "") 
			+ " " + eventDate
			+ " " + threadName
			+ " " + eventClassName 
			+ " " + eventMethodName 
			+ ((eventMethodDetail != null)? " " + eventMethodDetail : "")
			+ " @" + internalEventStoreDataAddress
			+ "]";
	}
	
}
