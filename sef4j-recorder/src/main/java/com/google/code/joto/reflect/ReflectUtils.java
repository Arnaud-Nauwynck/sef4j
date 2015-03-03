package com.google.code.joto.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.ImmutableFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.JVM;

public class ReflectUtils {

	private static JVM jvm = new JVM();
	private static ReflectionProvider reflectionProvider = jvm.bestReflectionProvider();
	private static FieldDictionary fieldDictionary = new FieldDictionary(new ImmutableFieldKeySorter());

	
	// -------------------------------------------------------------------------
	
	/** private to force all static */
	private ReflectUtils() {
	}

	// -------------------------------------------------------------------------
	
	public static ReflectionProvider getReflectionProvider() {
		return reflectionProvider;
	}
	
	public static FieldDictionary getFieldDictionary() {
		return fieldDictionary;
	}

	public static String fieldToCapitalizedName(Field field) {
		return fieldToCapitalizedName(field.getName());
	}
		
	public static String fieldToCapitalizedName(String fieldName) {
		String res = fieldName;
		if (res.startsWith("_")) {
			// detect "_field" naming convention
			res = res.substring(1);
		}
		int len = res.length();
		if (len == 1) {
			res = "" + Character.toUpperCase(res.charAt(0));
		} else {
			char ch0 = res.charAt(0);
			if (len > 2 && (ch0 == 'm' || ch0 == 'f')
				&& Character.isUpperCase(res.charAt(1))) {
				// detect "mField" and "fField" naming conventions...
				res = res.substring(1);
			}
			
			// ok.. now transform "field" into "Field"
			res = Character.toUpperCase(res.charAt(0)) + ((res.length() > 1)? res.substring(1, res.length()) : "");
		}
		
		if (res.endsWith("_")) {
			res = res.substring(0, res.length() - 1);
		}
		return res;
	}
	
	/**
	 * 
	 * @param methodPrefix
	 * @param field
	 * @param methodSuffix
	 * @param parameterTypes
	 * @return
	 */
	public static Method findFieldMethod(
			String methodPrefix, Field field, String methodSuffix, 
			Class <?>... parameterTypes) {
		String methodName = ((methodPrefix != null)? methodPrefix : "")
			+ fieldToCapitalizedName(field)
			+ ((methodSuffix != null)? methodSuffix : "");

		Method res = findMethod(field.getDeclaringClass(), methodName, parameterTypes);
		return res;
	}

	
	public static Method findMethod(Class<?> clss,
			String methodName, Class<?>... parameterTypes) {
		try {
			return clss.getMethod(methodName, parameterTypes);
		} catch(NoSuchMethodException ex) {
			return null;
		} catch(Exception ex) {
			throw new RuntimeException("Failed to find method", ex);
		}
	}

	public static List<Method> findMethodsByName(Class<?> clss, String methodName, boolean onlyPublic) {
		List<Method> res = new ArrayList<Method>();
		for (Method meth : clss.getMethods()) {
			if (meth.getName().equals(methodName)
					&& (!onlyPublic || Modifier.isPublic(meth.getModifiers()))
					) {
				res.add(meth);
			}
		}
		return res; 
	}

	
//	/**
//	 * TOCHECK ... see Class.getMethod(name, parameterTypes) ???
//	 * 
//	 */
//	public static Method findMethod(Class clssOrSuperClasses, String methodName, Class<?>... argTypes) {
//		Method res = null;
//		for(Class<?> clss = clssOrSuperClasses; !Object.class.equals(clss);  clss = clss.getSuperclass()) {
//        	Method tmp = findDeclaredMethod(clss, methodName, argTypes);
//        	if (tmp != null) {
//        		res = tmp;
//        		break;
//        	}
//        }
//		return res;
//	}
//	
//	public static Method findDeclaredMethod(Class declaredClss, String methodName, Class<?>... argTypes) {
//		...
//	}

	public static Method methodIfPublic(Method meth) {
		if (meth == null) return null;
		Method res = null;
		int modifiers = meth.getModifiers();
		if (meth.isAccessible()
			&& Modifier.isPublic(modifiers)) {
			res = meth; 
		}
		return res;
	}

	public static List<Field> findAssignableFieldsForValueType(Class<?> targetClass, Class<?> valueType) {
		List<Field> res = new ArrayList<Field>();
		for (Iterator<Field> iterator = fieldsFor(targetClass); iterator.hasNext();) {
            Field field = iterator.next();
            Class<?> fieldClass = field.getDeclaringClass();
            if (fieldClass.isAssignableFrom(valueType)) {
            	res.add(field);
            }
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public static Iterator<Field> fieldsFor(Class<?> targetClass) {
		return fieldDictionary.fieldsFor(targetClass);
	}

	public static Class<?> primitiveTypeToWrapperType(Class<?> t) {
	     if (t == java.lang.Boolean.TYPE) return java.lang.Boolean.class; 
	     else if (t == java.lang.Character.TYPE) return java.lang.Character.class;
	     else if (t == java.lang.Byte.TYPE) return java.lang.Byte.class;
	     else if (t == java.lang.Short.TYPE) return java.lang.Short.class;
	     else if (t == java.lang.Integer.TYPE) return java.lang.Integer.class;
	     else if (t == java.lang.Long.TYPE) return java.lang.Long.class;
	     else if (t == java.lang.Float.TYPE) return java.lang.Float.class;
	     else if (t == java.lang.Double.TYPE) return java.lang.Double.class;
	     else if (t == java.lang.Void.TYPE) return java.lang.Void.class;
	     else return null;
	}

	public static boolean isPrimitiveWrapperType(Class<?> t) {
		return t.getName().startsWith("java.lang.")
			&& wrapperTypeToPrimitive(t) != null;
	}

	public static Class<?> wrapperTypeToPrimitive(Class<?> t) {
	     if (t == java.lang.Boolean.class) return java.lang.Boolean.TYPE; 
	     else if (t == java.lang.Character.class) return java.lang.Character.TYPE;
	     else if (t == java.lang.Byte.class) return java.lang.Byte.TYPE;
	     else if (t == java.lang.Short.class) return java.lang.Short.TYPE;
	     else if (t == java.lang.Integer.class) return java.lang.Integer.TYPE;
	     else if (t == java.lang.Long.class) return java.lang.Long.TYPE;
	     else if (t == java.lang.Float.class) return java.lang.Float.TYPE;
	     else if (t == java.lang.Double.class) return java.lang.Double.TYPE;
	     else if (t == java.lang.Void.class) return java.lang.Void.TYPE;
	     else return null;
	}

	
}
