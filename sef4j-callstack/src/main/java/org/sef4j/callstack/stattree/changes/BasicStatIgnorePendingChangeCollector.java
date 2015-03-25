package org.sef4j.callstack.stattree.changes;

import java.util.Map;
import java.util.function.Function;

import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.core.api.proptree.PropTreeNode;
import org.sef4j.core.helpers.proptree.changes.AbstractPropTreeValueChangeCollector;

/**
 * collector of changed PerfStats since previous copy, ignoring Pending counts
 * 
 * this is a "basic" implementation: no optimization to avoid recursing in untouched sub-tree
 * A better implementation should count occurrences+pending to check when a sub-tree is unmodified.
 */
public class BasicStatIgnorePendingChangeCollector extends AbstractPropTreeValueChangeCollector<PerfStats> {

	public static final Function<PropTreeNode, PerfStats> DEFAULT_PERFSTAT_SRC_COPY_EXTRACTOR = 
			new Function<PropTreeNode, PerfStats>() {
		@Override
		public PerfStats apply(PropTreeNode t) {
			return t.getOrCreateProp("stats", PerfStats.FACTORY).getCopy();
		}
	};
	public static final Function<PropTreeNode, PerfStats> DEFAULT_PERFSTAT_PREV_EXTRACTOR = 
			new Function<PropTreeNode, PerfStats>() {
		@Override
		public PerfStats apply(PropTreeNode t) {
			return t.getOrCreateProp("stats", PerfStats.FACTORY);
		}
	};

	// ------------------------------------------------------------------------

	public BasicStatIgnorePendingChangeCollector(PropTreeNode srcRoot) {
		super(srcRoot, PropTreeNode.newRoot(), DEFAULT_PERFSTAT_SRC_COPY_EXTRACTOR, DEFAULT_PERFSTAT_PREV_EXTRACTOR);
	}

	public BasicStatIgnorePendingChangeCollector(
			PropTreeNode srcRoot,
			PropTreeNode prevRoot,
			Function<PropTreeNode, PerfStats> srcValueCopyExtractor,
			Function<PropTreeNode, PerfStats> prevValueExtractor) {
		super(srcRoot, prevRoot, srcValueCopyExtractor, prevValueExtractor);
	}

	// ------------------------------------------------------------------------
	
	protected void recursiveMarkAndCollectChanges_root(Map<String,PerfStats> res) {
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
			String currPath, Map<String,PerfStats> res) {
		PerfStats srcPerfStats = srcValueCopyExtractor.apply(src); // copy new value
		PerfStats prevPerfStats = prevValueExtractor.apply(prev); // by ref
		if (compareHasChangeCount(srcPerfStats, prevPerfStats)) {
			prevPerfStats.setCopy(srcPerfStats);
			res.put(currPath, srcPerfStats);
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
	
	protected boolean compareHasChangeCount(PerfStats src, PerfStats prev) {
//		if (src.getPendingCount() != prev.getPendingCount()) {
//			return true;
//		}
		if (src.getElapsedTimeStats().compareHasChangeCount(prev.getElapsedTimeStats())) {
			return true;
		}
		return false;
	}

}