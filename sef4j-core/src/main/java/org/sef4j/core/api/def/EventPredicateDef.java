package org.sef4j.core.api.def;

import java.util.function.Predicate;

import org.sef4j.core.util.factorydef.AbstractObjectDef;

public class EventPredicateDef<T> extends AbstractObjectDef {

	/** */
	private static final long serialVersionUID = 1L;

	// TODO ... if not serializable, should replace by corresponding DTO def
	private final Predicate<T> predicate;

	// ------------------------------------------------------------------------
	
	public EventPredicateDef(Predicate<T> predicate) {
		this.predicate = predicate;
	}

	// ------------------------------------------------------------------------

	public Predicate<T> getPredicate() {
		return predicate;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
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
		EventPredicateDef<T> other = (EventPredicateDef<T>) obj;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EventPredicateDef [" + predicate + "]";
	}
	
}
