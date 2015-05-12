package org.sef4j.core.helpers.export.senders;

import org.sef4j.core.api.ioeventchain.DefaultInputEventChainDefs.PeriodicTaskInputEventChainDef;
import org.sef4j.core.helpers.export.ExportFragmentList;
import org.sef4j.core.helpers.ioeventchain.PeriodicTaskInputEventChain;
import org.sef4j.core.helpers.tasks.PeriodicTask;
import org.sef4j.core.helpers.tasks.PollingEventProvider;
import org.sef4j.core.util.factorydef.ObjectWithHandle;

/**
 * InputEventChain sub-class implementation for exporting fragments of collected data and incrementatl change event
 * 
 */
public class FragmentsExporterPollingInputEventChain<T> extends PeriodicTaskInputEventChain<ExportFragmentList<T>> {

	// cf super .. PeriodicTask + ExportFragmentsPollingEventProvider<T>

	// ------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public FragmentsExporterPollingInputEventChain(PeriodicTaskInputEventChainDef def, String displayName,
			ObjectWithHandle<ExportFragmentsPollingEventProvider<T>> pollingEventProviderHandle,
			PeriodicTask.Builder pollingPeriodBuilder) {
		super(def, displayName, 
				(ObjectWithHandle<PollingEventProvider<ExportFragmentList<T>>>) (ObjectWithHandle<?>) pollingEventProviderHandle, 
				pollingPeriodBuilder);
	}

	// ------------------------------------------------------------------------

}
