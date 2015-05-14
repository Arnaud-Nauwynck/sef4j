package org.sef4j.core.api.def.ioevenchain;

public class NamedOutputEventChainDef extends OutputEventChainDef {
	
	/** */
	private static final long serialVersionUID = 1L;
	
	private final String name;

	public NamedOutputEventChainDef(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		NamedOutputEventChainDef other = (NamedOutputEventChainDef) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "NamedOutputEventChainDef[" + name + "]";
	}
	
}