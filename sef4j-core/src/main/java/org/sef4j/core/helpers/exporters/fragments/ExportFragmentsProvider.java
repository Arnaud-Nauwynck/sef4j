package org.sef4j.core.helpers.exporters.fragments;


/**
 * interface for providing fragments to exporter
 * 
 * a component wanting to provide some fragments to export must register itself as <code>ExportFragmentsProvider</code>
 * into 
 */
public interface ExportFragmentsProvider<T> {

	public void provideFragments(ExportFragmentsAdder<T> appender);
	
	
	public void onExportFragmentFailed(ExportFragment<T> entry);
	public void onOverrideIdentifiableFragment(ExportFragment<T> entry, ExportFragment<T> prev);
	
}
