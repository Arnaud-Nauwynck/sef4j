package org.sef4j.jdbc.optional;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class InstrumenterHelper {

	private static final Logger LOG = LoggerFactory.getLogger(InstrumenterHelper.class);
	
	@SuppressWarnings("restriction")
	protected static final sun.misc.Unsafe UNSAFE = AccessController.doPrivileged(new PrivilegedAction<sun.misc.Unsafe>() {
		public sun.misc.Unsafe run() {
			try {
				java.lang.reflect.Field singleoneInstanceField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
				boolean prev = singleoneInstanceField.isAccessible();
				singleoneInstanceField.setAccessible(true);
				sun.misc.Unsafe ret =  (sun.misc.Unsafe)singleoneInstanceField.get(null);
				singleoneInstanceField.setAccessible(prev);
				return ret;
			} catch (Throwable e) {
				LOG.error("Could not instanciate sun.miscUnsafe. should use java.nio DirectByteBuffer ?",e);
				return null;
			}
		}
	});

	protected static void unsafeSetField(Object obj, String fieldName, Object value) {
		Class<?> clss = obj.getClass();
		unsafeSetField(obj, clss, fieldName, value);
	}
	
	protected static void unsafeSetField(Object obj, Class<?> clss, String fieldName, Object value) {
		Field field = getFieldOfRethrow(clss, fieldName);
//		int fieldOffset = UNSAFE.fieldOffset(field);
//		UNSAFE.putObject(obj, fieldOffset, value);

		boolean prevIsAccessible = field.isAccessible();
		try {
			field.setAccessible(true);
			field.set(obj, value);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException("Failed to set " + clss + "." + fieldName, ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException("Failed to set " + clss + "." + fieldName, ex);
		} finally {	
			field.setAccessible(prevIsAccessible);
		}
	}

    protected static <T> T unsafeGetField(Object obj, String fieldName) {
		Class<?> clss = obj.getClass();
		return unsafeGetField(obj, clss, fieldName);
	}
	
	@SuppressWarnings("unchecked")
    protected static <T> T unsafeGetField(Object obj, Class<?> clss, String fieldName) {
		Field field = getFieldOfRethrow(clss, fieldName);
//		int fieldOffset = UNSAFE.fieldOffset(field);
//		UNSAFE.putObject(obj, fieldOffset, value);

		boolean prevIsAccessible = field.isAccessible();
		try {
			field.setAccessible(true);
			return (T) field.get(obj);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException("Failed to get " + clss + "." + fieldName, ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException("Failed to get " + clss + "." + fieldName, ex);
		} finally {	
			field.setAccessible(prevIsAccessible);
		}
	}
	
	protected static Field getFieldOfRethrow(Class<?> clss, String fieldName) {
		Field field;
		try {
			field = clss.getDeclaredField(fieldName);
		} catch (NoSuchFieldException ex) {
			throw new RuntimeException("Failed to set " + clss + "." + fieldName, ex);
		} catch (SecurityException ex) {
			throw new RuntimeException("Failed to set " + clss + "." + fieldName, ex);
		}
		return field;
	}

}
