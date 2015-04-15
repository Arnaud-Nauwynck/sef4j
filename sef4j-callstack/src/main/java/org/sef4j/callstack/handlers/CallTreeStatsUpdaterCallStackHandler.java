package org.sef4j.callstack.handlers;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.CallStackPushPopHandler;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.core.api.proptree.PropTreeNode;

/**
 * a CallStackPushPopHandler to update statistic on a call tree
 */
public class CallTreeStatsUpdaterCallStackHandler extends CallStackPushPopHandler {

	private PropTreeNode currNode;

	// ------------------------------------------------------------------------
	
	public CallTreeStatsUpdaterCallStackHandler(PropTreeNode root) {
		this.currNode = root;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public void onPush(CallStackElt stackElt) {
		// update seldf handler for pushed elt
		String childName = stackElt.getClassName() + ":" + stackElt.getName();
		this.currNode = currNode.getOrCreateChild(childName);
	
		currNode.getOrCreateProp("stats", PerfStats.FACTORY).addPending(stackElt);
		
		// add self as listener on child stack elt
		stackElt.onPushAddCallStackPushPopHandler(this);
	}

	@Override
	public void onPop(CallStackElt stackElt) {
		currNode.getOrCreateProp("stats", PerfStats.FACTORY).incrAndRemovePending(stackElt);
		
		// update seldf handler for pop elt
		this.currNode = currNode.getParent();
	}

	@Override
	public void onProgressStep(CallStackElt stackElt) {
		// do nothing
	}
	
}
