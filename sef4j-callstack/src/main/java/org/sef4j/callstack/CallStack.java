package org.sef4j.callstack;

import org.sef4j.callstack.CallStackElt.StackPopper;

/**
 * a CallStack is a sub-list of a java thread Stack, but enriched with applicative info
 * <p/>
 * This class is managed from current thread local facade: LocalCallStack using push()/pop() on current thread.
 * <p/>
 * 
 * There is NO exact mapping between java StackTraceElement and CallStackElt:
 * - only methodf with instrumented push-pop call to applicative "LocalCallStack" will maintain synchro between java stack and applicative stack
 * - when not creating applicative CallStackElt for a method => CallStackElt is a partial sub-list of java thread 
 * - when creating extra "virtual" element => CallStackElt can be used to categorize calls, using parameter values
 *    
 * <PRE>
 *   Applicative CallStack                       Java Thread Stack
 *                                                -----
 *                                                  |
 *                                    no sync -->   |   meth44() { .. }
 *   +-------------+                  no sync -->   |   meth43() { .. meth44(); ..}
 *   | callElt Curr|   <-- sync with push-pop -->   |   meth42() { .. meth43(); .. }  
 *   +-------------+                  no sync -->   |   meth41() { .. meth42(); .. }
 *                                    no sync -->   |   meth40() { .. meth41(); .. }
 *                                                  |
 *                                                  |
 *                                                  |
 *                                                  |   meth24 { .. meth25(); .. }
 *   +-------------+                                        |
 *   | callElt 2   |  <-- dummy Call(no sync)               |
 *   +-------------+                                        |
 *                                                  |   meth23 { .. meth24(); .. }
 *   |  ..         |                                |
 *   |  ..         |                                |
 *                                                  |
 *   +-------------+                                |
 *   | callElt 2   |  <--   sync               -->  |
 *   +-------------+                                |
 *                                                  |
 *   +-------------+                                |
 *   |  callElt 1  |  <--   sync               -->  |   meth3() { .. meth4(); .. }
 *   +-------------+                                |   meth2() { .. meth3(); .. }
 *                                                  |   meth1() { .. meth2(); .. }
 *   
 * </PRE>
 * 
 * 
 * <PRE>
 *                    <- "try { toPop=LocalCallStack().push()"   
 *                       when entering new "method" : push new element on stack
 *                       \
 *   +-------------+     /
 *   | callElt Curr|    <-- curr stack position
 *   +-------------+     \
 *   |                   /
 *   |                <-  "} finally { toPop.close(); }"
 *   |                     when exiting curr "method" : pop element on stack
 *   |  ..                
 *   |  ..
 *   +-------------+
 *   | callElt 2   |
 *   +-------------+
 *   |  callElt 1  |
 *   +-------------+
 * </PRE>
 * 
 */
public class CallStack {

	private static final int DEFAULT_PREALLOC_STACK_LEN = 10;
	private static final int DEFAULT_ALLOC_INCR_STACK_LEN = 5;
	
	private CallStackElt curr;
	private CallStackElt[] stackElts;
	
	// ------------------------------------------------------------------------
	
	public CallStack() {
		this.stackElts = new CallStackElt[1];
		this.stackElts[0] = new CallStackElt(this, 0, null);
		reallocStackEltArray(DEFAULT_PREALLOC_STACK_LEN);
		this.curr = stackElts[0];
	}
	

	// ------------------------------------------------------------------------
	
	public CallStackElt curr() {
		return curr;
	}
	
	// internal
	// ------------------------------------------------------------------------
	
	private void reallocStackEltArray(int stackLen) {
		CallStackElt[] prevStackElts = stackElts;
		CallStackElt[] newStackElts = new CallStackElt[stackLen];
		System.arraycopy(prevStackElts, 0, newStackElts, 0, prevStackElts.length);
		for(int i = prevStackElts.length; i < stackLen; i++) {
			newStackElts[i] = new CallStackElt(this, i, newStackElts[i-1]);
			newStackElts[i - 1].pusher = new CallStackElt.StackPusher(newStackElts[i]);
		}
		this.stackElts = newStackElts;
	}


	/*pp*/ StackPopper doPush(CallStackElt pushedElt) {
		if (pushedElt.pusher == null) {
			reallocStackEltArray(this.stackElts.length + DEFAULT_ALLOC_INCR_STACK_LEN);
		}
		this.curr = pushedElt;
		pushedElt.onPush();
		return pushedElt.popper;
	}

   /*pp*/ StackPopper doPushWithParentStartTime(CallStackElt pushedElt) {
        if (pushedElt.pusher == null) {
            reallocStackEltArray(this.stackElts.length + DEFAULT_ALLOC_INCR_STACK_LEN);
        }
        this.curr = pushedElt;
        pushedElt.onPushWithParentStartTime();
        return pushedElt.popper;
    }

	/*pp*/ void doPop(CallStackElt poppedElt) {
		this.curr = poppedElt.getParentCallStackElt();
		poppedElt.onPop();
	}
	
}
