package com.google.code.joto.eventrecorder.spy.calls;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;
import com.google.code.joto.eventrecorder.writer.RecordEventWriterCallback.CorrelatedEventSetterCallback;

/**
 * default implementation of java Proxy reflection, 
 * for generating record events as request/response
 */
public class MethodEventWriterInvocationHandler implements InvocationHandler {

	private Object target;
	
	private RecordEventWriter eventWriter;
	
	private String eventType;
	private String requestEventSubType;
	private String responseEventSubType;
	
	private ObjectReplacementMap objectReplacementMap;
	
	//-------------------------------------------------------------------------

	public MethodEventWriterInvocationHandler(Object target,
			RecordEventWriter eventWriter,
			String eventType,
			String requestEventSubType,
			String responseEventSubType,
			ObjectReplacementMap objectReplacementMap
			) {
		this.target = target;
		this.eventWriter = eventWriter;
		this.eventType = eventType;
		this.requestEventSubType = requestEventSubType;
		this.responseEventSubType = responseEventSubType;
		this.objectReplacementMap = objectReplacementMap;
	}

	
	//-------------------------------------------------------------------------

	
	public ObjectReplacementMap getObjectReplacementMap() {
		return objectReplacementMap;
	}

	public void setObjectReplacementMap(ObjectReplacementMap p) {
		this.objectReplacementMap = p;
	}


	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Class<?> clss = method.getDeclaringClass();
		String className = MethodCallEventUtils.extractEventClassName(clss);
		final String methodName = method.getName();

		// Handle the methods from java.lang.Object
		if (method.getDeclaringClass() == Object.class) {
			if (args == null && methodName.equals("toString")) {
				return "EventDataSourceProxy[" + target + "]";
			} else if (args == null && methodName.equals("hashCode")) {
				return target.hashCode() + 123;
			} else if (args.length == 1
					&& method.getParameterTypes()[0] == Object.class
					&& methodName.equals("equals")) {
				return proxy ==  args[0];
			} 
		} 

		// generate event for method request
		boolean enable = eventWriter.isEnable();
		if (!enable) {
			// *** do call (case 1/3, no event) ***
			Object res = method.invoke(target, args);
			return res;
		} else {
			RecordEventSummary evt = MethodCallEventUtils.createEvent(eventType, requestEventSubType, 
					className, methodName);
			if (!eventWriter.isEnable(evt)) {
				// *** do call (case 2/3, no event) ***
				Object res = method.invoke(target, args);
				return res;
			}

			Object replTarget = target;
			Object[] replArgs = args; // TODO not required to replace arg sin current version?
			if (objectReplacementMap != null) {
				replTarget = objectReplacementMap.checkReplace(replTarget);
				replArgs = objectReplacementMap.checkReplaceArray(replArgs);
			}
			EventMethodRequestData reqObjData = new EventMethodRequestData(replTarget, method, replArgs);
			
			RecordEventSummary respEvt = MethodCallEventUtils.createEvent(eventType, responseEventSubType, 
					className, methodName);
			CorrelatedEventSetterCallback callbackForEventId =
				new CorrelatedEventSetterCallback(respEvt);
			
			eventWriter.addEvent(evt, reqObjData, callbackForEventId);


			try {
				// *** do call (case 3/3, with events) ***
				Object res = method.invoke(target, args);
				
				Object replRes = res;
				if (objectReplacementMap != null) {
					replRes = objectReplacementMap.checkReplace(res);
				}
				EventMethodResponseData respObjData = new EventMethodResponseData(replRes, null);
				eventWriter.addEvent(respEvt, respObjData, null);

				return res;

			} catch(Exception ex) {
				EventMethodResponseData respObjData = new EventMethodResponseData(null, ex);
				eventWriter.addEvent(respEvt, respObjData, null);
	
				throw ex; // rethow!
			}
		}
	}

	
}
