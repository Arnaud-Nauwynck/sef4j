package org.sef4j.callstack.handlers;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.CallStackPushPopHandler;


public class MaxDepthCallStackHandlerFilter extends CallStackPushPopHandler {

	private CallStackPushPopHandler target;
	private int maxDepth;
	
	private int currLevel;
	
	// ------------------------------------------------------------------------
	
	public MaxDepthCallStackHandlerFilter(CallStackPushPopHandler target, int maxDepth) {
		this.target = target;
		this.maxDepth = maxDepth;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void onPush(CallStackElt stackElt) {
		if (currLevel < maxDepth) {
			int prev = stackElt.tmpMaskOnPushAddCallStackPushPopHandler();
			try {
				// delegate call to "target" ... problem: target handler might register itself on pushed stack elt!!
				target.onPush(stackElt);
			} finally {
				stackElt.tmpUnmaskOnPushAddCallStackPushPopHandler(prev);
			}
			
			// register self on pushed elt, only for matching level
			if (currLevel < maxDepth) {
				stackElt.onPushAddCallStackPushPopHandler(this);
			}
		}
		currLevel++;
	}

	@Override
	public void onPop(CallStackElt stackElt) {
		currLevel--;
		if (currLevel-1  < maxDepth) {
			target.onPop(stackElt);
		}
	}

	@Override
	public void onProgressStep(CallStackElt stackElt) {
		if (currLevel < maxDepth) {
			target.onProgressStep(stackElt);
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "MaxDepthCallStackHandlerFilter [maxDepth=" + maxDepth 
				+ ", target=" + target 
				+ "]";
	}
	
}
