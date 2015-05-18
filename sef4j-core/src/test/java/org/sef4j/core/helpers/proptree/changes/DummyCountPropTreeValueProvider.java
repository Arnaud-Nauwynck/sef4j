package org.sef4j.core.helpers.proptree.changes;

import java.util.function.Function;

import org.sef4j.core.helpers.export.ExportFragmentsAdder;
import org.sef4j.core.helpers.proptree.DummyCount;
import org.sef4j.core.helpers.proptree.model.PropTreeNode;

/**
 * collector of changed DummyCount since previous copy
 */
public class DummyCountPropTreeValueProvider extends AbstractPropTreeValueProvider<DummyCount> {

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

	public DummyCountPropTreeValueProvider(PropTreeNode srcRoot) {
		super(srcRoot, srcRoot, DEFAULT_DUMMYCOUNT_SRC_COPY_EXTRACTOR, DEFAULT_DUMMYCOUNT_PREV_EXTRACTOR);
	}

	public DummyCountPropTreeValueProvider(
			PropTreeNode srcRoot,
			PropTreeNode prevRoot,
			Function<PropTreeNode, DummyCount> srcValueCopyExtractor,
			Function<PropTreeNode, DummyCount> prevValueExtractor) {
		super(srcRoot, prevRoot, srcValueCopyExtractor, prevValueExtractor);
	}

	// ------------------------------------------------------------------------

	@Override
	protected void provideFragments(PropTreeNode src, String currPath, 
			ExportFragmentsAdder<DummyCount> res) {
		DummyCount srcDummyCount = srcValueCopyExtractor.apply(src); // copy new value
		res.putIdentifiableFragment(this, currPath, srcDummyCount, 0);
	}

	@Override
	protected void markAndCollectChanges(PropTreeNode src, PropTreeNode prev, 
			String currPath, ExportFragmentsAdder<DummyCount> res) {
		DummyCount srcDummyCount = srcValueCopyExtractor.apply(src); // copy new value
		DummyCount prevDummyCount = prevValueExtractor.apply(prev); // by ref
		if (compareHasChange(srcDummyCount, prevDummyCount)) {
			prevDummyCount.setCopy(srcDummyCount);
			res.putIdentifiableFragment(this, currPath, srcDummyCount, 0);
		}
	}
	
	protected boolean compareHasChange(DummyCount src, DummyCount prev) {
		if (src.getCount1() != prev.getCount1()
		        || src.getCount2() != prev.getCount2()) {
			return true;
		}
		return false;
	}

}