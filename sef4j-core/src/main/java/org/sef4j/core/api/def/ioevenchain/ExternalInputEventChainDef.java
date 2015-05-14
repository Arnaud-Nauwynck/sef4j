package org.sef4j.core.api.def.ioevenchain;

public class ExternalInputEventChainDef extends InputEventChainDef {
	
	/** */
	private static final long serialVersionUID = 1L;
	
	private final String externalSystem;
	private final InputEventChainDef underlying;

	public ExternalInputEventChainDef(String externalSystem, InputEventChainDef underlying) {
		this.externalSystem = externalSystem;
		this.underlying = underlying;
	}
	
	public String getExternalSystem() {
		return externalSystem;
	}

	public InputEventChainDef getUnderlying() {
		return underlying;
	}

	@Override
	public String toString() {
		return "ExternalInputEventChainDef[" + externalSystem + " : " + underlying + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((externalSystem == null) ? 0 : externalSystem.hashCode());
		result = prime * result + ((underlying == null) ? 0 : underlying.hashCode());
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
		ExternalInputEventChainDef other = (ExternalInputEventChainDef) obj;
		if (externalSystem == null) {
			if (other.externalSystem != null)
				return false;
		} else if (!externalSystem.equals(other.externalSystem))
			return false;
		if (underlying == null) {
			if (other.underlying != null)
				return false;
		} else if (!underlying.equals(other.underlying))
			return false;
		return true;
	}

	
}