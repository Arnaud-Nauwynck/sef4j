package org.sef4j.core.utils.adapters;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 
 */
public class TypeHierarchyToObjectMap<T> {

    private Map<Class<?>, T> overrideForClassMap = new HashMap<Class<?>, T>();

//    private Map<Class<?>,T> overrideForInterfaceMap = new HashMap<Class<?>,T>();

    private WeakHashMap<Class<?>, T> cachedPerClassMap = new WeakHashMap<Class<?>, T>();

    // ------------------------------------------------------------------------

    public TypeHierarchyToObjectMap() {
    }

    // ------------------------------------------------------------------------

    public void putOverride(Class<?> key, T value) {
	overrideForClassMap.put(key, value);
	cachedPerClassMap.clear();
    }

    public T getOverride(Class<?> key) {
	return overrideForClassMap.get(key);
    }

    public void removeOverride(Class<?> key) {
	overrideForClassMap.remove(key);
	cachedPerClassMap.clear();
    }

    // ------------------------------------------------------------------------

    public T get(Class<?> key) {
	T res = cachedPerClassMap.get(key);
	if (res == null) {
	    if (key.isInterface()) {
		res = doGetPerInterfaceOrSuperInterface(key);
	    } else {
		res = doGetPerClassOrSuperClass(key);
	    }
	    cachedPerClassMap.put(key, res);
	}
	return res;
    }

    // ------------------------------------------------------------------------

    private T doGetPerClassOrSuperClass(Class<?> clss) {
	T res = null;
	Class<?> currClss = clss;
	for (;; currClss = currClss.getSuperclass()) {
	    if (currClss == null) {
		break; // superclass of Object, or interface
	    }
	    res = overrideForClassMap.get(currClss);
	    if (res != null) {
		return res;
	    }
	    Class<?>[] interfaces = currClss.getInterfaces();
	    if (interfaces.length != 0) {
		for (int i = 0; i < interfaces.length; i++) {
		    res = overrideForClassMap.get(interfaces[i]);
		    if (res != null) {
			return res;
		    }
		    // cf next to find for super interfaces! (after super class scanning)
		}
	    }
	}
	// second lookup, per super class -> super interfaces
	currClss = clss;
	for (;; currClss = currClss.getSuperclass()) {
	    if (currClss == null) {
		break; // superclass of Object, or interface
	    }
	    Class<?>[] interfaces = currClss.getInterfaces();
	    if (interfaces.length != 0) {
		for (int i = 0; i < interfaces.length; i++) {
		    res = get(interfaces[i]); // => recurse in doGetPerInterfaceOrSuperInterface()
		    if (res != null) {
			return res;
		    }
		    // cf next to find for super interfaces! (after super class scanning)
		}
	    }
	}
	return null;
    }

    private T doGetPerInterfaceOrSuperInterface(Class<?> clss) {
	T res = overrideForClassMap.get(clss);
	if (res != null) {
	    return res;
	}
	Class<?>[] interfaces = clss.getInterfaces();
	if (interfaces.length != 0) {
	    for (int i = 0; i < interfaces.length; i++) {
		res = get(interfaces[i]); // => cache + recurse in doGetPerInterfaceOrSuperInterface()
		if (res != null) {
		    return res;
		}
	    }
	}
	return null;
    }

}
