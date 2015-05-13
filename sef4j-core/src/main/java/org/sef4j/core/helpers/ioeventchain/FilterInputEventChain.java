package org.sef4j.core.helpers.ioeventchain;

import java.util.function.Predicate;

import org.sef4j.core.api.ioeventchain.DefaultInputEventChainDefs.FilteredInputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.api.ioeventchain.InputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChainFactory;
import org.sef4j.core.helpers.senders.AbstractFilterEventSender.PredicateFilterEventSender;
import org.sef4j.core.util.factorydef.DependencyObjectCreationContext;

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

	private InputEventChain<T> underlying;

	private PredicateFilterEventSender<T> predicateFilterEventProvider;
	
	private InputEventChain.ListenerHandle<T> underlyingListenerHandle;
	
	// ------------------------------------------------------------------------

	public FilterInputEventChain(String displayName,
			InputEventChain<T> underlying, Predicate<T> predicate) {
		super(displayName);
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

	// ------------------------------------------------------------------------
	
	public static class Factory<T> extends InputEventChainFactory<T> {
		
		public Factory() {
			super("FilteredInputEventChain", FilteredInputEventChainDef.class);
		}

		@Override
		@SuppressWarnings("unchecked")
		public InputEventChain<T> create(InputEventChainDef defObj, DependencyObjectCreationContext ctx) {
			FilteredInputEventChainDef def = (FilteredInputEventChainDef) defObj;
			
			InputEventChain<T> underlying = ctx.getOrCreateDependencyByDef("underlying", def.getUnderlying());

			Predicate<T> predicate = (Predicate<T>) def.getFilterDef().getPredicate();
			String displayName = ctx.getCurrObjectDisplayName();
			
			return new FilterInputEventChain<T>(displayName, underlying, predicate);
		}
		
	}
	
}
