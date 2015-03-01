package org.sef4j.callstack.stattree.changecollector;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * collector for changes in statistics (pendingCount or histogram time counts) 
 * since previous marked copy
 */
public abstract class AbstractCallTreeNodeChangeCollector<TValue> {
	
	protected CallTreeNode srcRoot;

	protected CallTreeNode prevRoot;
	
	protected Function<CallTreeNode, TValue> srcValueCopyExtractor;
	protected Function<CallTreeNode, TValue> prevValueExtractor;

	// ------------------------------------------------------------------------
	
	public AbstractCallTreeNodeChangeCollector(
			CallTreeNode srcRoot,
			CallTreeNode prevRoot,
			Function<CallTreeNode, TValue> srcValueCopyExtractor,
			Function<CallTreeNode, TValue> prevValueExtractor) {
		this.srcRoot = srcRoot;
		this.prevRoot = prevRoot;
		this.srcValueCopyExtractor = srcValueCopyExtractor;
		this.prevValueExtractor = prevValueExtractor;
	}
	
	// ------------------------------------------------------------------------

	public Map<String,TValue> markAndCollectChanges() {
		Map<String,TValue> res = new HashMap<String,TValue>();
		recursiveMarkAndCollectChanges_root(res);
		return res;
	}

	protected abstract void recursiveMarkAndCollectChanges_root(Map<String,TValue> res);

	protected abstract void recursiveMarkAndCollectChanges(CallTreeNode src, CallTreeNode prev, 
			String currPath, Map<String,TValue> res);
	
}
