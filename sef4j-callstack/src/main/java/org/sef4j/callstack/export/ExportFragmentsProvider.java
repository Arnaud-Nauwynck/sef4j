package org.sef4j.callstack.export;


/**
 * interface extension for providing text fragments to exporter
 * 
 * a component wanting to provide some fragments to export must register itself as <code>ExportFragmentsProvider</code>
 * into 
 */
public interface ExportFragmentsProvider<T> {

	public void provideFragments(ExportFragmentsAdder<T> appender);
	
}
