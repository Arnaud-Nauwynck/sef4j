package org.sef4j.core.helpers.proptree.changes;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.sef4j.core.api.proptree.PropTreeNode;

/**
 * collector for changes in statistics (pendingCount or histogram time counts) 
 * since previous marked copy
 */
public abstract class AbstractPropTreeValueChangeCollector<TValue> implements IPropTreeValueChangeCollector<TValue> {
	
	protected PropTreeNode srcRoot;

	protected PropTreeNode prevRoot;
	
	protected Function<PropTreeNode, TValue> srcValueCopyExtractor;
	protected Function<PropTreeNode, TValue> prevValueExtractor;

	// ------------------------------------------------------------------------
	
	public AbstractPropTreeValueChangeCollector(
			PropTreeNode srcRoot,
			PropTreeNode prevRoot,
			Function<PropTreeNode, TValue> srcValueCopyExtractor,
			Function<PropTreeNode, TValue> prevValueExtractor) {
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

	protected abstract void recursiveMarkAndCollectChanges(PropTreeNode src, PropTreeNode prev, 
			String currPath, Map<String,TValue> res);
	
}
