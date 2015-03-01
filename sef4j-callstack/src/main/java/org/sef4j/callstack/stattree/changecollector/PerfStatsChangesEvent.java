package org.sef4j.callstack.stattree.changecollector;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;

import org.sef4j.callstack.stats.PerfStats;

/**
 * event class for holding PerfStats changes 
 */
public class PerfStatsChangesEvent implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	public static final Function<Map<String,PerfStats>,PerfStatsChangesEvent> FACTORY =
			new Function<Map<String,PerfStats>,PerfStatsChangesEvent>() {
		@Override
		public PerfStatsChangesEvent apply(Map<String, PerfStats> t) {
			return new PerfStatsChangesEvent(t);
		}
	};
	
	private final Map<String,PerfStats> changes;

	// ------------------------------------------------------------------------
	
	public PerfStatsChangesEvent(Map<String, PerfStats> changes) {
		if (changes == null) throw new IllegalArgumentException();
		this.changes = changes;
	}

	// ------------------------------------------------------------------------
	
	public Map<String, PerfStats> getChanges() {
		return changes;
	}

	@Override
	public String toString() {
		return "PerfStatsChangesEvent[" 
				+ changes.size()+ " change(s)"
				+ "]";
	}
	
}
