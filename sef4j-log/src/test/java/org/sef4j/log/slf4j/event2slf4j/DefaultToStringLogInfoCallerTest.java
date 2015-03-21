package org.sef4j.log.slf4j.event2slf4j;

import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;


public class DefaultToStringLogInfoCallerTest {

	private static class E {
		protected String msg;
		public E(String msg) {
			this.msg = msg;
		}
		public String toString() {
			return "E[" + msg + "]";
		}
	}
	
	protected Logger slf4jLogger = Mockito.mock(Logger.class);

	protected DefaultToStringLogInfoCaller<E> sut = new DefaultToStringLogInfoCaller<E>("msg-prefix- ", " -msg-suffix");
	
	@Test
	public void testLogTo() {
		// Prepare
		E e1 = new E("e1");
		Mockito.doNothing().when(slf4jLogger).info(Mockito.anyString());
		// Perform
		sut.logTo(e1, slf4jLogger);
		// Post-check
		Mockito.verify(slf4jLogger).info("msg-prefix- E[e1] -msg-suffix");
	}
}
