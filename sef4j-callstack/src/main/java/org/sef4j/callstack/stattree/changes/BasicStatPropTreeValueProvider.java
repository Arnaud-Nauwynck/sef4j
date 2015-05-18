package org.sef4j.callstack.stattree.changes;

import java.util.function.Function;

import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stats.dto.BasicStatPropTreeValueProviderDef;
import org.sef4j.core.helpers.export.ExportFragment;
import org.sef4j.core.helpers.export.ExportFragmentsAdder;
import org.sef4j.core.helpers.proptree.changes.AbstractPropTreeValueProvider;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.util.factorydef.DependencyObjectCreationContext;

/**
 * collector of changed PerfStats since previous copy, ignoring Pending counts
 * 
 * this is a "basic" implementation: no optimization to avoid recursing in untouched sub-tree
 * A better implementation should count occurrences+pending to check when a sub-tree is unmodified.
 */
public class BasicStatPropTreeValueProvider extends AbstractPropTreeValueProvider<PerfStats> {

	public static final Function<PropTreeNode, PerfStats> DEFAULT_PERFSTAT_SRC_COPY_EXTRACTOR = 
			new Function<PropTreeNode, PerfStats>() {
		@Override
		public PerfStats apply(PropTreeNode t) {
			return t.getOrCreateProp("stats", PerfStats.FACTORY).clone();
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

	public BasicStatPropTreeValueProvider(PropTreeNode srcRoot) {
		super(srcRoot, PropTreeNode.newRoot(), DEFAULT_PERFSTAT_SRC_COPY_EXTRACTOR, DEFAULT_PERFSTAT_PREV_EXTRACTOR);
	}

	public BasicStatPropTreeValueProvider(
			PropTreeNode srcRoot,
			PropTreeNode prevRoot,
			Function<PropTreeNode, PerfStats> srcValueCopyExtractor,
			Function<PropTreeNode, PerfStats> prevValueExtractor) {
		super(srcRoot, prevRoot, srcValueCopyExtractor, prevValueExtractor);
	}

	// ------------------------------------------------------------------------

	@Override
	protected void provideFragments(PropTreeNode src, String currPath, 
			ExportFragmentsAdder<PerfStats> out) {
		PerfStats srcPerfStats = srcValueCopyExtractor.apply(src); // copy new value
		out.addEntry(new ExportFragment<PerfStats>(this, currPath, srcPerfStats));
	}
	
	@Override
	protected void markAndCollectChanges(PropTreeNode src, PropTreeNode prev, String currPath, 
			ExportFragmentsAdder<PerfStats> out) {
		PerfStats srcPerfStats = srcValueCopyExtractor.apply(src); // copy new value
		PerfStats prevPerfStats = prevValueExtractor.apply(prev); // by ref
		if (compareHasChangeCount(srcPerfStats, prevPerfStats)) {
			prevPerfStats.set(srcPerfStats);
			out.addEntry(new ExportFragment<PerfStats>(this, currPath, srcPerfStats));
		}
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
	
	// ------------------------------------------------------------------------
	
	public static class Factory 
		extends ExportFragmentsProviderFactory<BasicStatPropTreeValueProviderDef,BasicStatPropTreeValueProvider> {
		
		public Factory() {
			super("BasicStatPropTreeValueProvider", BasicStatPropTreeValueProviderDef.class);
		}
	
		@Override
		public BasicStatPropTreeValueProvider create(
				BasicStatPropTreeValueProviderDef def, 
				DependencyObjectCreationContext ctx) {
			PropTreeNode rootNode = ctx.getOrCreateDependencyByDef("rootNode", def.getRootNodeDef());
			return new BasicStatPropTreeValueProvider(rootNode);
		}
		
	}

}