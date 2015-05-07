package org.sef4j.core.helpers.proptree.model;

import org.sef4j.core.util.ICopySupport;


/**
 * 
 * for real, you should use Orika mapper framework to map (copy) prop tree values...
 * cf sub class OrikaPropTreeValueMapper 
 */
public interface PropTreeValueMapper {

	public Object mapProp(PropTreeNode node, String propName, Object propValue);
	
	// ------------------------------------------------------------------------
	
	public static abstract class AbstractTypedPropTreeValueMapper<T,TDto> implements PropTreeValueMapper {
		@SuppressWarnings("unchecked")
		public TDto mapProp(PropTreeNode node, String propName, Object propValue) {
			return mapProp((T) propValue);
		}
		public abstract TDto mapProp(T propValue);
	}
	
	public static class IdentityPropTreeValueMapper implements PropTreeValueMapper {
		public static final IdentityPropTreeValueMapper INSTANCE = new IdentityPropTreeValueMapper();

		public Object mapProp(PropTreeNode node, String propName, Object propValue) {
			return propValue;
		}
	}
	
	public static class CopyPropTreeValueMapper implements PropTreeValueMapper {
		public static final CopyPropTreeValueMapper INSTANCE = new CopyPropTreeValueMapper();
		
		@SuppressWarnings("unchecked")
		public Object mapProp(PropTreeNode node, String propName, Object propValue) {
			if (propValue instanceof ICopySupport<?>) {
				return ((ICopySupport<Object>) propValue).copy();
			} else {
				// should not occur!
				return propValue;
			}
		}
	}
		
}
