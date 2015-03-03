package org.sef4j.callstack;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.CallStackElt.StackPopper;


public class LocalCallStackTest {

	@Test
	public void testGet() {
		CallStack currCallStack = LocalCallStack.get();
		Assert.assertNotNull(currCallStack);
		CallStack currCallStack2 = LocalCallStack.get();
		Assert.assertSame(currCallStack, currCallStack2);
	}
	
	@Test
	public void testMeth() {
		// cf testPushPop()
	}
	@Test
	public void testPush() {
		// cf testPushPop()
	}
	@Test
	public void testPop() {
		// cf testPushPop()
	}
	
	@Test
	public void testPushPop() {
		// Prepare
		CallStack currCallStack = LocalCallStack.get();
		CallStackElt currCallStackElt = currCallStack.curr();
		
		// Perform
		StackPopper toPop = LocalCallStack.meth("test").push();
		try {
			// do nothing
		} finally {
			toPop.close();
		}
		
		// Post-check
		CallStackElt checkCurrCallStackElt = currCallStack.curr();
		Assert.assertSame(currCallStackElt, checkCurrCallStackElt);
	}
}
