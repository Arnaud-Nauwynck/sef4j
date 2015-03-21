package org.sef4j.log.slf4j.event2slf4j;

import java.util.Arrays;

import org.junit.Test;
import org.mockito.Mockito;
import org.sef4j.core.helpers.adapters.TypeHierarchyToObjectMap;
import org.slf4j.Logger;


public class Slf4jLogCallerEventSenderTest {

	private static class E {
		protected String msg;
		public E(String msg) {
			this.msg = msg;
		}
		public String toString() {
			return "E[" + msg + "]";
		}
	}
	
	private static class F extends E {
		public F(String msg) {
			super(msg);
		}
		public String toString() {
			return "F[" + msg + "]";
		}
	}
	
	protected Logger slf4jLogger = Mockito.mock(Logger.class);

	protected EventToSlf4jLogCaller<E> eventToSlf4jLogCaller1 = 
			new DefaultToStringLogInfoCaller<E>("msg-prefix1- ", " -msg-suffix1");

	protected EventToSlf4jLogCaller<E> eventToSlf4jLogCaller2 = 
			new DefaultToStringLogInfoCaller<E>("msg-prefix2- ", " -msg-suffix2");

	protected EventToSlf4jLogCaller<E> defaultEventToSlf4jLogCaller = 
			new DefaultToStringLogInfoCaller<E>("default-msg-prefix- ", " -default-msg-suffix");

	protected TypeHierarchyToObjectMap<EventToSlf4jLogCaller<E>> eventToSlf4jLogCallerPerClass = 
			new TypeHierarchyToObjectMap<EventToSlf4jLogCaller<E>>();

	private Slf4jLogCallerEventSender<E> sut = new Slf4jLogCallerEventSender<E>(
			slf4jLogger, null, null);

	
	@Test
	public void testSendEvent() {
		// Prepare
		Mockito.doNothing().when(slf4jLogger).info(Mockito.anyString());
		E e1 = new E("e1");
		// Perform
		sut.sendEvent(e1);
		// Post-check
		Mockito.verify(slf4jLogger, Mockito.times(0)).info(Mockito.anyString());

		// Prepare
		sut.setDefautEventToSlf4jLogCaller(defaultEventToSlf4jLogCaller);
		// Perform
		sut.sendEvent(e1);
		// Post-check
		Mockito.verify(slf4jLogger).info("default-msg-prefix- E[e1] -default-msg-suffix");

		
		// Prepare
		eventToSlf4jLogCallerPerClass.putOverride(E.class, eventToSlf4jLogCaller1);
		sut.setEventToSlf4jLogCallerPerClass(eventToSlf4jLogCallerPerClass);
		// Perform
		sut.sendEvent(e1);
		// Post-check
		Mockito.verify(slf4jLogger).info("msg-prefix1- E[e1] -msg-suffix1");
	
		// Prepare
		F f2 = new F("f2");
		// Perform
		sut.sendEvent(f2);
		// Post-check
		Mockito.verify(slf4jLogger).info("msg-prefix1- F[f2] -msg-suffix1");

		// Prepare
		eventToSlf4jLogCallerPerClass.putOverride(F.class, eventToSlf4jLogCaller2);
		// Perform
		sut.sendEvent(f2);
		// Post-check
		Mockito.verify(slf4jLogger).info("msg-prefix2- F[f2] -msg-suffix2");
	}

	
	@Test
	public void testSendEvents() {
		// Prepare
		Mockito.doNothing().when(slf4jLogger).info(Mockito.anyString());
		E e1 = new E("e1");
		E e2 = new E("e2");
		sut.setDefautEventToSlf4jLogCaller(defaultEventToSlf4jLogCaller);
		// Perform
		sut.sendEvents(Arrays.asList(e1, e2));
		// Post-check
		Mockito.verify(slf4jLogger).info("default-msg-prefix- E[e1] -default-msg-suffix");
		Mockito.verify(slf4jLogger).info("default-msg-prefix- E[e2] -default-msg-suffix");
	}
	
}
