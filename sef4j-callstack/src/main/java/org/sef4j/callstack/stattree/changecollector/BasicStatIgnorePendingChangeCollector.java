package org.sef4j.callstack.stattree.changecollector;

import java.util.Map;
import java.util.function.Function;

import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stattree.CallTreeNode;

/**
 * collector of changed PerfStats since previous copy, ignoring Pending counts
 * 
 * this is a "basic" implementation: no optimization to avoid recursing in untouched sub-tree
 * A better implementation should count occurrences+pending to check when a sub-tree is unmodified.
 */
public class BasicStatIgnorePendingChangeCollector extends AbstractCallTreeNodeChangeCollector<PerfStats> {

	public static final Function<CallTreeNode, PerfStats> DEFAULT_PERFSTAT_SRC_COPY_EXTRACTOR = 
			new Function<CallTreeNode, PerfStats>() {
		@Override
		public PerfStats apply(CallTreeNode t) {
			return t.getStats().getCopy();
		}
	};
	public static final Function<CallTreeNode, PerfStats> DEFAULT_PERFSTAT_PREV_EXTRACTOR = 
			new Function<CallTreeNode, PerfStats>() {
		@Override
		public PerfStats apply(CallTreeNode t) {
			return t.getStats();
		}
	};

	// ------------------------------------------------------------------------

	public BasicStatIgnorePendingChangeCollector(CallTreeNode srcRoot) {
		super(srcRoot, CallTreeNode.newRoot(), DEFAULT_PERFSTAT_SRC_COPY_EXTRACTOR, DEFAULT_PERFSTAT_PREV_EXTRACTOR);
	}

	public BasicStatIgnorePendingChangeCollector(
			CallTreeNode srcRoot,
			CallTreeNode prevRoot,
			Function<CallTreeNode, PerfStats> srcValueCopyExtractor,
			Function<CallTreeNode, PerfStats> prevValueExtractor) {
		super(srcRoot, prevRoot, srcValueCopyExtractor, prevValueExtractor);
	}

	// ------------------------------------------------------------------------
	
	protected void recursiveMarkAndCollectChanges_root(Map<String,PerfStats> res) {
		for(Map.Entry<String, CallTreeNode> srcEntry : srcRoot.getChildMap().entrySet()) {
			String childName = srcEntry.getKey();
			CallTreeNode srcChild = srcEntry.getValue();
			CallTreeNode prevChild = prevRoot.getOrCreateChild(childName);
			String childPath = childName;
			
			// *** recurse ***
			recursiveMarkAndCollectChanges(srcChild, prevChild, childPath, res);
		}
	}

	protected void recursiveMarkAndCollectChanges(CallTreeNode src, CallTreeNode prev, 
			String currPath, Map<String,PerfStats> res) {
		PerfStats srcPerfStats = srcValueCopyExtractor.apply(src); // copy new value
		PerfStats prevPerfStats = prevValueExtractor.apply(prev); // by ref
		if (compareHasChangeCount(srcPerfStats, prevPerfStats)) {
			prevPerfStats.setCopy(srcPerfStats);
			res.put(currPath, srcPerfStats);
		}
		
		// recurse
		for(Map.Entry<String, CallTreeNode> srcEntry : src.getChildMap().entrySet()) {
			String childName = srcEntry.getKey();
			CallTreeNode srcChild = srcEntry.getValue();
			CallTreeNode prevChild = prev.getOrCreateChild(childName);
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