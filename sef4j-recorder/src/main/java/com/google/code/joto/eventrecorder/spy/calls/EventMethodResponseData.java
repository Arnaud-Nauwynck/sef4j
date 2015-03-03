package com.google.code.joto.eventrecorder.spy.calls;

import java.io.Serializable;

/**
 * Serializable object representation of the ending of a method call. 
 * <p/>
 * Contains the object result of the method, or the exception being thrown, 
 * but not objects and arguments of the corresponding methods being called! ...
 * <p/>
 * cf corresponding class EventMethodRequestData for the object and arguments.
 *
 * when storing request-response data into record events store, they are wrapped into RecordEventSummary,  
 * the response event as an id to its corresponding request event.
 *  
 * 
 * Pseudo code for request-response events
 * <pre>
 * {@code
 * // record beginning of method:
 * RecordEventSummary requestEvent = new RecordEventSummary(...);
 * EventMethodRequestData requestData = new EventMethodRequestData(methodObject, methodArguments); 
 * eventWriter.addEvent(evt, reqObjData, ...);  
 * int requestEventId = ...  // pseudo-code: retreived eventId (with async callbacks)
 * 
 * // the method..
 * 
 * // record end of method
 * RecordEventSummary responseEvent = new RecordEventSummary(...);
 * responseEvent.setCorrelatedEventId(requestEventId); // pseudo-code ... (with in async callback)
 * EventMethodResponseData responseData = new EventMethodResponseData(methodResult, methodException) 
 * eventWriter.addEvent(responseEvent, responseData, ...);
 * }</pre> 
 */
public class EventMethodResponseData implements Serializable {

	/** intenral for javA.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	private Object result;
	private Throwable exception;
	
	// -------------------------------------------------------------------------
	
	public EventMethodResponseData() {
	}
	
	public EventMethodResponseData(Object result, Throwable exception) {
		this.result = result;
		this.exception = exception;
	}

	// -------------------------------------------------------------------------

	public Object getResult() {
		return result;
	}

	public void setResult(Object p) {
		this.result = p;
	}

	public Throwable getException() {
		return exception;
	}

	public void setException(Throwable p) {
		this.exception = p;
	}
	
}
