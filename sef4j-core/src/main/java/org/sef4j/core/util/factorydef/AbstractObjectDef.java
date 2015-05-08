package org.sef4j.core.util.factorydef;

import java.io.Serializable;

/**
 * marker top-level class for describing class that are "object definition"
 * sub-classes must be value-object (immutable, serializable DTO, comparable)
 * 
 * it must be safe to use this class and any sub-class as a key in Map<K,V>
 * 
 */
public abstract class AbstractObjectDef implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	public abstract boolean equals(Object other);
	public abstract int hashCode();
	public abstract String toString();

}
