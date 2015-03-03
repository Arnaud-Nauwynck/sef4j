package com.google.code.joto.eventrecorder.spy.calls;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import org.apache.commons.collections.Transformer;

import com.google.code.joto.eventrecorder.writer.RecordEventWriter;

/**
 * adapter for commons-collection Transformer, to wrap object with java.lang.Proxy
 * for writing event request/response to a RecordEventWriter
 * using MethodEventWriterInvocationHandler
 */
public class MethodEventWriterProxyTransformer implements Transformer {

	protected RecordEventWriter eventWriter;

	private String eventType;
	private String requestEventSubType;
	private String responseEventSubType;
	
	private ObjectReplacementMap objectReplacementMap;
	
	// ------------------------------------------------------------------------
	

	public MethodEventWriterProxyTransformer(
			RecordEventWriter eventWriter,
			ObjectReplacementMap objectReplacementMap
			) {
		this(eventWriter, MethodCallEventUtils.METHODCALL_EVENT_TYPE, 
				MethodCallEventUtils.REQUEST_EVENT_SUBTYPE, MethodCallEventUtils.RESPONSE_EVENT_SUBTYPE,
				objectReplacementMap);
	}

	public MethodEventWriterProxyTransformer(
			RecordEventWriter eventWriter,
			String eventType,
			String requestEventSubType,
			String responseEventSubType,
			ObjectReplacementMap objectReplacementMap
			) {
		this.eventWriter = eventWriter;
		this.eventType = eventType;
		this.requestEventSubType = requestEventSubType;
		this.responseEventSubType = responseEventSubType;
		this.objectReplacementMap = objectReplacementMap;
	}


	// ------------------------------------------------------------------------
	
	@Override
	public Object transform(Object obj) {
		Class<?> objClass = obj.getClass();
		Class<?>[] objInterfaces = objClass.getInterfaces();
		Object resProxy = createProxy(objInterfaces, obj);
		return resProxy;
	}
	

	public <T> T createProxy(Class<?>[] proxyInterfaces, T targetObjCallToRecord) {
		ClassLoader classLoader = targetObjCallToRecord.getClass().getClassLoader();
		InvocationHandler h = 
			new MethodEventWriterInvocationHandler(targetObjCallToRecord, eventWriter, 
					eventType, requestEventSubType, responseEventSubType,
					objectReplacementMap);
		@SuppressWarnings("unchecked")
		T resProxy = (T) Proxy.newProxyInstance(classLoader, proxyInterfaces, h);
		return resProxy;
	}
	

}
