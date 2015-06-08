package org.sef4j.core.helpers.proptree.model;

import java.io.Serializable;

/**
 * immutable tuple [Path, propName]
 *
 */
public final class PropTreeValuePath implements Comparable<PropTreeValuePath>, Serializable {

	/** */
	private static final long serialVersionUID = 1L;
	
	private final Path path;
	private final String propName;
	
	// ------------------------------------------------------------------------
	
	public PropTreeValuePath(Path path, String propName) {
		this.path = path;
		this.propName = propName;
	}

	// ------------------------------------------------------------------------
	
	public Path getPath() {
		return path;
	}

	public String getPropName() {
		return propName;
	}

	// ------------------------------------------------------------------------
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = ((path == null) ? 0 : path.hashCode());
		result = prime * result + propName.hashCode();
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
		PropTreeValuePath other = (PropTreeValuePath) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path)) {
			return false;
		}
		if (!propName.equals(other.propName))
			return false;
		return true;
	}

	@Override
	public int compareTo(PropTreeValuePath other) {
		int res = 0;
		if (path == other.path) { //same or both null
			res = 0;
		} else if (path == null || other.path == null) {
			res = (path == null)? -1 : +1;
		} else {
			res = path.compareTo(other.path);
		}
		if (res == 0) {
			res = propName.compareTo(other.propName);
		}
		return res;
	}


	@Override
	public String toString() {
		return ((path != null)? path.toString() : "") + "/-/" + propName;
	}

}
