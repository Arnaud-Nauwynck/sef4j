package org.sef4j.callstack.stattree.changes;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.sef4j.callstack.stats.PerfStats;
import org.sef4j.core.helpers.export.ExportFragmentList;

/**
 * event class for holding PerfStats changes 
 */
public class PerfStatsChangesEvent implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	public static final Function<ExportFragmentList<PerfStats>,List<PerfStatsChangesEvent>> FACTORY =
			new Function<ExportFragmentList<PerfStats>,List<PerfStatsChangesEvent>>() {
		@Override
		public List<PerfStatsChangesEvent> apply(ExportFragmentList<PerfStats> changes) {
			return Collections.singletonList(new PerfStatsChangesEvent(changes.identifiableFragmentsToValuesMap()));
		}
	};
	
	private final Map<?,PerfStats> changes;

	// ------------------------------------------------------------------------
	
	public PerfStatsChangesEvent(Map<?, PerfStats> changes) {
		if (changes == null) throw new IllegalArgumentException();
		this.changes = changes;
	}

	// ------------------------------------------------------------------------
	
	public Map<?,PerfStats> getChanges() {
		return changes;
	}

	@Override
	public String toString() {
		return "PerfStatsChangesEvent[" 
				+ changes.size()+ " change(s)"
				+ "]";
	}
	
}
