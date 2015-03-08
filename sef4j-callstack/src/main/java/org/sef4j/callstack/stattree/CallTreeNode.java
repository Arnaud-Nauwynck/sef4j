package org.sef4j.callstack.stattree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.sef4j.callstack.stats.PerfStats;

/**
 * 
 */
public class CallTreeNode {

	private static final LinkedHashMap<String,CallTreeNode> EMPTY_CHILD_MAP = new LinkedHashMap<String, CallTreeNode>();
	private static final HashMap<String,Object> EMPTY_PROP_MAP = new HashMap<String,Object>();

	private final CallTreeNode parent;
	private final String name;
	
	private static Object childLock = new Object();
	
	// copy-on-write => no lock for reading, lock on childLock for writing copy
	private LinkedHashMap<String,CallTreeNode> childMap = EMPTY_CHILD_MAP;
	
	private PerfStats stats = new PerfStats();
	
	private static Object propsLock = new Object();
	private HashMap<String,Object> propsMap = EMPTY_PROP_MAP;
	
	
	// ------------------------------------------------------------------------
	
	/**
	 * should be called only for ROOT element, or using CallTreeNode.getOrCreateChild(name)
	 */
	private CallTreeNode(CallTreeNode parent, String name) {
		this.parent = parent;
		this.name = name;
	}
	
	public static CallTreeNode newRoot() {
		return new CallTreeNode(null, "");
	}
	
	// ------------------------------------------------------------------------
	
	public CallTreeNode getParent() {
		return parent;
	}
	
	public String getName() {
		return name;
	}
	
	public LinkedHashMap<String, CallTreeNode> getChildMap() {
		return childMap;
	}

	public Collection<CallTreeNode> getChildList() {
	    return childMap.values();
	}

	public PerfStats getStats() {
		return stats;
	}
	
	public static void recursiveCopyToDTO(CallTreeNode src, CallTreeNodeDTO dest) {
		for(Map.Entry<String,CallTreeNode> e : src.childMap.entrySet()) {
			String childName = e.getKey();
			CallTreeNode srcChild = e.getValue();
			CallTreeNodeDTO destChild = dest.getOrCreateChild(childName);
			// *** recurse ***
			recursiveCopyToDTO(srcChild, destChild);
		}
		dest.setPropsMap(src.propsMap); // copy-on-write field => copy immutable pointer
		src.stats.getCopyTo(dest.getStats());
	}
	
	// ------------------------------------------------------------------------
	
	public int getDepth() {
		int res = 0;
		for(CallTreeNode curr = this; curr.parent != null; curr = curr.parent) {
			res++;
		}
		return res;
	}

	/**
	 * @return path names for element from root to self
	 */
	public String[] getPath() {
		int depth = getDepth();
		String[] res = new String[depth];
		int i = depth - 1;
		for(CallTreeNode curr = this; curr.parent != null; curr = curr.parent) {
			res[i--] = curr.name;
		}
		return res;
	}

	/**
	 * @return reverse path names for element from self to root
	 */
	public String[] getReversePath() {
		int depth = getDepth();
		String[] res = new String[depth];
		int i = 0;
		for(CallTreeNode curr = this; curr.parent != null; curr = curr.parent) {
			res[i++] = curr.name;
		}
		return res;
	}
	
	/**
	 * @param childName
	 * @return child with name "childName", newly created if did not exist before
	 */
	public CallTreeNode getOrCreateChild(String childName) {
		CallTreeNode res = childMap.get(childName);
		if (res == null) {
			synchronized(childLock) {
				res = childMap.get(childName);
				if (res != null) return res;
				res = new CallTreeNode(this, childName);
				LinkedHashMap<String, CallTreeNode> newChildMap = new LinkedHashMap<String, CallTreeNode>(childMap.size() + 1);
				newChildMap.putAll(childMap);
				newChildMap.put(childName, res);
				this.childMap = newChildMap;
			}
		}
		return res;
	}

	/**
	 * helper method for repeating getOrCreateChild() with path elements
	 * @param path
	 * @return sub-sub child for path names
	 */
	public CallTreeNode getOrCreateChildPath(String[] path) {
		CallTreeNode res = this;
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
			synchronized(propsLock) {
				res = (T) propsMap.get(propName);
				if (res != null) return res;
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
		}
		return res;
	}

	/** @return unmodifiable Map, and immutable (owner use copy-on-write, so new Map is created) */
	public Map<String,Object> getPropsMap() {
	    return Collections.unmodifiableMap(propsMap);
	}
	
	// ------------------------------------------------------------------------
	

	@Override
	public String toString() {
		return "CallTreeNode[" + name + "]";
	}
}
