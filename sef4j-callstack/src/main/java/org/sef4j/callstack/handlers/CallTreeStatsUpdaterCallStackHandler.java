package org.sef4j.callstack.handlers;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.CallStackPushPopHandler;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.helpers.proptree.model.PropTreeValueCallback;

/**
 * a CallStackPushPopHandler to update statistic on a call tree
 */
public class CallTreeStatsUpdaterCallStackHandler extends CallStackPushPopHandler {

	private PropTreeNode currNode;
	private static final String propName = "stats";
	
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

		currNode.updateOrCreateProp(propName, PerfStats.FACTORY, new PropTreeValueCallback<PerfStats>() {
			@Override
			public void doWith(PropTreeNode node, String propName, PerfStats propValue) {
				propValue.addPending(stackElt);
			}
		});
		
		// add self as listener on child stack elt
		stackElt.onPushAddCallStackPushPopHandler(this);
	}

	@Override
	public void onPop(CallStackElt stackElt) {
		currNode.updateProp(propName, PerfStats.class, new PropTreeValueCallback<PerfStats>() {
			@Override
			public void doWith(PropTreeNode node, String propName, PerfStats propValue) {
				if (propValue == null) {
					return; // should not occur!
				}
				propValue.incrAndRemovePending(stackElt);
			}
		});
		// update seldf handler for pop elt
		this.currNode = currNode.getParent();
	}

	@Override
	public void onProgressStep(CallStackElt stackElt) {
		// do nothing
	}
	
}
