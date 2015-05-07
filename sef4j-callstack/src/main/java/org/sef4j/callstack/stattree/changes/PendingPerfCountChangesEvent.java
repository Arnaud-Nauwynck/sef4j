package org.sef4j.callstack.stattree.changes;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.sef4j.callstack.stats.PendingPerfCount;
import org.sef4j.core.helpers.export.ExportFragmentList;

/**
 * event class for holding PendingPerfCount changes 
 */
public class PendingPerfCountChangesEvent implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	public static final Function<ExportFragmentList<PendingPerfCount>,List<PendingPerfCountChangesEvent>> FACTORY =
			new Function<ExportFragmentList<PendingPerfCount>,List<PendingPerfCountChangesEvent>>() {
		@Override
		public List<PendingPerfCountChangesEvent> apply(ExportFragmentList<PendingPerfCount> changes) {
			return Collections.singletonList(new PendingPerfCountChangesEvent(changes.identifiableFragmentsToValuesMap()));
		}
	};
	
	private final Map<?,PendingPerfCount> changes;

	// ------------------------------------------------------------------------
	
	public PendingPerfCountChangesEvent(Map<?, PendingPerfCount> changes) {
		if (changes == null) throw new IllegalArgumentException();
		this.changes = changes;
	}

	// ------------------------------------------------------------------------
	
	public Map<?,PendingPerfCount> getChanges() {
		return changes;
	}

	@Override
	public String toString() {
		return "PendingPerfCountChangesEvent[" 
				+ changes.size()+ " change(s)"
				+ "]";
	}
	
}
