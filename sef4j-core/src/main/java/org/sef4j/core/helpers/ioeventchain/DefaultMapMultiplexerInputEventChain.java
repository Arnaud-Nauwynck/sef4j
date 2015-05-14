package org.sef4j.core.helpers.ioeventchain;

import java.util.HashMap;
import java.util.Map;

import org.sef4j.core.api.def.ioevenchain.DefaultMapMultiplexerInputEventChainDef;
import org.sef4j.core.api.def.ioevenchain.InputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.api.ioeventchain.InputEventChainFactory;
import org.sef4j.core.helpers.senders.multiplexer.DefaultMultiplexerPerKeyEventSender;
import org.sef4j.core.helpers.senders.multiplexer.MultiplexedEvent;
import org.sef4j.core.util.factorydef.DependencyObjectCreationContext;

import com.google.common.collect.ImmutableMap;

/**
 * InputEventChain for wrapping input InputEventChain events with keys
 * 
 * <PRE>
 * 
 * inputChain1.addListener(key1Listener)                                   addEventListener() 
 *                     <-----                                            <----
 * inputChain2.addListener(key1Listener)
 *                     <-----
 *                                 +---------------------------+  
 *  inputChain1.sendEvent(e1)      |key1Listener               |   sendEvent(MultiplexedEvent(key1,e1)
 *    --->                         |   ----> wrapE1=(key1,e1)  |  --->
 *                                 |                           |
 *  inputChain2.sendEvent(e2)      |key2Listener               |   sendEvent(MultiplexedEvent(key1,e1)
 *    --->                         |   ----> wrapE1=(key1,e1)  |  --->
 *                                 +---------------------------+ 
 *                                 
 * inputChain1.removeListener(key1Listener)                               removeEventListener() 
 *                     <-----                                           <----
 * inputChain2.removeListener(key1Listener)
 *                     <-----
 *  
 *  
 *  </PRE>
 *  
 * @param <K>
 * @param <T>
 */
public class DefaultMapMultiplexerInputEventChain<K,T> extends InputEventChain<MultiplexedEvent<K,T>> {

	private final Map<K,InputEventChainDependency<T>> inputDeps;

	// ------------------------------------------------------------------------

	public DefaultMapMultiplexerInputEventChain(String displayName, Map<K,InputEventChain<T>> inputs) {
		super(displayName);
		ImmutableMap.Builder<K,InputEventChainDependency<T>> depsBuilder = new ImmutableMap.Builder<K,InputEventChainDependency<T>>();
		for(Map.Entry<K,InputEventChain<T>> e : inputs.entrySet()) {
			K key = e.getKey();
			InputEventChain<T> keyInput = e.getValue();
			DefaultMultiplexerPerKeyEventSender<K,T> innerKeyWrapperSender = 
					new DefaultMultiplexerPerKeyEventSender<K,T>(key, innerEventProvider);
			InputEventChainDependency<T> inputDep = 
					new InputEventChainDependency<T>(keyInput, innerKeyWrapperSender);
			depsBuilder.put(e.getKey(), inputDep);
		}
		this.inputDeps = depsBuilder.build();
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean isStarted() {
		boolean res = InputEventChainDependency.areAllStarted(inputDeps.values());
		return res;
	}

	@Override
	public void start() {
		InputEventChainDependency.startListenerAll(inputDeps.values());
	}

	@Override
	public void stop() {
		InputEventChainDependency.stopListenerAll(inputDeps.values());
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "DefaultMultiplexedInputEventChain[" + inputDeps.keySet() + "]";
	}

	// ------------------------------------------------------------------------
	
	public static class Factory<K,T> 
		extends InputEventChainFactory<DefaultMapMultiplexerInputEventChainDef<K>,DefaultMapMultiplexerInputEventChain<K,T>> {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Factory() {
			super("DefaultMapMultiplexerInputEventChain", (Class) DefaultMapMultiplexerInputEventChainDef.class);
		}

		@Override
		public DefaultMapMultiplexerInputEventChain<K,T> create(
				DefaultMapMultiplexerInputEventChainDef<K> def, 
				DependencyObjectCreationContext ctx) {
			Map<K, InputEventChain<T>> inputs = new HashMap<K, InputEventChain<T>>();
			for(Map.Entry<K,InputEventChainDef> e : def.getInputs().entrySet()) {
				K key = e.getKey();
				InputEventChain<T> inputDep = ctx.getOrCreateDependencyByDef("input-" + key, def);
				inputs.put(key, inputDep);
			}
			String displayName = ctx.getCurrObjectDisplayName();
			return new DefaultMapMultiplexerInputEventChain<K,T>(displayName, inputs);
		}
		
	}
}
