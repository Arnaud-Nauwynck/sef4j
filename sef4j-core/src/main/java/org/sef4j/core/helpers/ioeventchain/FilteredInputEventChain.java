package org.sef4j.core.helpers.ioeventchain;

import java.util.function.Predicate;

import org.sef4j.core.api.ioeventchain.DefaultInputEventChainDefs.FilteredInputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.helpers.senders.AbstractFilterEventSender.PredicateFilterEventSender;

/**
 * InputEventChain for filtering events by predicate
 * 
 * @param <T>
 */
public class FilteredInputEventChain<T> extends InputEventChain<T> {

	private InputEventChain<T> underlying;

	private PredicateFilterEventSender<T> predicateFilterEventProvider;
	
	private InputEventChain.ListenerHandle<T> underlyingListenerHandle;
	
	// ------------------------------------------------------------------------

	public FilteredInputEventChain(FilteredInputEventChainDef def, String displayName,
			InputEventChain<T> underlying, Predicate<T> predicate) {
		super(def, displayName);
		this.underlying = underlying;
		this.predicateFilterEventProvider = new PredicateFilterEventSender<T>(innerEventProvider, predicate);
	}

	
	@Override
	public void close() {
		super.close();
		assert underlyingListenerHandle == null;
		
		this.underlying = null;
		this.predicateFilterEventProvider = null;		
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean isStarted() {
		return underlyingListenerHandle != null && underlying.isStarted();
	}

	@Override
	public void start() {
		if (underlyingListenerHandle == null) {
			underlyingListenerHandle = underlying.registerEventListener(predicateFilterEventProvider);
		}
	}

	@Override
	public void stop() {
		if (underlyingListenerHandle != null) {
			underlying.unregisterEventListener(underlyingListenerHandle);
			underlyingListenerHandle = null;
		}		
	}
	
}
