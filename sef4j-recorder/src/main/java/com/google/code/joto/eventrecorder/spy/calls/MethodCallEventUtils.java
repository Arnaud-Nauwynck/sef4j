package com.google.code.joto.eventrecorder.spy.calls;

import java.util.Date;

import com.google.code.joto.eventrecorder.RecordEventSummary;

public class MethodCallEventUtils {

	public static final String METHODCALL_EVENT_TYPE = "methodCall";
	public static final String REQUEST_EVENT_SUBTYPE = "request";
	public static final String RESPONSE_EVENT_SUBTYPE = "response";


	
	public static RecordEventSummary createEvent(String eventType, String eventSubType, String className, String methodName) {
		RecordEventSummary evt = new RecordEventSummary(-1);
		evt.setEventDate(new Date());
		evt.setEventType(eventType);
		evt.setEventSubType(eventSubType);
		evt.setEventClassName(className);
		evt.setEventMethodName(methodName);
		return evt;
	}



	public static String extractEventClassName(Class<?> clss) {
		String className = clss.getName();
		if (className.startsWith("Proxy$")) {
			Class<?>[] interfaces = clss.getInterfaces();
			if (interfaces.length > 0) {
				Class<?> firstInterface = interfaces[0];
				className = firstInterface.getName();
			}
		}
		return className;
	}

}
