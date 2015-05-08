package org.sef4j.core.helpers.tasks;

import java.io.Serializable;

public abstract class TaskDef implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	public abstract boolean equals(Object other);
	public abstract int hashCode();
	public abstract String toString();


}
