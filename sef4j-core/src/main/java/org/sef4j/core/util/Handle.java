package org.sef4j.core.util;

import java.io.Serializable;

/**
 * opaque object for a "server"-side handle allocated to a "client"
 */
public class Handle implements Serializable {
	
	/** */
	private static final long serialVersionUID = 1L;

	private int internalId;

	public Handle(int internalId) {
		this.internalId = internalId;
	}

	@Override
	public int hashCode() {
		return internalId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Handle other = (Handle) obj;
		if (internalId != other.internalId)
			return false;
		return true;
	}
	
}
