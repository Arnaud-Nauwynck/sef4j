package org.sef4j.core.helpers.export.senders;

import java.io.Serializable;
import java.util.Collection;

import org.sef4j.core.helpers.export.ExportFragmentsProviderDef;
import org.sef4j.core.helpers.tasks.TaskDef;

import com.google.common.collect.ImmutableList;


public class ExportFragmentsPollingEventProviderDef extends TaskDef implements Serializable {
	
	/** */
	private static final long serialVersionUID = 1L;
	
	private final ImmutableList<ExportFragmentsProviderDef> fragmentProviderDefs;

	// ------------------------------------------------------------------------
	
	public ExportFragmentsPollingEventProviderDef(Collection<ExportFragmentsProviderDef> fragmentProviderDefs) {
		this.fragmentProviderDefs = ImmutableList.copyOf(fragmentProviderDefs);
	}

	// ------------------------------------------------------------------------

	public ImmutableList<ExportFragmentsProviderDef> getFragmentProviderDefs() {
		return fragmentProviderDefs;
	}

	// ------------------------------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fragmentProviderDefs == null) ? 0 : fragmentProviderDefs.hashCode());
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
		ExportFragmentsPollingEventProviderDef other = (ExportFragmentsPollingEventProviderDef) obj;
		if (fragmentProviderDefs == null) {
			if (other.fragmentProviderDefs != null)
				return false;
		} else if (!fragmentProviderDefs.equals(other.fragmentProviderDefs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExportFragmentsPollingEventProviderDef [fragmentProviderDefs=" + fragmentProviderDefs + "]";
	}	
	
}