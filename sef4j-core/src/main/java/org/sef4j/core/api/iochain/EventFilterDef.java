package org.sef4j.core.api.iochain;

import java.io.Serializable;

public abstract class EventFilterDef implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	public abstract boolean equals(Object other);
	public abstract int hashCode();
	public abstract String toString();

}
