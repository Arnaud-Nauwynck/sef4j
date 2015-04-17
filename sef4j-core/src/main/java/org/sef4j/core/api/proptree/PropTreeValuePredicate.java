package org.sef4j.core.api.proptree;

@FunctionalInterface
public interface PropTreeValuePredicate {

	boolean apply(PropTreeNode node, String propName, Object propValue);

	
	// ------------------------------------------------------------------------
	
	public static abstract class AbstractTypedPropTreeValuePredicate<T> implements PropTreeValuePredicate {

		@SuppressWarnings("unchecked")
		public boolean apply(PropTreeNode node, String propName, Object propValue) {
			return apply((T) propValue);
		}

		public abstract boolean apply(T propValue);
	}
	
}
