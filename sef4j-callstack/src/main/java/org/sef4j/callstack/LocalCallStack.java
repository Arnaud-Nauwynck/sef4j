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

	/**
	 * method for adding a CallStackElt corresponding to a return result value of a parent CallStackElt method
	 * this is ~equivalent to <code>push().withParam() + immediate pop()</code>
	 * but the pushed elt "start time" is taken from the parent call elt, to count statistics of correct method return
	 * ... cf also <code>pushPopParentResVoid()</code> and <code>pushPopParentResEx()</code> 
	 * @param resEltName
	 * @param resParamName
	 * @param resValue
	 * 
	 * typical code usage:
	 * <code>
	 * public Xyz foo() throws SQLException {
	 *  StackPopper toPop = LocalCallStack.meth("foo").push();
	 *  try {
	 *      // ... do foo
	 *      Xyz res = ... 
	 *      
	 *      return LocalCallStack.pushPopParentReturn(res);
	 *  } catch(RuntimeException ex) {
     *      throw LocalCallStack.pushPopParentException(ex);
	 *  } finally {
	 *      toPop.close();
	 *  }
	 * </code>
	 */
    public static <T> T pushPopParentReturn(T resValue) {
        StackPopper toPop = meth("return").withParam("res", resValue).pushWithParentStartTime();
        toPop.close();
        return resValue;
    }

    
    /** 
     * method for adding a CallStackElt corresponding to a void return of a parent CallStackElt method
     * this is ~equivalent to <code>push() + immediate pop()</code>
     * but the pushed elt "start time" is taken from the parent call elt, to count statistics of correct method return
     * 
     * see also pushPopParentResValue()
     * typical code usage:
     * <code>
     * public void foo() throws SQLException {
     *  StackPopper toPop = LocalCallStack.meth("foo").push();
     *  try {
     *      // ... do foo
     *      
     *      LocalCallStack.pushPopParentReturn();
     *  } catch(RuntimeException ex) {
     *      throw LocalCallStack.pushPopParentException(ex);
     *  } finally {
     *      toPop.close();
     *  }
     * </code>
     * 
     */
    public static void pushPopParentReturn() {
        StackPopper toPop = meth("return").pushWithParentStartTime();
        toPop.close();
    }

    /** 
     * method for adding a CallStackElt corresponding to a exception exit of a parent CallStackElt method
     * this is ~equivalent to <code>push() + immediate pop()</code>
     * but the pushed elt "start time" is taken from the parent call elt, to count statistics of correct method return
     * 
     * see also pushPopParentResValue(), pushPopParentReturnVoid()
     * typical code usage:
     * <code>
     * public void foo() throws SQLException {
     *  StackPopper toPop = LocalCallStack.meth("foo").push();
     *  try {
     *      // ... do foo
     *      
     *      LocalCallStack.pushPopParentReturn();
     *  } catch(RuntimeException ex) {
     *      throw LocalCallStack.pushPopParentException(ex);
     *  } finally {
     *      toPop.close();
     *  }
     * </code>
     */
    public static <T extends Throwable> T pushPopParentException(T ex) {
        String eltName = "exception-" + ex.getClass().getSimpleName();
        pushPopParentException(eltName, ex);
        return ex;
    }

    /** idem pushPopParentException() but using custom exception eltName instead of default (<code>eltName= "exception-" + ex.getClass().getSimpleName()</code>) */
    public static void pushPopParentException(String eltName, Throwable ex) {
        StackPopper toPop = meth(eltName).withParam("ex", ex).pushWithParentStartTime();
        toPop.close();
    }
    

    /** type-safe overload of pushPopParentReturn(Object), for avoiding boxing/unboxing */
    public static int pushPopParentReturn(int resValue) {
        StackPopper toPop = meth("return").withParam("res", resValue).pushWithParentStartTime();
        toPop.close();
        return resValue;
    }
    /** type-safe overload of pushPopParentReturn(Object), for avoiding boxing/unboxing */
    public static long pushPopParentReturn(long resValue) {
        StackPopper toPop = meth("return").withParam("res", resValue).pushWithParentStartTime();
        toPop.close();
        return resValue;
    }
    /** type-safe overload of pushPopParentReturn(Object), for avoiding boxing/unboxing */
    public static double pushPopParentReturn(double resValue) {
        StackPopper toPop = meth("return").withParam("res", resValue).pushWithParentStartTime();
        toPop.close();
        return resValue;
    }
    /** type-safe overload of pushPopParentReturn(Object), for avoiding boxing/unboxing */
    public static boolean pushPopParentReturn(boolean resValue) {
        StackPopper toPop = meth("return").withParam("res", resValue).pushWithParentStartTime();
        toPop.close();
        return resValue;
    }

}
