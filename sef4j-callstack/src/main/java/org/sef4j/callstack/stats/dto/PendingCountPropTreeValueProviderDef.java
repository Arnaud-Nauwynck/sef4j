package org.sef4j.callstack.stats.dto;

import java.io.Serializable;

import org.sef4j.core.helpers.export.ExportFragmentsProviderDef;
import org.sef4j.core.helpers.proptree.dto.PropTreeRootNodeDef;

public class PendingCountPropTreeValueProviderDef extends ExportFragmentsProviderDef implements Serializable {
	
	/** */
	private static final long serialVersionUID = 1L;
	
	private final PropTreeRootNodeDef rootNodeDef;

	public PendingCountPropTreeValueProviderDef(PropTreeRootNodeDef rootNodeDef) {
		this.rootNodeDef = rootNodeDef;
	}
	
	public PropTreeRootNodeDef getRootNodeDef() {
		return rootNodeDef;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rootNodeDef == null) ? 0 : rootNodeDef.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PendingCountPropTreeValueProviderDef other = (PendingCountPropTreeValueProviderDef) obj;
		if (rootNodeDef == null) {
			if (other.rootNodeDef != null)
				return false;
		} else if (!rootNodeDef.equals(other.rootNodeDef))
			return false;
		return true;
	}
	
	
}