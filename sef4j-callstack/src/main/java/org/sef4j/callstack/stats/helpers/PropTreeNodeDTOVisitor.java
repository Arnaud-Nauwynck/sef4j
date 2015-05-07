package org.sef4j.callstack.stats.helpers;

import java.util.Map;

import org.sef4j.core.helpers.proptree.dto.PropTreeNodeDTO;

public abstract class PropTreeNodeDTOVisitor {

	protected int currDepth;
	
	public void visitChild(PropTreeNodeDTO node, int remainDepth) {
		onStartVisitNode(node);
		currDepth++;
		
		visitProps(node);
		
		if (remainDepth == -1 || remainDepth > 0) {
			int childMaxDepth = (remainDepth == -1)? -1 : remainDepth-1;
			for(Map.Entry<String,PropTreeNodeDTO> e : node.getChildMap().entrySet()) {
				String childName = e.getKey();
				if (childName == null) {
				    continue; //should not occur!!
				}
				PropTreeNodeDTO child = e.getValue();
				// *** recurse ***
				visitChild(child, childMaxDepth);
			}
		}
		currDepth--;
		onEndVisitNode(node);
	}

	public void visitProps(PropTreeNodeDTO node) {
		currDepth++;
		Map<String, Object> propsMap = node.getPropsMap();
		if (propsMap != null) {
			for(Map.Entry<String, Object> e : propsMap.entrySet()) {
				visitProp(node, e.getKey(), e.getValue());
			}
		}
		currDepth--;
	}

	public void onStartVisitNode(PropTreeNodeDTO node) {
	}

	public void onEndVisitNode(PropTreeNodeDTO node) {
	}

	public void visitProp(PropTreeNodeDTO node, String key, Object value) {
	}
	
}
