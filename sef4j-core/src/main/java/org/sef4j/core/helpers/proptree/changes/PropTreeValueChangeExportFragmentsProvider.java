package org.sef4j.core.helpers.proptree.changes;

import java.util.function.Function;

import org.sef4j.core.helpers.exporters.fragments.ExportFragment;
import org.sef4j.core.helpers.exporters.fragments.ExportFragmentsAdder;
import org.sef4j.core.helpers.exporters.fragments.ExportFragmentsProvider;
import org.sef4j.core.helpers.proptree.changes.IPropTreeValueChangeCollector;

/**
 * adapter for ICallTreeValueChangeCollector<T> to ExportFragmentsProvider<E>
 * 
 * @param <T>
 */
public class PropTreeValueChangeExportFragmentsProvider<T,E> implements ExportFragmentsProvider<E> {

	private String displayName;
	
	private IPropTreeValueChangeCollector<T> changeCollector;
	private Function<T,E> valueToEvent;
	private Function<E,String> eventToNodeName;
	
	// ------------------------------------------------------------------------

	public PropTreeValueChangeExportFragmentsProvider(String displayName, 
			IPropTreeValueChangeCollector<T> changeCollector,
			Function<T,E> valueToEvent,
			Function<E,String> eventToNodeName) {
		this.displayName = displayName;
		this.changeCollector = changeCollector;
		this.valueToEvent = valueToEvent;
		this.eventToNodeName = eventToNodeName;
	}

	// ------------------------------------------------------------------------

	@Override
	public void provideFragments(ExportFragmentsAdder<E> appender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExportFragmentFailed(ExportFragment<E> entry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOverrideIdentifiableFragment(ExportFragment<E> entry, ExportFragment<E> prev) {
		// TODO Auto-generated method stub
		
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "CallTreeValueChangeExportFragmentsProvider[" + displayName + "]";
	}
	
}
