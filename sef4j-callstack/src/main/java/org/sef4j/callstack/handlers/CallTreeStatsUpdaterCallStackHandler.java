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
	PerfStats currProp;
	
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
	
		PerfStats prop = currNode.getOrCreateProp("stats", PerfStats.FACTORY);
		currProp = prop;
		prop.addPending(stackElt);
		
		// add self as listener on child stack elt
		stackElt.onPushAddCallStackPushPopHandler(this);
	}

	@Override
	public void onPop(CallStackElt stackElt) {
		PerfStats prop = (PerfStats) currNode.getPropOrNull("stats");
		if (prop == null) {
			return; // should not occur!
		}
		if (currProp != null) {
			assert prop == currProp;
		}
		prop.incrAndRemovePending(stackElt);
		
		// update seldf handler for pop elt
		this.currNode = currNode.getParent();
		currProp = null;
	}

	@Override
	public void onProgressStep(CallStackElt stackElt) {
		// do nothing
	}
	
}
