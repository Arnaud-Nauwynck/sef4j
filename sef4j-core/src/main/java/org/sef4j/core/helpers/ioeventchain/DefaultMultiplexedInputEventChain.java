package org.sef4j.core.helpers.ioeventchain;

import java.util.HashMap;
import java.util.Map;

import org.sef4j.core.api.ioeventchain.DefaultInputEventChainDefs.DefaultMultiplexedInputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChain;
import org.sef4j.core.api.ioeventchain.InputEventChainDef;
import org.sef4j.core.api.ioeventchain.InputEventChainFactory;
import org.sef4j.core.util.factorydef.DependencyObjectCreationContext;

import com.google.common.collect.ImmutableMap;

public class DefaultMultiplexedInputEventChain<K,T> extends InputEventChain<T> {

	private final Map<K,InputEventChainDependency<T>> inputDeps;

	// ------------------------------------------------------------------------

	public DefaultMultiplexedInputEventChain(String displayName, Map<K,InputEventChain<T>> inputs) {
		super(displayName);
		ImmutableMap.Builder<K,InputEventChainDependency<T>> depsBuilder = new ImmutableMap.Builder<K,InputEventChainDependency<T>>();
		for(Map.Entry<K,InputEventChain<T>> e : inputs.entrySet()) {
			InputEventChainDependency<T> dep = new InputEventChainDependency<T>(e.getValue(), null); // TODO
			depsBuilder.put(e.getKey(), dep);
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
		extends InputEventChainFactory<DefaultMultiplexedInputEventChainDef<K>,DefaultMultiplexedInputEventChain<K,T>> {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Factory() {
			super("DefaultMultiplexedInputEventChain", (Class) DefaultMultiplexedInputEventChainDef.class);
		}

		@Override
		public DefaultMultiplexedInputEventChain<K,T> create(
				DefaultMultiplexedInputEventChainDef<K> def, 
				DependencyObjectCreationContext ctx) {
			Map<K, InputEventChain<T>> inputs = new HashMap<K, InputEventChain<T>>();
			for(Map.Entry<K,InputEventChainDef> e : def.getInputs().entrySet()) {
				K key = e.getKey();
				InputEventChain<T> inputDep = ctx.getOrCreateDependencyByDef("input-" + key, def);
				inputs.put(key, inputDep);
			}
			String displayName = ctx.getCurrObjectDisplayName();
			return new DefaultMultiplexedInputEventChain<K,T>(displayName, inputs);
		}
		
	}
}
