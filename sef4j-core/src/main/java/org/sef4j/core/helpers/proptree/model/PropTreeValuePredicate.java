package org.sef4j.core.helpers.proptree.model;

import java.util.function.Predicate;

@FunctionalInterface
public interface PropTreeValuePredicate<T> {

	boolean test(PropTreeNode node, String propName, T propValue);

	
	// ------------------------------------------------------------------------

	public static class DelegatePropTreeValuePredicate<T> implements PropTreeValuePredicate<T> {
		private final Predicate<T> delegate;
		
		public DelegatePropTreeValuePredicate(Predicate<T> delegate) {
			this.delegate = delegate;
		}

		public static <T> DelegatePropTreeValuePredicate<T> wrapOrNull(Predicate<T> delegate) {
			return delegate != null? new DelegatePropTreeValuePredicate<T>(delegate) : null;
		}
		
		@SuppressWarnings("unchecked")
		public boolean test(PropTreeNode node, String propName, Object propValue) {
			return delegate.test((T) propValue);
		}

	}
	
}
