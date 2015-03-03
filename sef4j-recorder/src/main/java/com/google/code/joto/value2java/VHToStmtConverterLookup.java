package com.google.code.joto.value2java;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.code.joto.util.PriorityList;
import com.google.code.joto.value2java.converters.VHToStmtConverterLookupUtils;
import com.thoughtworks.xstream.converters.ConversionException;


/**
 * helper class for configuring custom Converter per types, on looking up them
 * 
 */
public class VHToStmtConverterLookup {

	/**
	 * ordered list of user-defined converters
	 */
	private PriorityList<ObjectVHToStmtConverter> objConverters = 
		new PriorityList<ObjectVHToStmtConverter>();

	/**
	 * ordered list of link object->object converters
	 */
	private PriorityList<RefObjectVHToStmtConverter> refObjConverters =
		new PriorityList<RefObjectVHToStmtConverter>();

	
	/** lazy computed field, from objConverters */
	private transient Map<Class<?>,ObjectVHToStmtConverter> cachedTypeToConverter = 
		new HashMap<Class<?>,ObjectVHToStmtConverter>(); 
	
	/** lazy computed field, from objConverters */
	private transient Map<RefClassToClassKey,RefObjectVHToStmtConverter> cachedRefKeyToRefObjConverter = 
		new HashMap<RefClassToClassKey,RefObjectVHToStmtConverter>(); 

	
	//-------------------------------------------------------------------------

	public VHToStmtConverterLookup(boolean registerDefaults) {
		if (registerDefaults) {
			VHToStmtConverterLookupUtils.registerDefaultConverters(this);
		}
	}

	//-------------------------------------------------------------------------
	
	public void registerConverter(ObjectVHToStmtConverter p, int priority) { 
		objConverters.add(p, priority);
		if (cachedTypeToConverter != null) {
			cachedTypeToConverter.clear(); // clear corresponding cache results
		}
	}

	public void registerLinkConverter(RefObjectVHToStmtConverter p, int priority) { 
		refObjConverters.add(p, priority);
		if (cachedRefKeyToRefObjConverter != null) {
			cachedRefKeyToRefObjConverter.clear(); // clear corresponding cache results
		}
	}

	
	public ObjectVHToStmtConverter lookupConverter(Class<?> type) {
		ObjectVHToStmtConverter res = cachedTypeToConverter.get(type);
        if (res == null) {
	        for(ObjectVHToStmtConverter elt : objConverters) {
	            if (elt.canConvert(type)) {
	            	cachedTypeToConverter.put(type, elt);
	                res = elt;
	                break;
	            }
	        }
	        if (res == null) {
	        	res = null; // throw new ConversionException("No converter specified for " + type);
	        	// => TODO standard Bean converter ... 
	        }
        }
        return res;
	}
	
	
	public RefObjectVHToStmtConverter lookupConverter(Class<?> from, String[] path, Class<?> to) {
		RefClassToClassKey key = new RefClassToClassKey(from, path, to);
		RefObjectVHToStmtConverter res = cachedRefKeyToRefObjConverter.get(key);
        if (res == null) {
	        for(RefObjectVHToStmtConverter elt : refObjConverters) {
	            if (elt.canConvert(from, path, to)) {
	            	cachedRefKeyToRefObjConverter.put(key, elt);
	                res = elt;
	                break;
	            }
	        }
	        if (res == null) {
	        	throw new ConversionException("No converter specified for link " + from + " -" + path + "->" + to);
	        }
        }
        return res;
	}
	
	// -------------------------------------------------------------------------

	/**
	 * internal
	 */
	private static class RefClassToClassKey {
		private final Class<?> fromClass;
		private final String[] path;
		private final Class<?> toClass;
		
		public RefClassToClassKey(Class<?> fromClass, String[] path, Class<?> toClass) {
			super();
			this.fromClass = fromClass;
			this.path = new String[path.length];
			System.arraycopy(path, 0, this.path, 0, path.length);
			this.toClass = toClass;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((fromClass == null) ? 0 : fromClass.hashCode());
			result = prime * result + Arrays.hashCode(path);
			result = prime * result
					+ ((toClass == null) ? 0 : toClass.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RefClassToClassKey other = (RefClassToClassKey) obj;
			if (fromClass == null) {
				if (other.fromClass != null)
					return false;
			} else if (!fromClass.equals(other.fromClass))
				return false;
			if (!Arrays.equals(path, other.path))
				return false;
			if (toClass == null) {
				if (other.toClass != null)
					return false;
			} else if (!toClass.equals(other.toClass))
				return false;
			return true;
		}

		
		
	}

}
