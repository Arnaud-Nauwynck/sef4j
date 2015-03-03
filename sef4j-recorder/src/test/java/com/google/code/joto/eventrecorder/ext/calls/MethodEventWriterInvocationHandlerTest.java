package com.google.code.joto.eventrecorder.ext.calls;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore;
import com.google.code.joto.eventrecorder.spy.calls.EventMethodRequestData;
import com.google.code.joto.eventrecorder.spy.calls.EventMethodResponseData;
import com.google.code.joto.eventrecorder.spy.calls.MethodEventWriterProxyTransformer;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;
import com.google.code.joto.testobj.SerializableObj;

/**
 * JUnit test for MethodEventWriterInvocationHandler
 *
 */
public class MethodEventWriterInvocationHandlerTest extends TestCase {
	
	public static IFoo createFooProxyRecorder(IFoo targetObjCallToRecord, RecordEventWriter eventWriter) {
		MethodEventWriterProxyTransformer proxyTransformer = 
				new MethodEventWriterProxyTransformer(eventWriter, null);
		IFoo fooProxy = (IFoo) proxyTransformer.createProxy(new Class[] { IFoo.class }, targetObjCallToRecord);
		return fooProxy;
	}

	private static class TestData {
		IFoo impl;
		DefaultMemoryRecordEventStore eventStore;
		RecordEventWriter eventWriter;
		IFoo fooProxy;
		
		public TestData() {
			impl = new DefaultSerializableFoo();
			eventStore = new DefaultMemoryRecordEventStore();
			eventWriter = eventStore.getEventWriter(); 
			fooProxy = createFooProxyRecorder(impl, eventWriter);
		}
	}
	
	public MethodEventWriterInvocationHandlerTest(String name) {
		super(name);
	}

	public void testFooSimple() {
		TestData d = new TestData();
		IFoo fooProxy = d.fooProxy;
		
		String res = fooProxy.methSimple("test", 1);
		assertEquals("test1", res);
		
		List<RecordEventSummary> events = d.eventStore.getEvents();
		assertEquals(2, events.size());
		
		RecordEventSummary eventRequest = events.get(0);
		RecordEventData eventRequestData = d.eventStore.getEventData(eventRequest);
		assertNotNull(eventRequestData);
		EventMethodRequestData requestData = (EventMethodRequestData) 
			eventRequestData.getObjectData();
		assertNotNull(requestData);
		assertTrue(requestData.getExpr().getClass() == d.impl.getClass());
		Method methodSimple;
		try {
			methodSimple = IFoo.class.getMethod("methSimple", new Class[] { String.class, int.class} );
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
		// assertSame(methodSimple, requestData.getMethod()); //wrong!
		assertEquals(methodSimple, requestData.getMethod());
		assertEquals(2, requestData.getArguments().length);
		assertEquals("test", requestData.getArguments()[0]);
		assertEquals(Integer.valueOf(1), requestData.getArguments()[1]);
		
		RecordEventSummary eventResponse = events.get(1);
		RecordEventData eventResponseData = d.eventStore.getEventData(eventResponse);
		assertNotNull(eventResponseData);
		EventMethodResponseData responseData = (EventMethodResponseData) 
			eventResponseData.getObjectData();
		assertNotNull(responseData);
		assertEquals(eventRequest.getEventId(), eventResponse.getCorrelatedEventId());
		assertEquals("test1", responseData.getResult());
		assertNull(responseData.getException());
		
		// call more..
		fooProxy.methSimple("test", 1);
		assertEquals(4, d.eventStore.getEvents().size());
	}
	
	public void testAllFooMethods() {
		TestData d = new TestData();
		IFoo fooProxy = d.fooProxy;
		
		doCallFooMethods(fooProxy);
		
	}

	public static void doCallFooMethods(IFoo p) {
		p.methVoid(1);
		
		p.methVoidFromPrimitives(0, 0.0f, 0.0, '\0', false, (byte)0);
		p.methVoidFromPrimitives(1, 1.0f, 1.0, 'a', true, (byte)1);
		
		p.methVoidFromPrimitivesArrays(
				new int[] { 0, 1 }, 
				new float[] { 0.0f, 1.0f }, 
				new double[] { 0.0, 1.0 }, 
				new char[] { '\0', 'a', '0', '\\', '\'' }, 
				new boolean[] { false, true }, 
				new byte[] { (byte)0, (byte)1 });
		
		p.methVoidFromObj(new SerializableObj(), "a", 
				new Date(), Integer.valueOf(1), new Float(1.0f), 
				new Double(1.0));

		p.methVoidFromObjArrays(
				new Object[] { null, new SerializableObj() }, 
				new String[] { null, "", "a", "\"", "\\" }, 
				new Date[] { null, new Date() } , 
				new Integer[] { null, new Integer(0), new Integer(1) }, 
				new Float[] { null, new Float(0.0f), new Float(1.0f) }, 
				new Double[] { null, new Double(0.0), new Double(1.0) });
		
		p.methInt();
		p.methFloat();
		p.methDouble();

		p.methObj();
		p.methString();
		
		p.methObjArray();
		p.methIntArray();
		p.methFloatArray();
		p.methStringArray();
	}
}
