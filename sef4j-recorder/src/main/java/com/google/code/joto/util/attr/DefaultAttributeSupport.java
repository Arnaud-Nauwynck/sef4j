package com.google.code.joto.util.attr;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class DefaultAttributeSupport implements IAttributeSupport {

	private Map<Object,Object> attributes;
	
	// ------------------------------------------------------------------------

	public DefaultAttributeSupport() {
	}

	// ------------------------------------------------------------------------

	public boolean containsAttr(Object key) {
		if (attributes == null) {
			return false;
		}
		return attributes.containsKey(key);
	}
	
	public Object getAttr(Object key) {
		return getAttr(key, null);
	}
	
	public Object getAttr(Object key, Object defaultIfNotFound) {
		Object res = null;
		if (attributes == null) {
			res = defaultIfNotFound;
		} else {
			res = attributes.get(key);
			if (res == null) {
				res = defaultIfNotFound;
			}
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAttrOrPutNewInstance(Object key, Class<T> classToInstanciante) {
		T res = null;
		if (attributes == null) {
			attributes = new HashMap<Object,Object>();
		}
		res = (T) attributes.get(key);
		if (res == null) {
			try {
				res = (T) classToInstanciante.newInstance();
			} catch(Exception ex) {
				throw new RuntimeException("failed to instanciate attribute for " + key + ", class to instanciate:" + classToInstanciante, ex);
			}
			attributes.put(key, res);
		}
		return res;
	}

	public Object putAttr(Object key, Object value) {
		if (attributes == null) {
			attributes = new HashMap<Object,Object>();
		}
		return attributes.put(key, value);
	}
	
	public Object removeAttr(Object key) {
		if (attributes == null) return null;
		Object res = attributes.remove(key);
		if (attributes.size() == 0) {
			attributes = null;
		}
		return res;
	}
	
}
