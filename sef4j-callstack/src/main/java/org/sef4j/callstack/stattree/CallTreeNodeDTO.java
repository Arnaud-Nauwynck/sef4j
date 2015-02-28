package org.sef4j.callstack.stattree;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.sef4j.callstack.stats.PerfStats;

/**
 * DTO for CallTreeNode
 * (simpler structure without child->parent relation, and no read-write lock) 
 */
public final class CallTreeNodeDTO implements Serializable {

	/** for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	private final String name;
	
	private LinkedHashMap<String,CallTreeNodeDTO> childMap;

	private PerfStats stats = new PerfStats();

	private Map<String,Object> propsMap;

	// ------------------------------------------------------------------------
	
	/**
	 * should be called only for ROOT element, or using CallTreeNode.getOrCreateChild(name)
	 */
	private CallTreeNodeDTO(String name) {
		this.name = name;
	}
	
	public static CallTreeNodeDTO newRoot() {
		return new CallTreeNodeDTO("");
	}
	
	// ------------------------------------------------------------------------
		
	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	public Map<String,CallTreeNodeDTO> getChildMap() {
		return (childMap != null)? childMap : (Map<String,CallTreeNodeDTO>)Collections.EMPTY_MAP;
	}
	
	/**
	 * @param childName
	 * @return child with name "childName", newly created if did not exist before
	 */
	public CallTreeNodeDTO getOrCreateChild(String childName) {
		CallTreeNodeDTO res = childMap.get(childName);
		if (res == null) {
			res = new CallTreeNodeDTO(childName);
			LinkedHashMap<String, CallTreeNodeDTO> newChildMap = new LinkedHashMap<String, CallTreeNodeDTO>(childMap.size() + 1);
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
	public CallTreeNodeDTO getOrCreateChildPath(String[] path) {
		CallTreeNodeDTO res = this;
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
	
	public PerfStats getStats() {
		return stats;
	}
	
	public void setStats(PerfStats stats) {
		this.stats = stats;
	}
	
	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "CallTreeNode[" + name + "]";
	}

}
