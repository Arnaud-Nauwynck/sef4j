package org.sef4j.core.api.proptree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * copy Mapper for PropTreeNode -> PropTreeNodeDTO
 * 
 * <PRE>
 * PropTreeNode                 +---------------------+        PropTreeNodeDTO
 *  |   \                       | PropTreeNodeMapper  |         | \
 *  |    + prop1: value1  ----> |     + prop1:mapper  |  -->    |  + prop1: MappedValue1
 *  |    + prop2: value2        |                     |         |  ... (some props skipped)
 *  |    ...                    +---------------------+         |
 *  |                                                           |
 *  + child1                                                    + child1
 *     + child11                                                    + child11
 *       ..                                                           .. (some child skipped)
 *  + child2                                                    + child2
 *     + child21                                                    + child21
 *       ..                                                           .. (some props skipped)
 * </PRE>
 * 
 * delegate to PropTreeValueMapper for mapping property values
 * see also Orika Mapper sub-class OrikaPropTreeValueMapper for copying some key-value properties
 */
public class PropTreeNodeDTOMapper {

	public static class PropMapperEntry {
		public final String propName;
		public final String destPropName;
		public final PropTreeValueMapper mapper;
		public final PropTreeValuePredicate<Object> propPredicate;
		public final Predicate<Object> valuePredicate;
		
		@SuppressWarnings("unchecked")
		public PropMapperEntry(String propName, String destPropName, 
				PropTreeValueMapper mapper, 
				PropTreeValuePredicate<?> propPredicate,
				Predicate<?> valuePredicate
				) {
			this.propName = propName;
			if (destPropName == null) destPropName = propName;
			this.destPropName = destPropName;
			this.mapper = mapper;
			this.propPredicate = (PropTreeValuePredicate<Object>) propPredicate;
			this.valuePredicate = (Predicate<Object>) valuePredicate;
		}
		
	}
	
	private final PropMapperEntry[] propMapperEntries;
	private final int maxDepth;

	private final Predicate<PropTreeNode> nodePredicate;
	private final Predicate<PropTreeNode> recurseNodePredicate;
	
	// ------------------------------------------------------------------------

	protected PropTreeNodeDTOMapper(Builder b) {
		this.propMapperEntries = b.propMapperEntries.toArray(new PropMapperEntry[b.propMapperEntries.size()]);
		this.maxDepth = b.maxDepth;
		this.nodePredicate = b.nodePredicate;
		this.recurseNodePredicate = b.recurseNodePredicate;
	}
	
	public static class Builder {
		private List<PropMapperEntry> propMapperEntries = new ArrayList<PropMapperEntry>();
		private int maxDepth = -1;
		private Predicate<PropTreeNode> nodePredicate;
		private Predicate<PropTreeNode> recurseNodePredicate;
	
		public PropTreeNodeDTOMapper build() {
			return new PropTreeNodeDTOMapper(this);
		}

		public Builder withPropMapperEntries(PropMapperEntry... p) {
			this.propMapperEntries.addAll(Arrays.asList(p));
			return this;
		}
		public Builder withMaxDepth(int p) {
			this.maxDepth = p;
			return this;
		}
		public Builder withNodePredicate(Predicate<PropTreeNode> p) {
			this.nodePredicate = p;
			return this;
		}
		public Builder withRecuseNodePredicate(Predicate<PropTreeNode> p) {
			this.recurseNodePredicate = p;
			return this;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public PropTreeNodeDTO map(PropTreeNode src) {
		PropTreeNodeDTO res = PropTreeNodeDTO.newRoot();
		recursiveCopyToDTO(src, res, maxDepth);
		return res;
	}
	
	protected void recursiveCopyToDTO(PropTreeNode src, PropTreeNodeDTO dest, int remainDepth) {
		if (nodePredicate == null || nodePredicate.test(src)) {
			// Notice: map propValues may change in another thread => should copy to apply filtering then conversion...
			copyPropValues(src, dest);
		}
		
		if (remainDepth == -1 || remainDepth > 0) {
			int childMaxDepth = (remainDepth == -1)? -1 : remainDepth-1;
			for(Map.Entry<String,PropTreeNode> e : src.getChildMap().entrySet()) { // read-only ref (copy-on-write field)
				String childName = e.getKey();
				if (childName == null) {
				    continue; //should not occur!!
				}
				PropTreeNode srcChild = e.getValue();
				PropTreeNodeDTO destChild = dest.getOrCreateChild(childName);
				// *** recurse ***
				if (srcChild.getParent() == null || srcChild.getParent().getParent() == null 
						|| recurseNodePredicate == null || recurseNodePredicate.test(srcChild)) {
					recursiveCopyToDTO(srcChild, destChild, childMaxDepth);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected  void copyPropValues(PropTreeNode src, PropTreeNodeDTO dest) {
		Map<String, Object> propsMap = src.getPropsMap(); // read-only ref (copy-on-write field)
		for (PropMapperEntry e : propMapperEntries) {
			String propName = e.propName;
			Object propValue = propsMap.get(propName);
			if (propValue == null) {
				continue; // value not present on this node
			}
			// propValue may change in another thread => create local immutable copy to apply filtering then conversion...
			if (propValue instanceof ICopySupport) {
				propValue = ((ICopySupport<Object>) propValue).copy();
			}
			if (e.propPredicate != null && ! e.propPredicate.test(src, propName, propValue)) {
				continue; // ignore this prop!
			}
			if (e.valuePredicate != null && ! e.valuePredicate.test(propValue)) {
				continue; // ignore this prop!
			}
			Object destPropValue = e.mapper.mapProp(src, propName, propValue);
			if (destPropValue != null) {
				dest.putProp(e.destPropName, destPropValue);
			}
		}
	}
	
}
