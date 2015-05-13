package org.sef4j.core.helpers.ioeventchain;

import java.util.function.Predicate;

import org.sef4j.core.api.ioeventchain.DefaultInputEventChainDefs.FilterInputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain;
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

	private InputEventChainDependency<T> underlying;

	private PredicateFilterEventSender<T> predicateFilterEventProvider;
		
	// ------------------------------------------------------------------------

	public FilterInputEventChain(String displayName,
			InputEventChain<T> underlying, Predicate<T> predicate) {
		super(displayName);
		this.predicateFilterEventProvider = new PredicateFilterEventSender<T>(innerEventProvider, predicate);
		this.underlying = new InputEventChainDependency<T>(underlying, predicateFilterEventProvider);
	}

	
	@Override
	public void close() {
		super.close();
		assert ! isStarted();
		
		this.underlying = null;
		this.predicateFilterEventProvider = null;		
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean isStarted() {
		return underlying.isStarted();
	}

	@Override
	public void start() {
		underlying.startListener();
	}

	@Override
	public void stop() {
		underlying.stopListener();
	}

	// ------------------------------------------------------------------------
	
	public static class Factory<T> 
		extends InputEventChainFactory<FilterInputEventChainDef,FilterInputEventChain<T>> {
		
		public Factory() {
			super("FilteredInputEventChain", FilterInputEventChainDef.class);
		}

		@Override
		@SuppressWarnings("unchecked")
		public FilterInputEventChain<T> create(FilterInputEventChainDef def, DependencyObjectCreationContext ctx) {
			InputEventChain<T> underlying = ctx.getOrCreateDependencyByDef("underlying", def.getUnderlying());

			Predicate<T> predicate = (Predicate<T>) def.getFilterDef().getPredicate();

			String displayName = ctx.getCurrObjectDisplayName();
			return new FilterInputEventChain<T>(displayName, underlying, predicate);
		}
		
	}
	
}
