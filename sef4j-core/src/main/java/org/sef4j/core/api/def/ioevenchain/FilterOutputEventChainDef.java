package org.sef4j.core.api.def.ioevenchain;

import org.sef4j.core.api.def.EventPredicateDef;

/**
 * value-object for describing <code>FilteredEventSender(EventSender(..))<code>
 */
public class FilterOutputEventChainDef extends OutputEventChainDef {
	
	/** */
	private static final long serialVersionUID = 1L;

	private final EventPredicateDef<?> filterDef;

	private final OutputEventChainDef underlying;

	public FilterOutputEventChainDef(EventPredicateDef<?> filterDef, OutputEventChainDef underlying) {
		this.filterDef = filterDef;
		this.underlying = underlying;
	}

	public EventPredicateDef<?> getFilterDef() {
		return filterDef;
	}

	public OutputEventChainDef getUnderlying() {
		return underlying;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filterDef == null) ? 0 : filterDef.hashCode());
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
		FilterOutputEventChainDef other = (FilterOutputEventChainDef) obj;
		if (filterDef == null) {
			if (other.filterDef != null)
				return false;
		} else if (!filterDef.equals(other.filterDef))
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
		return "FitleredOutputEventChainDef [filterDef=" + filterDef + ", underlying=" + underlying + "]";
	}
	
}