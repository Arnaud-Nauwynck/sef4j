package org.sef4j.callstack;

import org.sef4j.callstack.CallStackElt.StackPopper;
import org.sef4j.callstack.CallStackElt.StackPusher;

/**
 * 
 */
public class LocalCallStack {

	private static final ThreadLocal<CallStack> threadLocal = new ThreadLocal<CallStack>() {
		@Override
		protected CallStack initialValue() {
			return new CallStack();
		}
	};
	
	public static CallStack get() {
		return threadLocal.get();
	}

	public static CallStackElt currThreadStackElt() {
		return threadLocal.get().curr();
	}


	/**
	 * This is the main entry point for calling push()/pop() on the current thread call stack
	 * 
	 * sample code (with jdk version >= 8):
	 * <code>
	 * try (StackPopper toPop = LocalCallStack.meth("methodName").push()) {
	 * 
	 * }
	 * </code>
	 * 
	 * using plain old java style (jdk version < 8)
	 * <code>
	 * StackPopper toPop = LocalCallStack.meth("methodName").push();
	 * try {
	 * 
	 * } finally {
	 * 	toPop.close();
	 * }
	 * </code>
	 * 
	 * Advanced example using StackPusher (~ Builder design-pattern):
	 * <code>
	 * try (StackPopper toPop = LocalCallStack.meth("methodName")
	 * 		.withParam("param1", value1)
	 * 		.withParam("param2", value2)
	 * 		.withInheritedProp("prop3", value3)
	 * 		.withLogger(LOG, LogLevel.INFO, LogLevel.INFO)  // <= log INFO on push() and INFO on pop()
	 * 		// .withLogger(LOG, LogLevel.INFO, LogLevel.DEBUG, 500)  // <= log INFO on push(), and DEBUG on pop(), but INFO when time exceed 500 ms
	 * 		.push()) {
	 * 
	 * }
	 * </code>
	 * 
	 * @param name
	 * @return
	 */
	public static StackPusher meth(String name) {
		CallStackElt currThreadStackElt = currThreadStackElt();
		return currThreadStackElt.pusher(name);
	}
	
	/** alias for <code>meth(name).push()</code> 
	 * using this shor tsyntax, it is not possible to configure StackElt with parameters,properties,logger...
	 * sample code:
	 * <code>
	 * try (StackPopper toPop = LocalCallStack.push("methodName")) {
	 * 
	 * }
	 * </code>
	 */
	public static StackPopper push(String name) {
		return meth(name).push();
	}

	/** alias for StackPopper.close() */
	public static void pop(StackPopper toPop) {
		toPop.close();
	}

	/** alias for progressStep(+1, null) */
	public static void progressStep() {
		progressStep(+1, null);
	}
	
	public static void progressStep(int incr, String progressMessage) {
		CallStackElt currStackElt = currThreadStackElt();
		currStackElt.onProgressStep(incr, progressMessage);
	}
	
}
