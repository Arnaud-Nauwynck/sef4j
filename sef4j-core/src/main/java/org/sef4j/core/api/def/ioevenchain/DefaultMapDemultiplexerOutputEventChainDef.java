package org.sef4j.core.api.def.ioevenchain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultMapDemultiplexerOutputEventChainDef<K> extends OutputEventChainDef {
	
	/** */
	private static final long serialVersionUID = 1L;

	private final Map<K,OutputEventChainDef> outputs;

	public DefaultMapDemultiplexerOutputEventChainDef(Map<K, OutputEventChainDef> Outputs) {
		this.outputs = Collections.unmodifiableMap(new HashMap<K, OutputEventChainDef>(Outputs));
	}

	public Map<K, OutputEventChainDef> getOutputs() {
		return outputs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((outputs == null) ? 0 : outputs.hashCode());
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
		@SuppressWarnings("unchecked")
		DefaultMapDemultiplexerOutputEventChainDef<K> other = (DefaultMapDemultiplexerOutputEventChainDef<K>) obj;
		if (outputs == null) {
			if (other.outputs != null)
				return false;
		} else if (!outputs.equals(other.outputs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DemultiplexerOutputEventChainDef[" + outputs + "]";
	}
	
	
}