package org.sef4j.core.api.iochain;

import java.io.Serializable;

/**
 * abstract top-level hierarchy class for describing EventSource channels
 * sub-classes must be value-object (immutable, serializable DTO, comparable)
 * 
 * it should be safe to use EventSourceDef as key in Map<K,V>
 * 
 * see corresponding EventSource and EventSourceFactory classes
 */
public abstract class InputEventChainDef implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;
	
	public abstract boolean equals(Object other);
	public abstract int hashCode();
	public abstract String toString();
	
}
