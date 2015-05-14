package org.sef4j.core.helpers.ioeventchain;

import java.util.function.Function;

import org.sef4j.core.api.def.ioevenchain.TransformerInputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.api.ioeventchain.InputEventChainFactory;
import org.sef4j.core.helpers.senders.AbstractTransformerEventSender.FuncTransformerEventSender;
import org.sef4j.core.util.factorydef.DependencyObjectCreationContext;

/**
 * InputEventChain for transforming events received from an underlying InputEventChain 
 * 
 * <PRE>
 *                         registerEventListener
 *                         <-----
 *   underlyingInput                                    TransformerInputEventChain
 *  +-----------------+    sendEvent                  +-------------------+     sendEvent
 *  | InputEventChain |    ------>                    |                   |     ----> 
 *  +-----------------+                               | fransform(event)  |
 *                         sendEvent                  +-------------------+
 *                         ------>
 *                         
 *                         unregisterEventListener
 *                         <----
 * </PRE>
 * 
 * @param <T>
 */
public class TransformerInputEventChain<TSrc,T> extends InputEventChain<T> {

	private InputEventChain<TSrc> underlying;

	private FuncTransformerEventSender<TSrc,T> transformerEventProvider;
	// private Function<TSrc,T> transformer ... = transformerEventProvider.getTransformer();
	
	private InputEventChain.ListenerHandle<TSrc> underlyingListenerHandle;
	
	// ------------------------------------------------------------------------

	public TransformerInputEventChain(String displayName,
			InputEventChain<TSrc> underlying, Function<TSrc,T> transformer) {
		super(displayName);
		this.underlying = underlying;
		this.transformerEventProvider = new FuncTransformerEventSender<TSrc,T>(innerEventProvider, transformer);
	}

	
	@Override
	public void close() {
		super.close();
		assert underlyingListenerHandle == null;
		
		this.underlying = null;
		this.transformerEventProvider = null;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean isStarted() {
		return underlyingListenerHandle != null && underlying.isStarted();
	}

	@Override
	public void start() {
		if (underlyingListenerHandle == null) {
			underlyingListenerHandle = underlying.registerEventListener(transformerEventProvider);
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
	
	public static class Factory<TSrc,T> 
		extends InputEventChainFactory<TransformerInputEventChainDef<TSrc,T>,TransformerInputEventChain<TSrc,T>> {
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Factory() {
			super("TransformedInputEventChain", (Class)TransformerInputEventChainDef.class);
		}

		@Override
		@SuppressWarnings("unchecked")
		public TransformerInputEventChain<TSrc,T> create(
				TransformerInputEventChainDef<TSrc,T> def, 
				DependencyObjectCreationContext ctx) {
			InputEventChain<TSrc> underlying = ctx.getOrCreateDependencyByDef("underlying", def.getUnderlying());
			
			Function<TSrc,T> transformer = (Function<TSrc,T>) def.getEventTransformerDef();
			
			String displayName = ctx.getCurrObjectDisplayName();
			return new TransformerInputEventChain<TSrc,T>(displayName, underlying, transformer);
		}
		
	}
	
}
