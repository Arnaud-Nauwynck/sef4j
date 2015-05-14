package org.sef4j.core.api.def.ioevenchain;

import org.sef4j.core.api.def.EventPredicateDef;

/**
 * value-object for describing <code>FilteredEventSender(EventSender(..))<code>
 */
public class FilterInputEventChainDef extends InputEventChainDef {
	
	/** */
	private static final long serialVersionUID = 1L;

	private final EventPredicateDef<?> eventPredicateDef;

	private final InputEventChainDef underlying;

	public FilterInputEventChainDef(EventPredicateDef<?> filterDef, InputEventChainDef underlying) {
		this.eventPredicateDef = filterDef;
		this.underlying = underlying;
	}

	@SuppressWarnings("unchecked")
	public <T> EventPredicateDef<T> getFilterDef() {
		return (EventPredicateDef<T>) eventPredicateDef;
	}

	public InputEventChainDef getUnderlying() {
		return underlying;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventPredicateDef == null) ? 0 : eventPredicateDef.hashCode());
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
		FilterInputEventChainDef other = (FilterInputEventChainDef) obj;
		if (eventPredicateDef == null) {
			if (other.eventPredicateDef != null)
				return false;
		} else if (!eventPredicateDef.equals(other.eventPredicateDef))
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
		return "FitleredInputEventChainDef [filterDef=" + eventPredicateDef + ", underlying=" + underlying + "]";
	}
	
}