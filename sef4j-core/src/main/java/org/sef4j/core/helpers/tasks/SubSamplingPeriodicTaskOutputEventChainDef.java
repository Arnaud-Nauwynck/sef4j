package org.sef4j.core.helpers.tasks;

import org.sef4j.core.api.def.ioevenchain.OutputEventChainDef;

public class SubSamplingPeriodicTaskOutputEventChainDef extends OutputEventChainDef {
	
	/** */
	private static final long serialVersionUID = 1L;
	
	private final PeriodicTaskOutputEventChainDef underlying;
	private final int subSampingFrequency;
	
	public SubSamplingPeriodicTaskOutputEventChainDef(PeriodicTaskOutputEventChainDef underlying, int subSampingFrequency) {
		this.underlying = underlying;
		this.subSampingFrequency = subSampingFrequency;
	}

	public PeriodicTaskOutputEventChainDef getUnderlying() {
		return underlying;
	}

	public int getSubSampingFrequency() {
		return subSampingFrequency;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + subSampingFrequency;
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
		SubSamplingPeriodicTaskOutputEventChainDef other = (SubSamplingPeriodicTaskOutputEventChainDef) obj;
		if (subSampingFrequency != other.subSampingFrequency)
			return false;
		if (underlying == null) {
			if (other.underlying != null)
				return false;
		} else if (!underlying.equals(other.underlying))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SubSamplingPeriodicTaskOutputEventChainDef [underlying=" + underlying + ", subSampingFrequency=" + subSampingFrequency + "]";
	}
	
}