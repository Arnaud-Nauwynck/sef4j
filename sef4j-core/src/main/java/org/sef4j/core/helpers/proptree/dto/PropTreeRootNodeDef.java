package org.sef4j.core.helpers.proptree.dto;

import java.io.Serializable;

public class PropTreeRootNodeDef implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;
	
	private final String treeName;

	public PropTreeRootNodeDef(String treeName) {
		this.treeName = treeName;
	}

	public String getTreeName() {
		return treeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((treeName == null) ? 0 : treeName.hashCode());
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
		PropTreeRootNodeDef other = (PropTreeRootNodeDef) obj;
		if (treeName == null) {
			if (other.treeName != null)
				return false;
		} else if (!treeName.equals(other.treeName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PropTreeRootNodeDef [treeName=" + treeName + "]";
	}
	
}
