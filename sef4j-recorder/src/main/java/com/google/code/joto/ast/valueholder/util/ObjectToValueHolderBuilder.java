package com.google.code.joto.ast.valueholder.util;

import java.lang.ref.SoftReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.ast.valueholder.ValueHolderAST.AbstractObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.CollectionValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.FieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ImmutableObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.MapValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.ObjectValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveArrayValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.PrimitiveFieldValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefArrayValueHolder;
import com.google.code.joto.ast.valueholder.ValueHolderAST.RefFieldValueHolder;
import com.google.code.joto.reflect.ReflectUtils;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;


/**
 * Convert a "real" jvm Object, into a generic AST tree instance AbstractObjectValueHolder 
 * (a sort of Map<Map<Key,Value>> representation)
 *
 */
public class ObjectToValueHolderBuilder {
	
	private static Logger log = LoggerFactory.getLogger(ObjectToValueHolderBuilder.class);
	
	private Map<Object,AbstractObjectValueHolder> identityMap =
		new IdentityHashMap<Object,AbstractObjectValueHolder>();

	
	// -------------------------------------------------------------------------

	public ObjectToValueHolderBuilder() {
		super();
	}
	
	// -------------------------------------------------------------------------
	
	public Map<Object,AbstractObjectValueHolder> getResultMap() {
		return identityMap;
	}
	
	public AbstractObjectValueHolder buildValue(Object obj) {
		if (obj == null) {
			return null; // NullValueHolder.getInstance();
		} if (obj.getClass().isArray()) {
			Class<?> compType = obj.getClass().getComponentType();
			if (compType.isPrimitive()) {
				return casePrimitiveArray(obj);
			} else {
				return caseRefArray((Object[]) obj);
			}
		} if (obj instanceof Collection) {
			return caseCollection((Collection<?>) obj);
		} else if (obj instanceof Map) {
			return caseMap((Map<?,?>) obj);
		} else if (ReflectUtils.isPrimitiveWrapperType(obj.getClass())
				|| obj instanceof String) {
			return caseImmutableObject(obj);
		} else {
			return caseObject(obj);
		}
	}



	protected ObjectValueHolder caseObject(Object obj) {
		ObjectValueHolder res = (ObjectValueHolder) identityMap.get(obj);
		if (res == null) {
			res = new ObjectValueHolder(obj.getClass());
			identityMap.put(obj, res);

			caseObject(obj, res);
		}
		return res;
	}

	protected ImmutableObjectValueHolder caseImmutableObject(Object obj) {
		ImmutableObjectValueHolder res = (ImmutableObjectValueHolder) identityMap.get(obj);
		if (res == null) {
			res = new ImmutableObjectValueHolder(obj);
			identityMap.put(obj, res);
		}
		return res;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected PrimitiveArrayValueHolder casePrimitiveArray(Object obj) {
		PrimitiveArrayValueHolder res = (PrimitiveArrayValueHolder) identityMap.get(obj);
		if (res == null) {
			int len = Array.getLength(obj);
			res = new PrimitiveArrayValueHolder(obj.getClass(), len);
			identityMap.put(obj, res);

			casePrimitiveArray(obj, res);
		}
		return res;
	}

	protected RefArrayValueHolder caseRefArray(Object[] obj) {
		if (obj == null) {
			return null;
		}
		RefArrayValueHolder res = (RefArrayValueHolder) identityMap.get(obj);
		if (res == null) {
			res = new RefArrayValueHolder(obj.getClass(), obj.length);
			identityMap.put(obj, res);

			caseRefArray(obj, res);
		}
		return res;
	}


	protected CollectionValueHolder caseCollection(Collection<?> obj) {
		if (obj == null) {
			return null;
		}
		CollectionValueHolder res = (CollectionValueHolder) identityMap.get(obj);
		if (res == null) {
			res = new CollectionValueHolder(); // obj.getClass());
			identityMap.put(obj, res);

			caseCollection(obj, res);
		}
		return res;
	}

	protected MapValueHolder caseMap(Map<?,?> obj) {
		MapValueHolder res = (MapValueHolder) identityMap.get(obj);
		if (res == null) {
			res = new MapValueHolder();
			identityMap.put(obj, res);

			caseMap(obj, res);
		}
		return res;
	}

	// -------------------------------------------------------------------------
	
	protected void caseObject(final Object obj, final ObjectValueHolder node) {
		// BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
		// final Class<?> objClass = obj.getClass();
		
		if (obj instanceof SoftReference) {
			return;
		}
		if (obj instanceof StackTraceElement) {
			return;
		}
		if (obj instanceof ClassLoader) {
			return;
		}
		if (obj instanceof java.security.ProtectionDomain) {
			return;
		}
		String className = obj.getClass().getName();
		if (className.startsWith("org.apache.log4j.")) {
			return; //??
		} else if (className.startsWith("org.hibernate.") || className.startsWith("net.sf.hibernate.")) {
			return;
		} else if (className.startsWith("org.springframework.aop.") || className.startsWith("org.springframework.transaction.")) {
			return; //??
		}
		if (obj instanceof java.lang.reflect.InvocationHandler || obj instanceof java.lang.reflect.Proxy) {
			return;
		}
		if (className.startsWith("com.google.code.joto.eventrecorder.")) {
			return;
		}
		if (className.startsWith("GeneratedMethodAccessor")) {
			return;
		}
		if (className.indexOf("EnhancerByCGLIB") != -1) {
			return;
		}
				
		final ReflectionProvider reflectionProvider = ReflectUtils.getReflectionProvider();

		if (obj instanceof java.util.Date || obj instanceof java.sql.Date ) {
			Field timeField = reflectionProvider.getField(java.util.Date.class, "fastTime");
			Date dateObj = (Date) obj;
			long time = dateObj.getTime();
			FieldValueHolder fvh = node.getFieldValue(timeField);
			PrimitiveFieldValueHolder fvh2 = (PrimitiveFieldValueHolder) fvh;
			fvh2.setValue(time);
			return;
		} 
		
		
		reflectionProvider.visitSerializableFields(obj, new ReflectionProvider.Visitor() {
			@SuppressWarnings("rawtypes")
			@Override
			public void visit(String fieldName, 
					Class fieldType, 
					Class definedIn, Object value
					) {
				if (fieldName.equals("hashCode")) {
					return;
				}
				if (fieldName.equals("_objParent")) { 
					return;
				}
				String fieldClassName = fieldType.getClass().getName();
				if (fieldClassName.indexOf("ByCGLIB") != -1) {
					return;
				}
				
				Field field = reflectionProvider.getField(definedIn, fieldName);
				FieldValueHolder fvh = node.getFieldValue(field);
				if (fieldType.isPrimitive()) {
					PrimitiveFieldValueHolder fvh2 = (PrimitiveFieldValueHolder) fvh;
					fvh2.setValue(value);
				} else {
					// HACK??
					if (fieldClassName.startsWith("org.hibernate.") || fieldClassName.startsWith("net.sf.hibernate.")) {
						return;
					}
					
//					System.out.println("obj:" + obj.getClass() + "  field:" + fieldName + " (" + fieldType + ")");
					RefFieldValueHolder fvh2 = (RefFieldValueHolder) fvh;
					//.. recurse
					AbstractObjectValueHolder valueHolder = buildValue(value);
					fvh2.setTo(valueHolder);
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void casePrimitiveArray(final Object obj, 
			@SuppressWarnings("rawtypes") final PrimitiveArrayValueHolder valueHolder) {
		int len = Array.getLength(obj);
		Class<?> compType = obj.getClass().getComponentType();
		
		if (compType == Boolean.TYPE) {
			boolean[] array = (boolean[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Byte.TYPE) {
			byte[] array = (byte[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Character.TYPE) {
			char[] array = (char[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Short.TYPE) {
			short[] array = (short[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Integer.TYPE) {
			int[] array = (int[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Long.TYPE) {
			long[] array = (long[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Float.TYPE) {
			float[] array = (float[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else if (compType == Double.TYPE) {
			double[] array = (double[]) obj;
			for (int i = 0; i < len; i++) {
				valueHolder.setValueAt(i, array[i]);
			}
		} else {
			throw new RuntimeException("unrecognized primitive array " + compType);
		}
	}

	protected void caseRefArray(final Object[] obj, final RefArrayValueHolder valueHolder) {
		int len = obj.length;
		for (int i = 0; i < len; i++) {
			AbstractObjectValueHolder eltVH = buildValue(obj[i]);
			valueHolder.setValueAt(i, eltVH);
		}
	}

	protected void caseCollection(Collection<?> obj, CollectionValueHolder valueHolder) {
		String className = obj.getClass().getName();
		if (className.startsWith("org.hibernate.") || className.startsWith("net.sf.hibernate.")) {
			return; // NOT SUPPORTED YET (can have side-effect of lazy loading elts!!) 
		}
		try {
			for(Object objElt : obj) {
				try {
					AbstractObjectValueHolder eltVH = buildValue(objElt);
					valueHolder.addRefElt(eltVH);
				} catch(Exception ex) {
					log.error("Failed to build ValueHolder corresponding to collection elt ... ignore!", ex);
				}
			}
		} catch(Exception ex) {
			log.error("Failed to build ValueHolder corresponding to collection ... ignore!", ex);
		}
	}

	protected <K,T> void caseMap(Map<K,T> obj, MapValueHolder valueHolder) {
		try {
			for(Map.Entry<K,T> entry : obj.entrySet()) {
				try {
					AbstractObjectValueHolder keyVH = buildValue(entry.getKey());
					AbstractObjectValueHolder valueVH = buildValue(entry.getValue());
					valueHolder.putEntry(keyVH, valueVH);
				} catch(Exception ex) {
					log.error("Failed to build ValueHolder corresponding to Map entry ... ignore!", ex);
				}
			}
		} catch(Exception ex) {
			log.error("Failed to build ValueHolder corresponding to Map ... ignore!", ex);
		}
	}

}
