package org.sef4j.core.helpers.ioeventchain;

import java.util.function.Predicate;

import org.sef4j.core.api.ioeventchain.DefaultInputEventChainDefs.FilteredInputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.api.ioeventchain.InputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChainFactory;
import org.sef4j.core.helpers.senders.AbstractFilterEventSender.PredicateFilterEventSender;
import org.sef4j.core.util.factorydef.ObjectByDefRepositories;
import org.sef4j.core.util.factorydef.ObjectWithHandle;

/**
 * InputEventChain for filtering events by predicate received from an underlying InputEventChain 
 * 
 * <PRE>
 *                         registerEventListener
 *                         <-----
 *   underlyingInput                                    FilteredInputEventChain
 *  +-----------------+    sendEvent                  +-------------------+     sendEvent
 *  | InputEventChain |    ------>                    |                   |     ----> 
 *  +-----------------+                               | filter(event)  |
 *                         sendEvent                  +-------------------+
 *                         ------>
 *                         
 *                         unregisterEventListener
 *                         <----
 * </PRE>
 * 
 * @param <T>
 */
public class FilterInputEventChain<T> extends InputEventChain<T> {

	private ObjectWithHandle<? extends InputEventChain<T>> underlying;

	private PredicateFilterEventSender<T> predicateFilterEventProvider;
	
	private InputEventChain.ListenerHandle<T> underlyingListenerHandle;
	
	// ------------------------------------------------------------------------

	public FilterInputEventChain(FilteredInputEventChainDef def, String displayName,
			ObjectWithHandle<? extends InputEventChain<T>> underlying, Predicate<T> predicate) {
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
		return underlyingListenerHandle != null && underlying.getObject().isStarted();
	}

	@Override
	public void start() {
		if (underlyingListenerHandle == null) {
			underlyingListenerHandle = underlying.getObject().registerEventListener(predicateFilterEventProvider);
		}
	}

	@Override
	public void stop() {
		if (underlyingListenerHandle != null) {
			underlying.getObject().unregisterEventListener(underlyingListenerHandle);
			underlyingListenerHandle = null;
		}		
	}

	// ------------------------------------------------------------------------
	
	public static class Factory<T> extends InputEventChainFactory<T> {
		
		public Factory() {
			super("FilteredInputEventChain");
		}

		@Override
		public boolean accepts(InputEventChainDef def) {
			return def instanceof FilteredInputEventChainDef;
		}

		@Override
		@SuppressWarnings("unchecked")
		public InputEventChain<T> create(InputEventChainDef defObj, ObjectByDefRepositories repositories) {
			FilteredInputEventChainDef def = (FilteredInputEventChainDef) defObj;
			
			ObjectWithHandle<?> underlyingHandle = repositories.getOrCreateByDef(def.getUnderlying());
			ObjectWithHandle<InputEventChain<T>> underlyingHandle2 = 
					(ObjectWithHandle<InputEventChain<T>>) underlyingHandle;

			Predicate<T> predicate = (Predicate<T>) def.getFilterDef().getPredicate();
			
			return new FilterInputEventChain<T>(def, "Filter", underlyingHandle2, predicate);
		}
		
	}
	
}
