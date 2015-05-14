package org.sef4j.core.helpers.proptree.changes;

import org.sef4j.core.api.def.ioevenchain.InputEventChainDef;

public class PropTreePathInputEventChainDef extends InputEventChainDef {
	
	/** */
	private static final long serialVersionUID = 1L;
	
	private final String path;
	private final String propName;
	private final String markAndCompareAccessorName;
	
	public PropTreePathInputEventChainDef(String path, String propName,
			String markAndCompareAccessorName) {
		this.path = path;
		this.propName = propName;
		this.markAndCompareAccessorName = markAndCompareAccessorName;
	}
	
	public String getPath() {
		return path;
	}
	public String getPropName() {
		return propName;
	}
	public String getMarkAndCompareAccessorName() {
		return markAndCompareAccessorName;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((markAndCompareAccessorName == null) ? 0 : markAndCompareAccessorName.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((propName == null) ? 0 : propName.hashCode());
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
		PropTreePathInputEventChainDef other = (PropTreePathInputEventChainDef) obj;
		if (markAndCompareAccessorName == null) {
			if (other.markAndCompareAccessorName != null)
				return false;
		} else if (!markAndCompareAccessorName.equals(other.markAndCompareAccessorName))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (propName == null) {
			if (other.propName != null)
				return false;
		} else if (!propName.equals(other.propName))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "PropTreePathInputEventChainDef [" 
				+ "path=" + path 
				+ ", propName=" + propName
				+ ", markAndCompareAccessorName=" + markAndCompareAccessorName 
				+ "]";
	}
	
}