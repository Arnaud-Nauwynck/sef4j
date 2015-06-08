package org.sef4j.core.util;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class CopyOnWriteUtils {

	// HashMap copy-on-write utilities
	// ------------------------------------------------------------------------
	
    public static <K,V> HashMap<K,V> newWithPut(Map<K,V> src, K key, V value) {
    	HashMap<K,V> res = new HashMap<K,V>(src.size() + 1);
    	res.putAll(src);
    	res.put(key, value);
    	return res;
    }

    public static <K,V> HashMap<K,V> newWithRemove(Map<K,V> src, K key) {
    	HashMap<K,V> res = new HashMap<K,V>(Math.max(0, src.size() - 1));
    	res.putAll(src);
    	res.remove(key);
    	return res;
    }

	// guava ImmutableMap copy-on-write utilities
	// ------------------------------------------------------------------------
	
    public static <K,V> ImmutableMap<K,V> newWithPut(ImmutableMap<K,V> src, K key, V value) {
    	return new ImmutableMap.Builder<K,V>()
    			.putAll(src).put(key, value).build();
    }

    public static <K,V> ImmutableMap<K,V> newWithRemove(ImmutableMap<K,V> src, K key) {
    	HashMap<K,V> res = new HashMap<K,V>(Math.max(0, src.size() - 1));
    	res.putAll(src);
    	res.remove(key);
    	return ImmutableMap.copyOf(res);
    }

	// native Array copy-on-write utilities  (... need extra Class "componentType" argument for handling empty array from generic class ctors!!)
	// ------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(Class<T> clss, int length) {
		return (T[]) Array.newInstance(clss, length);
	}

    public static <T> T[] newWithAdd(Class<T> clss, T[] array, T element) {
    	int len = array.length;
    	T[] res = newArray(clss, len + 1);
    	System.arraycopy(array, 0, res, 0, len);
    	res[len] = element;
    	return res;
    }

    public static <T> T[] newWithRemove(Class<T> clss, T[] array, T element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return array; // no need to clone... assume immutable
        }
        return newWithRemoveAt(clss ,array, index);
    }

    public static <T> T[] newWithRemoveAt(Class<T> clss, T[] array, int index) {
        int length = array.length;
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        T[] result = (T[]) newArray(clss, length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }
        return result;
    }

    public static <T> T[] newWithMerge(Class<T> clss, T[] left, T[] right) {
    	int len = left.length + right.length;
    	T[] res = newArray(clss, len);
    	System.arraycopy(left, 0, res, 0, left.length);
    	System.arraycopy(right, 0, res, left.length, right.length);
    	return res;
    }
    
    public static <T> int indexOf(T[] array, T objectToFind) {
        if (array == null) {
            return -1;
        }
        if (objectToFind == null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < array.length; i++) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

}
