package org.sef4j.core.api.proptree;

import java.util.Collection;
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
public class PropTreeNodeMapper {

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
	
	// ------------------------------------------------------------------------

	public PropTreeNodeMapper(Collection<PropMapperEntry> mapperEntries) {
		this.propMapperEntries = mapperEntries.toArray(new PropMapperEntry[mapperEntries.size()]);
	}

	// ------------------------------------------------------------------------
	
	public void recursiveCopyToDTO(PropTreeNode src, PropTreeNodeDTO dest) {
		recursiveCopyToDTO(src, dest, -1);
	}
	
	public void recursiveCopyToDTO(PropTreeNode src, PropTreeNodeDTO dest, int maxDepth) {
		copyPropValues(src, dest);
		
		if (maxDepth == -1 || maxDepth > 0) {
			int childMaxDepth = (maxDepth == -1)? -1 : maxDepth-1;
			for(Map.Entry<String,PropTreeNode> e : src.getChildMap().entrySet()) { // read-only ref (copy-on-write field)
				String childName = e.getKey();
				if (childName == null) {
				    continue; //should not occur!!
				}
				PropTreeNode srcChild = e.getValue();
				PropTreeNodeDTO destChild = dest.getOrCreateChild(childName);
				// *** recurse ***
				recursiveCopyToDTO(srcChild, destChild, childMaxDepth);
			}
		}
	}

	protected  void copyPropValues(PropTreeNode src, PropTreeNodeDTO dest) {
		Map<String, Object> propsMap = src.getPropsMap(); // read-only ref (copy-on-write field)
		for (PropMapperEntry e : propMapperEntries) {
			String propName = e.propName;
			Object propValue = propsMap.get(propName);
			if (propValue == null) {
				continue; // value not present on this node
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
