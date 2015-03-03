package com.google.code.joto.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * A useful utility class that will enumerate over an array of enumerations.
 * cf sun.misc.CompoundEnumeration ... 
 * ... should be in jdk or in apache collections!  (or Iterator replacement)
 */
public class CompoundEnumeration<E> implements Enumeration<E> {
	
	private Enumeration<E>[] enums;
	private int index = 0;

	// ------------------------------------------------------------------------
	
	public CompoundEnumeration(Enumeration<E>... enums) {
		this.enums = enums;
	}
	
	// ------------------------------------------------------------------------
	
	private boolean next() {
		while (index < enums.length) {
			if (enums[index] != null && enums[index].hasMoreElements()) {
				return true;
			}
			index++;
		}
		return false;
	}

	public boolean hasMoreElements() {
		return next();
	}

	public E nextElement() {
		if (!next()) {
			throw new NoSuchElementException();
		}
		return (E) enums[index].nextElement();
	}
	
}