package org.sef4j.core.helpers.ioeventchain;

import java.util.HashMap;
import java.util.Map;

import org.sef4j.core.api.EventSender;
import org.sef4j.core.api.ioeventchain.DefaultOutputEventChainDefs.DemultiplexerOutputEventChainDef;
import org.sef4j.core.api.ioeventchain.OutputEventChain;
import org.sef4j.core.api.ioeventchain.OutputEventChainDef;
import org.sef4j.core.api.ioeventchain.OutputEventChainFactory;
import org.sef4j.core.helpers.senders.multiplexer.DefaultMapDemultiplexerEventSender;
import org.sef4j.core.helpers.senders.multiplexer.MultiplexedEvent;
import org.sef4j.core.util.factorydef.DependencyObjectCreationContext;

import com.google.common.collect.ImmutableMap;

/**
 * OutputEventChain for unwrapping events with key and dispatching to map<K,> underlying output
 * 
 * <PRE>
 *  </PRE>
 *  
 * @param <K>
 * @param <T>
 */
public class DefaultMapDemultiplexerOutputEventChain<K,T> extends OutputEventChain<MultiplexedEvent<K,T>> {

	private final Map<K,OutputEventChainDependency<T>> outputDeps;

	protected DefaultMapDemultiplexerEventSender<K,T> innerSender;
	
	// ------------------------------------------------------------------------

	public DefaultMapDemultiplexerOutputEventChain(String displayName, 
			Map<K,OutputEventChain<T>> outputs
			) {
		super(displayName);
		// wrap convert Map<K,OutputEventChain> to Map<K,OutputEventChainDependency>
		ImmutableMap.Builder<K,OutputEventChainDependency<T>> depsBuilder = new ImmutableMap.Builder<K,OutputEventChainDependency<T>>();
		for(Map.Entry<K,OutputEventChain<T>> e : outputs.entrySet()) {
			OutputEventChainDependency<T> outputDep = new OutputEventChainDependency<T>(e.getValue());
			depsBuilder.put(e.getKey(), outputDep);
		}
		this.outputDeps = depsBuilder.build();
		// convert Map<K,OutputEventChainDependency> to Map<K,EventSender(getInnerEventSender())>
		ImmutableMap.Builder<K,EventSender<T>> outputSenders = new ImmutableMap.Builder<K,EventSender<T>>();
		for(Map.Entry<K,OutputEventChainDependency<T>> e : outputDeps.entrySet()) {
			outputSenders.put(e.getKey(), e.getValue().getInnerEventSender());
		}			
		this.innerSender = new DefaultMapDemultiplexerEventSender<K,T>(outputSenders.build());
	}

	// ------------------------------------------------------------------------
	
	protected EventSender<MultiplexedEvent<K,T>> getInnerEventSender() {
		return innerSender;
	}

	@Override
	public boolean isStarted() {
		boolean res = OutputEventChainDependency.areAllStarted(outputDeps.values());
		return res;
	}

	@Override
	public void start() {
		OutputEventChainDependency.startAll(outputDeps.values());
	}

	@Override
	public void stop() {
		OutputEventChainDependency.stopAll(outputDeps.values());
	}

	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "DefaultDemultiplexerOutputEventChain[" + outputDeps.keySet() + "]";
	}

	// ------------------------------------------------------------------------
	
	public static class Factory<K,T> 
		extends OutputEventChainFactory<DemultiplexerOutputEventChainDef<K>,DefaultMapDemultiplexerOutputEventChain<K,T>> {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Factory() {
			super("DefaultMapDemultiplexerOutputEventChain", (Class) DemultiplexerOutputEventChainDef.class);
		}

		@Override
		public DefaultMapDemultiplexerOutputEventChain<K,T> create(
				DemultiplexerOutputEventChainDef<K> def, 
				DependencyObjectCreationContext ctx) {
			Map<K, OutputEventChain<T>> outputs = new HashMap<K, OutputEventChain<T>>();
			for(Map.Entry<K,OutputEventChainDef> e : def.getOutputs().entrySet()) {
				K key = e.getKey();
				OutputEventChain<T> outputDep = ctx.getOrCreateDependencyByDef("output-" + key, def);
				outputs.put(key, outputDep);
			}
			String displayName = ctx.getCurrObjectDisplayName();
			return new DefaultMapDemultiplexerOutputEventChain<K,T>(displayName, outputs);
		}
		
	}
}
