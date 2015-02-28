package org.sef4j.callstack.handlers;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.CallStackPushPopHandler;
import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * a CallStackPushPopHandler to update statistic on a call tree
 */
public class CallTreeStatsUpdaterCallStackHandler extends CallStackPushPopHandler {

	private CallTreeNode currNode;

	// ------------------------------------------------------------------------
	
	public CallTreeStatsUpdaterCallStackHandler(CallTreeNode root) {
		this.currNode = root;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void onPush(CallStackElt stackElt) {
		// update seldf handler for pushed elt
		String name = stackElt.getName();
		this.currNode = currNode.getOrCreateChild(name);
	
		currNode.getStats().addPending(stackElt);
		
		// add self as listener on child stack elt
		stackElt.onPushAddCallStackPushPopHandler(this);
	}

	@Override
	public void onPop(CallStackElt stackElt) {
		currNode.getStats().incrAndRemovePending(stackElt);
		
		// update seldf handler for pop elt
		this.currNode = currNode.getParent();
	}

	@Override
	public void onProgressStep(CallStackElt stackElt) {
		// do nothing
	}
	
}
