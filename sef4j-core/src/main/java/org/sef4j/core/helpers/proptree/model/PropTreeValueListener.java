package org.sef4j.core.helpers.proptree.model;

public interface PropTreeValueListener<T> {

	public void onChange(PropTreeNode node, String propName, T propValue);
	
}
