package org.sef4j.core.api.proptree;

@FunctionalInterface
public interface PropTreeValuePredicate {

	boolean apply(PropTreeNode node, String propName, Object propValue);
	
}
