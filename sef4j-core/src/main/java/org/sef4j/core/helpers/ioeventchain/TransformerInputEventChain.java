package org.sef4j.core.helpers.ioeventchain;

import java.util.function.Function;

import org.sef4j.core.api.ioeventchain.DefaultInputEventChainDefs.TransformerInputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.api.ioeventchain.InputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChainFactory;
import org.sef4j.core.helpers.senders.AbstractTransformerEventSender.FuncTransformerEventSender;
import org.sef4j.core.util.factorydef.ObjectByDefRepository;
import org.sef4j.core.util.factorydef.ObjectByDefRepository.ObjectWithHandle;

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

	private ObjectWithHandle<? extends InputEventChain<TSrc>> underlying;

	private FuncTransformerEventSender<TSrc,T> transformerEventProvider;
	// private Function<TSrc,T> transformer ... = transformerEventProvider.getTransformer();
	
	private InputEventChain.ListenerHandle<TSrc> underlyingListenerHandle;
	
	// ------------------------------------------------------------------------

	public TransformerInputEventChain(TransformerInputEventChainDef<TSrc,T> def, String displayName,
			ObjectWithHandle<? extends InputEventChain<TSrc>> underlying, Function<TSrc,T> transformer) {
		super(def, displayName);
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
		return underlyingListenerHandle != null && underlying.getObject().isStarted();
	}

	@Override
	public void start() {
		if (underlyingListenerHandle == null) {
			underlyingListenerHandle = underlying.getObject().registerEventListener(transformerEventProvider);
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
	
	public static class Factory<TSrc,T> extends InputEventChainFactory<T> {
		
		public Factory() {
			super("TransformedInputEventChain");
		}

		@Override
		public boolean accepts(InputEventChainDef def) {
			return def instanceof TransformerInputEventChainDef;
		}

		@Override
		@SuppressWarnings("unchecked")
		public InputEventChain<T> create(InputEventChainDef defObj, ObjectByDefRepository<InputEventChainDef,?> repository) {
			TransformerInputEventChainDef<TSrc,T> def = (TransformerInputEventChainDef<TSrc,T>) defObj;
			
			ObjectWithHandle<InputEventChain<TSrc>> underlying = (ObjectWithHandle<InputEventChain<TSrc>>)
					repository.register(def.getUnderlying());
			
			Function<TSrc,T> transformer = (Function<TSrc,T>) def.getEventTransformerDef();
			
			return new TransformerInputEventChain<TSrc,T>(def, "Filter", underlying, transformer);
		}
		
	}
	
}
