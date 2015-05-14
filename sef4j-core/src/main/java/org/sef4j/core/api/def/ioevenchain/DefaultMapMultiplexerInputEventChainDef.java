package org.sef4j.core.api.def.ioevenchain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultMapMultiplexerInputEventChainDef<K> extends InputEventChainDef {
	
	/** */
	private static final long serialVersionUID = 1L;

	private final Map<K,InputEventChainDef> inputs;

	public DefaultMapMultiplexerInputEventChainDef(Map<K, InputEventChainDef> inputs) {
		this.inputs = Collections.unmodifiableMap(new HashMap<K, InputEventChainDef>(inputs));
	}

	public Map<K, InputEventChainDef> getInputs() {
		return inputs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inputs == null) ? 0 : inputs.hashCode());
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
		DefaultMapMultiplexerInputEventChainDef<K> other = (DefaultMapMultiplexerInputEventChainDef<K>) obj;
		if (inputs == null) {
			if (other.inputs != null)
				return false;
		} else if (!inputs.equals(other.inputs))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MultiplexedInputEventChainDef [inputs=" + inputs + "]";
	}
	
	
}