package org.sef4j.core.helpers.export;

import org.sef4j.core.helpers.export.senders.ExportFragmentsPollingEventProvider;


/**
 * interface for providing fragments to FragmentsProvidersExporter
 * 
 * @see ExportFragmentsPollingEventProvider
 */
public interface ExportFragmentsProvider<T> {

	public void provideFragments(ExportFragmentsAdder<T> out);
	
	public void markAndCollectChanges(ExportFragmentsAdder<T> out);

	//TODO??
	// public void onExportFragmentFailed(ExportFragment<T> entry);
//	public void onOverrideIdentifiableFragment(ExportFragment<T> entry, ExportFragment<T> prev);
	
}
