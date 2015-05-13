package org.sef4j.core.helpers.export.senders;

import org.sef4j.core.helpers.export.ExportFragmentList;
import org.sef4j.core.helpers.ioeventchain.PeriodicTaskInputEventChain;
import org.sef4j.core.helpers.tasks.PeriodicTask;

/**
 * InputEventChain "alias" sub-class for PeriodicTaskInputEventChain<ExportFragmentList<T>>(ExportFragmentsPollingEventProvider<T>>)
 * for exporting fragments of collected data and incrementatl change event
 * by delegating to ExportFragmentsPollingEventProvider<T>
 * 
 */
public class FragmentsExporterPollingInputEventChain<T> extends PeriodicTaskInputEventChain<ExportFragmentList<T>> {

	// cf super .. PeriodicTask + ExportFragmentsPollingEventProvider<T>

	// ------------------------------------------------------------------------
	
	public FragmentsExporterPollingInputEventChain(String displayName,
			ExportFragmentsPollingEventProvider<T> pollingEventProvider,
			PeriodicTask.Builder pollingPeriodBuilder) {
		super(displayName, pollingEventProvider, pollingPeriodBuilder);
	}

	// ------------------------------------------------------------------------

}
