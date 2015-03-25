package org.sef4j.callstack.stattree.changes;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;

import org.sef4j.callstack.stats.PendingPerfCount;

/**
 * event class for holding PendingPerfCount changes 
 */
public class PendingPerfCountChangesEvent implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	public static final Function<Map<String,PendingPerfCount>,PendingPerfCountChangesEvent> FACTORY =
			new Function<Map<String,PendingPerfCount>,PendingPerfCountChangesEvent>() {
		@Override
		public PendingPerfCountChangesEvent apply(Map<String, PendingPerfCount> t) {
			return new PendingPerfCountChangesEvent(t);
		}
	};
	
	private final Map<String,PendingPerfCount> changes;

	// ------------------------------------------------------------------------
	
	public PendingPerfCountChangesEvent(Map<String, PendingPerfCount> changes) {
		if (changes == null) throw new IllegalArgumentException();
		this.changes = changes;
	}

	// ------------------------------------------------------------------------
	
	public Map<String,PendingPerfCount> getChanges() {
		return changes;
	}

	@Override
	public String toString() {
		return "PendingPerfCountChangesEvent[" 
				+ changes.size()+ " change(s)"
				+ "]";
	}
	
}
