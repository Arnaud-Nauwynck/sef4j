package org.sef4j.core.utils;

/**
 * similar to java8 Cloneable interface ... with typed method "copy()" instead
 * of "Object.clone()"
 */
public interface ICopySupport<T> {

    public T copy();

}
