package org.sef4j.core.helpers.proptree.changes;

import java.util.Map;
import java.util.function.Function;

import org.sef4j.core.helpers.export.ExportFragmentList;
import org.sef4j.core.helpers.export.ExportFragmentsAdder;
import org.sef4j.core.helpers.export.ExportFragmentsProvider;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;

/**
 * collector for changed values detection since previous marked copy
 * 
 * <PRE>
 * 
 * </PRE>
 * 
 */
public abstract class AbstractPropTreeValueChangeCollector<TValue> 
		implements ExportFragmentsProvider<TValue> {
	
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

	@Override
	public void provideFragments(ExportFragmentsAdder<TValue> out) {
		for(Map.Entry<String, PropTreeNode> srcEntry : srcRoot.getChildMap().entrySet()) {
			String childName = srcEntry.getKey();
			PropTreeNode srcChild = srcEntry.getValue();
			String childPath = childName;
			
			// *** recurse ***
			recursiveProvideFragments(srcChild, childPath, out);
		}
	}

	public void markAndCollectChanges(ExportFragmentsAdder<TValue> out) {
		for(Map.Entry<String, PropTreeNode> srcEntry : srcRoot.getChildMap().entrySet()) {
			String childName = srcEntry.getKey();
			PropTreeNode srcChild = srcEntry.getValue();
			PropTreeNode prevChild = prevRoot.getOrCreateChild(childName);
			String childPath = childName;
			
			// *** recurse ***
			recursiveMarkAndCollectChanges(srcChild, prevChild, childPath, out);
		}
	}

	/** helper for markAndCollectChanges() + convert result to Map<> */
	public Map<Object,TValue> markAndCollectChanges() {
		ExportFragmentList<TValue> changes = new ExportFragmentList<TValue>();
		markAndCollectChanges(changes);
		return changes.identifiableFragmentsToValuesMap();
	}

	
	protected void recursiveProvideFragments(PropTreeNode src, String currPath, ExportFragmentsAdder<TValue> out) {
		provideFragments(src, currPath, out);

		// recurse
		for(Map.Entry<String, PropTreeNode> srcEntry : src.getChildMap().entrySet()) {
			String childName = srcEntry.getKey();
			PropTreeNode srcChild = srcEntry.getValue();
			String childPath = currPath + "/" + childName;
			
			// *** recurse ***
			recursiveProvideFragments(srcChild, childPath, out);
		}
	}

	protected void recursiveMarkAndCollectChanges(PropTreeNode src, PropTreeNode prev, 
			String currPath, ExportFragmentsAdder<TValue> out) {
		
		markAndCollectChanges(src, prev, currPath, out);

		// recurse
		for(Map.Entry<String, PropTreeNode> srcEntry : src.getChildMap().entrySet()) {
			String childName = srcEntry.getKey();
			PropTreeNode srcChild = srcEntry.getValue();
			PropTreeNode prevChild = prev.getOrCreateChild(childName);
			String childPath = currPath + "/" + childName;
			
			// *** recurse ***
			recursiveMarkAndCollectChanges(srcChild, prevChild, childPath, out);
		}
	}


	protected abstract void provideFragments(PropTreeNode src, String currPath, 
			ExportFragmentsAdder<TValue> out);

	protected abstract void markAndCollectChanges(PropTreeNode src, PropTreeNode prev, String currPath, 
			ExportFragmentsAdder<TValue> res);
	
}
