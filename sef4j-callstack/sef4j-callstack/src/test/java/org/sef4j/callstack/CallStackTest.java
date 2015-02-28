package org.sef4j.callstack;

import org.junit.Assert;
import org.junit.Test;


public class CallStackTest {

	@Test
	public void testCurr() {
		CallStack sut = new CallStack();
		Assert.assertNotNull(sut.curr());
	}
}
