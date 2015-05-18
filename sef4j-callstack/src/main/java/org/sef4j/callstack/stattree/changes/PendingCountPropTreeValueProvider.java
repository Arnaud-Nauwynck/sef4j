package org.sef4j.callstack.stattree.changes;

import java.util.Map;
import java.util.function.Function;

import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.callstack.stats.dto.PendingCountPropTreeValueProviderDef;
import org.sef4j.core.helpers.export.ExportFragmentsAdder;
import org.sef4j.core.helpers.proptree.changes.AbstractPropTreeValueProvider;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;
import org.sef4j.core.util.factorydef.AbstractSharedObjByDefFactory;
import org.sef4j.core.util.factorydef.DependencyObjectCreationContext;

/**
 * Collector of changed PendingPerfCount since previous copy
 * 
 * usage note: 
 * PendingPerfCount reflects the running activity of the process, and changes could be collected
 * in pseudo real-time with a fast period (example: every 10 seconds).
 * On the contrary, average statistics could be collected with a slower period (example: every 1 minute)
 * 
 * Changes in PendingPerfCount can be optimized much more than changes in statistics, because of constraints 
 * between parent and child pendings counters.
 * 
 * 
 * Optimisation note: 
 *  knowing that sum_child pending counts <= pendings counts
 *  => can skip part of recursions, when src.pending=0 or prev.pending=0..
 *  
 * indeed:
 * <PRE>
 * foo() {                                       /foo.pending   /foo/bar.pending
 *    push   -->  incrPending() "/foo"               1           0
 *    
 *    bar(); -->  incrPending() "/foo/bar"           1           1
 *           -->  decrPending() "/foo/bar"           1           0
 *             
 *    pop()  -->  decrPending(); "/foo"              0           0
 * }
 * </PRE>
 */
public final class PendingCountPropTreeValueProvider extends AbstractPropTreeValueProvider<PendingPerfCount> {
	
	public static final Function<PropTreeNode, PendingPerfCount> DEFAULT_PENDING_SRC_COPY_EXTRACTOR = 
			new Function<PropTreeNode, PendingPerfCount>() {
		@Override
		public PendingPerfCount apply(PropTreeNode t) {
			return t.getOrCreateProp("stats", PerfStats.FACTORY).getPendingCounts().clone();
		}
	};
	public static final Function<PropTreeNode, PendingPerfCount> DEFAULT_PENDING_PREV_EXTRACTOR = 
			new Function<PropTreeNode, PendingPerfCount>() {
		@Override
		public PendingPerfCount apply(PropTreeNode t) {
			return t.getOrCreateProp("stats", PerfStats.FACTORY).getPendingCounts();
		}
	};
	public static final Function<PropTreeNode, PendingPerfCount> createGetOrCreatePropPendingExtractor(final String propName) {
		return new Function<PropTreeNode, PendingPerfCount>() {
			@Override
			public PendingPerfCount apply(PropTreeNode t) {
				return t.getOrCreateProp(propName, PendingPerfCount.FACTORY);
			}
		};
	}


	// ------------------------------------------------------------------------

	public PendingCountPropTreeValueProvider(PropTreeNode srcRoot) {
		super(srcRoot, PropTreeNode.newRoot(), DEFAULT_PENDING_SRC_COPY_EXTRACTOR, DEFAULT_PENDING_PREV_EXTRACTOR);
	}

	public PendingCountPropTreeValueProvider(
			PropTreeNode srcRoot,
			PropTreeNode prevRoot,
			Function<PropTreeNode, PendingPerfCount> srcCopyExtractor,
			Function<PropTreeNode, PendingPerfCount> prevExtractor) {
		super(srcRoot, prevRoot, srcCopyExtractor, prevExtractor);
	}

	// ------------------------------------------------------------------------
	
	
	@Override
	protected void provideFragments(PropTreeNode src, String currPath, 
			ExportFragmentsAdder<PendingPerfCount> out) {
		PendingPerfCount srcPendingsCopy = srcValueCopyExtractor.apply(src);
		out.putIdentifiableFragment(this, currPath, srcPendingsCopy, 0);
	}

	@Override
	protected void markAndCollectChanges(PropTreeNode src, PropTreeNode prev, String currPath, ExportFragmentsAdder<PendingPerfCount> res) {
		throw new IllegalStateException(); // cf overriden recursiveMarkAndCollectChanges
	}
	
	@Override
	protected void recursiveMarkAndCollectChanges(PropTreeNode src, PropTreeNode prev, 
			String currPath, ExportFragmentsAdder<PendingPerfCount> out) {
		PendingPerfCount srcPendingsCopy = srcValueCopyExtractor.apply(src);
		PendingPerfCount prevPendings = prevValueExtractor.apply(prev);
		
		int srcPendingCount = srcPendingsCopy.getPendingCount();
		int prevPendingCount = prevPendings.getPendingCount();

		if (srcPendingCount == 0 && prevPendingCount == 0) {
			// skip recurse compare: both empty!
			return;
		}
		
		if (srcPendingCount != prevPendingCount
				|| srcPendingsCopy.getPendingSumStartTime() != prevPendings.getPendingSumStartTime()
				) {
			prevPendings.set(srcPendingsCopy);
			out.putIdentifiableFragment(this, currPath, srcPendingsCopy, 0);
		}

		if (srcPendingCount != 0 && prevPendingCount != 0) {
			// recurse
			for(Map.Entry<String, PropTreeNode> srcEntry : src.getChildMap().entrySet()) {
				String childName = srcEntry.getKey();
				PropTreeNode srcChild = srcEntry.getValue();
				PropTreeNode prevChild = prev.getOrCreateChild(childName);
				String childPath = currPath + "/" + childName;
				
				// *** recurse ***
				recursiveMarkAndCollectChanges(srcChild, prevChild, childPath, out);
			}
		} else if (srcPendingCount == 0) {
			// recurse optim removePending
			for(Map.Entry<String, PropTreeNode> srcEntry : src.getChildMap().entrySet()) {
				String childName = srcEntry.getKey();
				PropTreeNode srcChild = srcEntry.getValue();
				PropTreeNode prevChild = prev.getOrCreateChild(childName);
				String childPath = currPath + "/" + childName;
				
				PendingPerfCount prevChildPendings = prevValueExtractor.apply(prevChild);
				
				// *** recurse ***
				recursiveMarkAndCollectChanges_removePending(srcChild, prevChild, prevChildPendings, childPath, out);
			}
		} else if (prevPendingCount == 0) {
			// recurse optim addPending
			for(Map.Entry<String, PropTreeNode> srcEntry : src.getChildMap().entrySet()) {
				String childName = srcEntry.getKey();
				PropTreeNode srcChild = srcEntry.getValue();
				PropTreeNode prevChild = prev.getOrCreateChild(childName);
				String childPath = currPath + "/" + childName;

				PendingPerfCount srcChildPendingsCopy = srcValueCopyExtractor.apply(srcChild);

				// *** recurse ***
				recursiveMarkAndCollectChanges_addPending(srcChild, srcChildPendingsCopy, prevChild, childPath, out);
			}
		}
		// compare child removal... not used!
	}


	/** optimized version of recursiveMarkAndCollectChanges() with src.pendingsCount=0 */
	protected void recursiveMarkAndCollectChanges_removePending(
			PropTreeNode src, PropTreeNode prev, PendingPerfCount prevPendings,
			String currPath, ExportFragmentsAdder<PendingPerfCount> res) {
		// 0 ... PendingPerfCount srcPendingsCopy = srcValueCopyExtractor.apply(src);
		// PendingPerfCount prevPendings = prevValueExtractor.apply(prev);
		
		int prevPendingCount = prevPendings.getPendingCount();

		if (prevPendingCount == 0) {
			// skip recurse compare: both empty!
			return;
		}

		// collect change
		prevPendings.clear();
		res.putIdentifiableFragment(this, currPath, prevPendings, 0);

		// loop child (until reaching sum=prevPendings)
		int remainingMaxChildPrevPendingCount = prevPendingCount;
		for(Map.Entry<String, PropTreeNode> srcEntry : src.getChildMap().entrySet()) {
			String childName = srcEntry.getKey();
			PropTreeNode srcChild = srcEntry.getValue();
			PropTreeNode prevChild = prev.getOrCreateChild(childName);
			String childPath = currPath + "/" + childName;
			
			PendingPerfCount prevChildPendings = prevValueExtractor.apply(prev);
							
			// *** recurse ***
			recursiveMarkAndCollectChanges_removePending(srcChild, prevChild, prevChildPendings, 
					childPath, res);
			
			remainingMaxChildPrevPendingCount -= prevChildPendings.getPendingCount();
			if (remainingMaxChildPrevPendingCount == 0) {
				break; // optim.. expecting no more child with prev pendingCount != 0
			}
		}
	}
	
	/** optimized version of recursiveMarkAndCollectChanges() with prev.pendingsCount=0 */
	protected void recursiveMarkAndCollectChanges_addPending(
			PropTreeNode src, PendingPerfCount srcPendingsCopy, PropTreeNode prev, 
			String currPath, ExportFragmentsAdder<PendingPerfCount> res) {
		// PendingPerfCount srcPendingsCopy = srcValueCopyExtractor.apply(src);
		PendingPerfCount prevPendings = prevValueExtractor.apply(prev);
		// 0 ... prevPendings.getPendingCount()
		
		int srcPendingCount = srcPendingsCopy.getPendingCount();
		
		if (srcPendingCount == 0) {
			// skip recurse compare: both empty!
			return;
		}

		// collect change
		prevPendings.set(srcPendingsCopy);
		res.putIdentifiableFragment(this, currPath, srcPendingsCopy, 0);

		// loop child (until reaching sum=prevPendings)
		int remainingMaxChildSrcPendingCount = srcPendingCount;
		for(Map.Entry<String, PropTreeNode> srcEntry : src.getChildMap().entrySet()) {
			String childName = srcEntry.getKey();
			PropTreeNode srcChild = srcEntry.getValue();
			PropTreeNode prevChild = prev.getOrCreateChild(childName);
			String childPath = currPath + "/" + childName;
			
			PendingPerfCount srcChildPendingsCopy = srcValueCopyExtractor.apply(srcChild);
			
			// *** recurse ***
			recursiveMarkAndCollectChanges_addPending(srcChild, srcChildPendingsCopy, prevChild, childPath, res);
			
			remainingMaxChildSrcPendingCount -= srcChildPendingsCopy.getPendingCount();
			if (remainingMaxChildSrcPendingCount == 0) {
				break; // optim.. expecting no more child with src pendingCount != 0
			}
		}

	}

	// ------------------------------------------------------------------------
	
	public static class Factory 
		extends ExportFragmentsProviderFactory<PendingCountPropTreeValueProviderDef,PendingCountPropTreeValueProvider> {
		
		public Factory() {
			super("PendingCountPropTreeValueProvider", PendingCountPropTreeValueProviderDef.class);
		}
	
		@Override
		public PendingCountPropTreeValueProvider create(
				PendingCountPropTreeValueProviderDef def, 
				DependencyObjectCreationContext ctx) {
			PropTreeNode rootNode = ctx.getOrCreateDependencyByDef("rootNode", def.getRootNodeDef());
			return new PendingCountPropTreeValueProvider(rootNode);
		}
		
	}

}