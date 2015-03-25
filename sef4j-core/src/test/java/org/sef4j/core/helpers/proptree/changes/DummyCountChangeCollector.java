package org.sef4j.core.helpers.proptree.changes;

import java.util.Map;
import java.util.function.Function;

import org.sef4j.core.api.proptree.PropTreeNode;
import org.sef4j.core.helpers.proptree.DummyCount;

/**
 * collector of changed DummyCount since previous copy
 */
public class DummyCountChangeCollector extends AbstractPropTreeValueChangeCollector<DummyCount> {

	public static final Function<PropTreeNode, DummyCount> DEFAULT_DUMMYCOUNT_SRC_COPY_EXTRACTOR = 
			new Function<PropTreeNode, DummyCount>() {
		@Override
		public DummyCount apply(PropTreeNode t) {
			return t.getOrCreateProp("dummyCount", DummyCount.FACTORY).getCopy();
		}
	};
	public static final Function<PropTreeNode, DummyCount> DEFAULT_DUMMYCOUNT_PREV_EXTRACTOR = 
			new Function<PropTreeNode, DummyCount>() {
		@Override
		public DummyCount apply(PropTreeNode t) {
			return t.getOrCreateProp("prev-dummyCount", DummyCount.FACTORY);
		}
	};

	// ------------------------------------------------------------------------

	public DummyCountChangeCollector(PropTreeNode srcRoot) {
		super(srcRoot, srcRoot, DEFAULT_DUMMYCOUNT_SRC_COPY_EXTRACTOR, DEFAULT_DUMMYCOUNT_PREV_EXTRACTOR);
	}

	public DummyCountChangeCollector(
			PropTreeNode srcRoot,
			PropTreeNode prevRoot,
			Function<PropTreeNode, DummyCount> srcValueCopyExtractor,
			Function<PropTreeNode, DummyCount> prevValueExtractor) {
		super(srcRoot, prevRoot, srcValueCopyExtractor, prevValueExtractor);
	}

	// ------------------------------------------------------------------------
	
	protected void recursiveMarkAndCollectChanges_root(Map<String,DummyCount> res) {
		for(Map.Entry<String, PropTreeNode> srcEntry : srcRoot.getChildMap().entrySet()) {
			String childName = srcEntry.getKey();
			PropTreeNode srcChild = srcEntry.getValue();
			PropTreeNode prevChild = prevRoot.getOrCreateChild(childName);
			String childPath = childName;
			
			// *** recurse ***
			recursiveMarkAndCollectChanges(srcChild, prevChild, childPath, res);
		}
	}

	protected void recursiveMarkAndCollectChanges(PropTreeNode src, PropTreeNode prev, 
			String currPath, Map<String,DummyCount> res) {
		DummyCount srcDummyCount = srcValueCopyExtractor.apply(src); // copy new value
		DummyCount prevDummyCount = prevValueExtractor.apply(prev); // by ref
		if (compareHasChange(srcDummyCount, prevDummyCount)) {
			prevDummyCount.setCopy(srcDummyCount);
			res.put(currPath, srcDummyCount);
		}
		
		// recurse
		for(Map.Entry<String, PropTreeNode> srcEntry : src.getChildMap().entrySet()) {
			String childName = srcEntry.getKey();
			PropTreeNode srcChild = srcEntry.getValue();
			PropTreeNode prevChild = prev.getOrCreateChild(childName);
			String childPath = currPath + "/" + childName;
			
			// *** recurse ***
			recursiveMarkAndCollectChanges(srcChild, prevChild, childPath, res);
		}
		// compare child removal... not used!
	}
	
	protected boolean compareHasChange(DummyCount src, DummyCount prev) {
		if (src.getCount1() != prev.getCount1()
		        || src.getCount2() != prev.getCount2()) {
			return true;
		}
		return false;
	}

}