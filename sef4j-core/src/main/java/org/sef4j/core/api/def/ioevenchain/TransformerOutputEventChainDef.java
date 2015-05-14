package org.sef4j.core.api.def.ioevenchain;

import org.sef4j.core.api.def.EventTransformerDef;

public class TransformerOutputEventChainDef extends OutputEventChainDef {
	
	/** */
	private static final long serialVersionUID = 1L;

	private final EventTransformerDef<?,?> eventTransformerDef;

	private final OutputEventChainDef underlying;

	public TransformerOutputEventChainDef(EventTransformerDef<?,?> eventTransformerDef, OutputEventChainDef underlying) {
		this.eventTransformerDef = eventTransformerDef;
		this.underlying = underlying;
	}

	public EventTransformerDef<?,?> getEventTransformerDef() {
		return eventTransformerDef;
	}

	public OutputEventChainDef getUnderlying() {
		return underlying;
	}

	@Override
	public String toString() {
		return "TransformedOutputEventChainDef [eventTransformerDef=" + eventTransformerDef + ", underlying=" + underlying + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventTransformerDef == null) ? 0 : eventTransformerDef.hashCode());
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
		TransformerOutputEventChainDef other = (TransformerOutputEventChainDef) obj;
		if (eventTransformerDef == null) {
			if (other.eventTransformerDef != null)
				return false;
		} else if (!eventTransformerDef.equals(other.eventTransformerDef))
			return false;
		if (underlying == null) {
			if (other.underlying != null)
				return false;
		} else if (!underlying.equals(other.underlying))
			return false;
		return true;
	}

	
}