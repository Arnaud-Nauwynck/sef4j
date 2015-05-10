package org.sef4j.core.helpers.export.senders;

import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.api.ioeventchain.InputEventChainDef;
import org.sef4j.core.helpers.tasks.PeriodicTask;

/**
 * InputEventChain sub-class implementation for exporting fragments of collected data and incrementatl change event
 * 
 * see corresponding adapted class EventSenderFragmentsExporter and PeriodicTask
 */
public class FragmentsExporterInputEventChain<T,E> extends InputEventChain<E> {

	private PeriodicTask sendChangesPeriodicTask;

	private EventSenderFragmentsExporter<T,E> exporter;

	// ------------------------------------------------------------------------
	
	public FragmentsExporterInputEventChain(InputEventChainDef def, String displayName) {
		super(def, displayName);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean isStarted() {
		return sendChangesPeriodicTask.isStarted();
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

	// ------------------------------------------------------------------------
	
}
