package org.sef4j.core.helpers.proptree.model;

import java.util.concurrent.Callable;

import org.sef4j.core.util.CopyOnWriteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

/**
 * PropTreeNode are nodes of a Tree, containing property name-values
 * <p/>
 * They are simply used to represent property (metrics, counters...) per node-path (per CallStack path, per instrumented node...)
 * <p/>
 *  
 * They are actually independant of CallStack and CallStackElt
 * When using with corresponding method name, they can be interpreted as metrics node values for   
 * a stack path, from root to any method "meth1/meth2/.../methN"
 * <p/>
 * 
 * PropTreeNode are lazily created on first access, using thread-safe <code>getOrCreateChild(childName)</code> 
 * <BR/> (atomic and thread safe, preserving uniqueness per childname)
 * <p/>
 * 
 * PropTreeNode can attach any free name-value property, using thread safe <code>getOrCreateProp(name, propFactory</code> 
 *  <BR/> (atomic and thread safe, preserving uniqueness per property name)
 * 
 * 
 * To create a dump snapshot of a whole Tree, see <code>recursiveCopyToDTO()</code> corresponding to DTO class 
 * 
 * <PRE>
 * 
 * "/"
 *  +---------------+
 *  | Root TreeNode |
 *  +---------------+
 *    |
 *    +--
 *    +--
 *    
 * 
 * "meth1/meth2/.../methN"
 *        +-------------------------------+
 *        | TreeNode                      |
 *        |  - parent                     | 
 *        |  - childName                  |
 *        |  - props  :  { name1 : prop1  |
 *        |                name2 : prop2  |
 *        |                 ..            |
 *        |              }                |
 *        | - childMap : { child1:.. }    |
 *        +-------------------------------+
 *          |
 *          |
 * "meth1/meth2/.../methN/child1"
 *          |    +-----------------+
 *          +--  | Child1 TreeNode |
 *          |    +-----------------+
 *          |         |
 *          |         +--
 *          |         +--
 *          |           |
 *          |           +--
 *          |     
 * "meth1/meth2/.../methN/child2"
 *          |    +-----------------+
 *          +--  | Child2 TreeNode |
 *          |    +-----------------+
 *          |         |
 *          |         +--
 *          |         +--
 *          |           |
 *          |           +--
 *          | 
 *          ..
 *          
 *          |     
 * "meth1/meth2/.../methN/childP"
 *          |    +-----------------+
 *          +--  | ChildP TreeNode |
 *               +-----------------+
 *                    |
 *                    +--
 *                    +--
 *                      |
 *                      +--
 *
 * </PRE>
 */
public class PropTreeNode {

	private static final Logger LOG = LoggerFactory.getLogger(PropTreeNode.class);
	
	private static final boolean DEBUG_NEW_NODE = true;
	private static final boolean DEBUG_NEW_PROP = false;
	private static int nodeIdGenerator = 1;
	private final int nodeId = nodeIdGenerator++;
	
	
	private final PropTreeNode parent;
	private final String name;
	
	private static Object childLock = new Object();
	
	// copy-on-write => no lock for reading, lock on childLock for writing copy
	private ImmutableMap<String,PropTreeNode> childMap = ImmutableMap.of();

	private static Object propsLock = new Object();
	private ImmutableMap<String,Object> propsMap = ImmutableMap.of();
	
//	private ImmutableMap<String,PropTreeValueListener<?>[]> inheritablePropListeners = ImmutableMap.of();
//	private ImmutableMap<String,PropTreeValueListener<?>[]> inheritedPropListeners = ImmutableMap.of();
//	
//	private static final PropTreeValueListener<?>[] EMPTY_LISTENERS_ARRAY = new PropTreeValueListener[0];
	
	// ------------------------------------------------------------------------
	
	/**
	 * should be called only for ROOT element, or using CallTreeNode.getOrCreateChild(name)
	 */
	private PropTreeNode(PropTreeNode parent, String name) {
		this.parent = parent;
		this.name = name;
	}
	
	public static PropTreeNode newRoot() {
		return new PropTreeNode(null, "");
	}
	
	// ------------------------------------------------------------------------
	
	public PropTreeNode getParent() {
		return parent;
	}
	
	public String getName() {
		return name;
	}
	
	public ImmutableMap<String, PropTreeNode> getChildMap() {
		return childMap;
	}

	public ImmutableCollection<PropTreeNode> getChildList() {
	    return childMap.values();
	}
	
	// ------------------------------------------------------------------------
	
//	public void putInheritablePropListener(String propName, PropTreeValueListener<?> listener) {
//		synchronized(childLock) {
//			PropTreeValueListener<?>[] listeners = inheritablePropListeners.get(propName);
//			if (listeners == null) listeners = EMPTY_LISTENERS_ARRAY;
//			PropTreeValueListener<?>[] newListeners = CopyOnWriteUtils.newWithAdd(PropTreeValueListener.class, listeners, listener);
//			this.inheritablePropListeners = CopyOnWriteUtils.newWithPut(inheritablePropListeners, propName, newListeners);
//		
//			// recursive recompute inherited from parent+inheritable
//			doRecursiveRecomputeInheritedPropListeners(propName);
//		}
//	}
//
//	public void removeInheritablePropListener(String propName, PropTreeValueListener<?> listener) {
//		synchronized(childLock) {
//			PropTreeValueListener<?>[] listeners = inheritablePropListeners.get(propName);
//			if (listeners == null) listeners = EMPTY_LISTENERS_ARRAY;
//			PropTreeValueListener<?>[] newListeners = CopyOnWriteUtils.newWithRemove(PropTreeValueListener.class, listeners, listener);
//			this.inheritablePropListeners = CopyOnWriteUtils.newWithPut(inheritablePropListeners, propName, newListeners);
//		
//			// recursive recompute inherited from parent+inheritable
//			doRecursiveRecomputeInheritedPropListeners(propName);
//		}
//	}
//
//	private void doRecursiveRecomputeInheritedPropListeners(String propName) {
//		PropTreeValueListener<?>[] mergeListeners;
//		PropTreeValueListener<?>[] parentPropListeners = getParent() != null? 
//				getParent().inheritedPropListeners.get(propName) : null;
//		if (! inheritablePropListeners.isEmpty()) {
//			PropTreeValueListener<?>[] propListeners = inheritablePropListeners.get(propName);
//			mergeListeners = CopyOnWriteUtils.newWithMerge(PropTreeValueListener.class,
//						parentPropListeners!=null? parentPropListeners : EMPTY_LISTENERS_ARRAY,
//						propListeners!=null? propListeners : EMPTY_LISTENERS_ARRAY);
//		} else {
//			mergeListeners = parentPropListeners;
//		}
//		this.inheritedPropListeners = CopyOnWriteUtils.newWithPut(inheritedPropListeners, propName, mergeListeners);
//		// *** recurse ***
//		for(PropTreeNode child : childMap.values()) {
//			synchronized(child.childLock) {
//				child.doRecursiveRecomputeInheritedPropListeners(propName);
//			}
//		}
//	}

	// ------------------------------------------------------------------------
	
	public int getDepth() {
		int res = 0;
		for(PropTreeNode curr = this; curr.parent != null; curr = curr.parent) {
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
		for(PropTreeNode curr = this; curr.parent != null; curr = curr.parent) {
			res[i--] = curr.name;
		}
		return res;
	}

	/**
     * @return path for element from root to self
     */
    public String getPathStr() {
        String[] tmpres = getPath();
        StringBuilder sb = new StringBuilder();
        final int len = tmpres.length;
        for (int i = 0; i < len; i++) {
            sb.append(tmpres[i]);
            if (i + 1 < len) {
                sb.append("/");
            }
        }
        return sb.toString();
    }
    
	/**
	 * @return reverse path names for element from self to root
	 */
	public String[] getReversePath() {
		int depth = getDepth();
		String[] res = new String[depth];
		int i = 0;
		for(PropTreeNode curr = this; curr.parent != null; curr = curr.parent) {
			res[i++] = curr.name;
		}
		return res;
	}
	
	/**
	 * @param childName
	 * @return child with name "childName", newly created if did not exist before
	 */
	public PropTreeNode getOrCreateChild(String childName) {
		PropTreeNode res = childMap.get(childName);
		if (res == null) {
			synchronized(childLock) {
				res = childMap.get(childName);
				if (res != null) return res;
				res = new PropTreeNode(this, childName);
				this.childMap = CopyOnWriteUtils.newWithPut(childMap, childName, res);
				
				if (DEBUG_NEW_NODE) {
					LOG.info("created node: " + childName + " (" + res.nodeId + ") on parent (" + nodeId + ")");
				}
			}
		}
		return res;
	}

	public PropTreeNode getChildOrNull(String childName) {
		return childMap.get(childName);
	}
	
	/**
	 * helper method for repeating getOrCreateChild() with path elements
	 * @param path
	 * @return sub-sub child for path names
	 */
	public PropTreeNode getOrCreateChildPath(String[] path) {
		PropTreeNode res = this;
		final int len = path.length;
		for (int i = 0; i < len; i++) {
			res = res.getOrCreateChild(path[i]);
		}
		return res;
	}

	public PropTreeNode getChildByPathOrNull(String[] path) {
		PropTreeNode res = this;
		final int len = path.length;
		for (int i = 0; i < len; i++) {
			res = res.getChildOrNull(path[i]);
			if (res == null) {
				return null;
			}
		}
		return res;
	}

	public <T> void updateOrCreateProp(String propName, Callable<T> valueFactory, PropTreeValueCallback<T> callback) {
		T propValue = getOrCreateProp(propName, valueFactory);
		callback.doWith(this, propName, propValue);		
	}

	public <T> void updateProp(String propName, Class<T> clss, PropTreeValueCallback<T> callback) {
		T propValue = getPropOrNull(propName, clss);
		callback.doWith(this, propName, propValue);		
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
				this.propsMap = CopyOnWriteUtils.newWithPut(propsMap, propName, res);

				if (DEBUG_NEW_PROP) {
					LOG.info("created Prop " + propName + " on node: " + name + " (" + nodeId + ")");
				}
			
			}
		}
		return res;
	}

	/** @return immutable Map */
	public ImmutableMap<String,Object> getPropsMap() {
	    return propsMap;
	}

	public Object getPropOrNull(String propName) {
	    return propsMap.get(propName);
	}

	@SuppressWarnings("unchecked")
	public <T> T getPropOrNull(String propName, Class<T> clss) {
	    return (T) getPropOrNull(propName);
	}

	// ------------------------------------------------------------------------
	

	@Override
	public String toString() {
		return "CallTreeNode[" + name + "]";
	}

}
