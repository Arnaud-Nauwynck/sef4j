package org.sef4j.core.api.proptree;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * DTO for PropTreeNode
 * 
 * (simpler structure without child->parent relation, and no read-write lock)
 * <p/> 
 * @see
 *  org.sef4j.callstack.stattree.CallTreeNode
 */
public final class PropTreeNodeDTO implements Serializable {

	/** for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	private final String name;
	
	private LinkedHashMap<String,PropTreeNodeDTO> childMap;

	private Map<String,Object> propsMap;

	// ------------------------------------------------------------------------
	
	/**
	 * should be called only for ROOT element, or using CallTreeNode.getOrCreateChild(name)
	 */
	private PropTreeNodeDTO(String name) {
		this.name = name;
	}
	
	public static PropTreeNodeDTO newRoot() {
		return new PropTreeNodeDTO("");
	}
	
	// ------------------------------------------------------------------------
		
	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public Map<String,PropTreeNodeDTO> getChildMap() {
		return (childMap != null)? childMap : (Map<String,PropTreeNodeDTO>)Collections.EMPTY_MAP;
	}
	
	/**
	 * @param childName
	 * @return child with name "childName", newly created if did not exist before
	 */
	public PropTreeNodeDTO getOrCreateChild(String childName) {
		PropTreeNodeDTO res = (childMap != null)? childMap.get(childName) : null;
		if (res == null) {
			res = new PropTreeNodeDTO(childName);
			LinkedHashMap<String, PropTreeNodeDTO> newChildMap = new LinkedHashMap<String, PropTreeNodeDTO>(childMap != null? childMap.size() + 1 : 1);
			if (childMap != null) {
				newChildMap.putAll(childMap);
			}
			newChildMap.put(childName, res);
			this.childMap = newChildMap;
		}
		return res;
	}

	/**
	 * helper method for repeating getOrCreateChild() with path elements
	 * @param path
	 * @return sub-sub child for path names
	 */
	public PropTreeNodeDTO getOrCreateChildPath(String[] path) {
		PropTreeNodeDTO res = this;
		final int len = path.length;
		for (int i = 0; i < len; i++) {
			res = res.getOrCreateChild(path[i]);
		}
		return res;
	}

	/**
	 * @param propName
	 * @return prop with name "propName", newly created if did not exist before
	 */
	@SuppressWarnings("unchecked")
	public <T> T getOrCreateProp(String propName, Callable<T> valueFactory) {
		T res = (T) propsMap.get(propName);
		if (res == null) {
			try {
				res = valueFactory.call();
			} catch (Exception ex) {
				throw new RuntimeException("Failed to create prop value", ex);
			}
			HashMap<String, Object> newPropsMap = new HashMap<String, Object>(propsMap.size() + 1);
			newPropsMap.putAll(propsMap);
			newPropsMap.put(propName, res);
			this.propsMap = newPropsMap;
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public <T> T getProp(String propName) {
		return (T) propsMap.get(propName);
	}

	@SuppressWarnings("unchecked")
	public <T> T putProp(String propName, T propValue) {
		return (T) propsMap.put(propName, propValue);
	}

	public Map<String, Object> getPropsMap() {
		return propsMap;
	}

	public void setPropsMap(Map<String, Object> p) {
		this.propsMap = p;
	}
	
	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "PropTreeNodeDTO[" + name + "]";
	}

}
