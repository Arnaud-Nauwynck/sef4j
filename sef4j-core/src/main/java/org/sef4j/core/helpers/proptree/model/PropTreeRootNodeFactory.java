package org.sef4j.core.helpers.proptree.model;

import java.util.HashMap;
import java.util.Map;

import org.sef4j.core.helpers.proptree.dto.PropTreeRootNodeDef;
import org.sef4j.core.util.factorydef.AbstractSharedObjByDefFactory;
import org.sef4j.core.util.factorydef.DependencyObjectCreationContext;

/**
 * Factory implementation for registering PropTreeNode roots by name
 */
public class PropTreeRootNodeFactory extends AbstractSharedObjByDefFactory<PropTreeRootNodeDef,PropTreeNode> {

	public static final PropTreeRootNodeFactory INSTANCE = new PropTreeRootNodeFactory();
	
	private Map<String,PropTreeNode> rootNodes = new HashMap<String,PropTreeNode>();
	
	public PropTreeRootNodeFactory() {
		super("PropTreeRootNode", PropTreeRootNodeDef.class);
	}
	
	public void putRootNode(String treeName, PropTreeNode rootNode) {
		this.rootNodes.put(treeName, rootNode);
	}

	public PropTreeNode getOrCreateRootNode(String treeName) {
		PropTreeNode res = rootNodes.get(treeName);
		if (res == null) {
			res = PropTreeNode.newRoot();
			rootNodes.put(treeName, res);
		}
		return res;
	}

	@Override
	public PropTreeNode create(PropTreeRootNodeDef def, DependencyObjectCreationContext ctx) {
		return getOrCreateRootNode(def.getTreeName());
	}

}
