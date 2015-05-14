package org.sef4j.core.api.def.ioevenchain;

import java.util.Arrays;
import java.util.Collection;

public class UnionOutputEventChainDef extends OutputEventChainDef {
	
	/** */
	private static final long serialVersionUID = 1L;
	
	private final OutputEventChainDef[] elements;

	public UnionOutputEventChainDef(Collection<OutputEventChainDef> elements) {
		this.elements = elements.toArray(new OutputEventChainDef[elements.size()]);
	}

	public OutputEventChainDef[] getElements() {
		return elements;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(elements);
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
		UnionOutputEventChainDef other = (UnionOutputEventChainDef) obj;
		if (!Arrays.equals(elements, other.elements))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UnionOutputEventChainDef [elements=" + Arrays.toString(elements) + "]";
	}
	
}