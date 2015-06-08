package org.sef4j.core.helpers.proptree.model;

public interface PropTreeValueCallback<T> {

	public void doWith(PropTreeNode node, String propName, T propValue);
	
}
