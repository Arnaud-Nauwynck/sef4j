package org.sef4j.callstack;

import org.sef4j.callstack.CallStackElt.StackPopper;

/**
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
	
	/*pp*/ void doPop(CallStackElt poppedElt) {
		this.curr = poppedElt.getParentCallStackElt();
		poppedElt.onPop();
	}
	
}
