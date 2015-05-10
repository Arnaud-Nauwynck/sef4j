package org.sef4j.core.api.ioeventchain;

import org.sef4j.core.util.factorydef.AbstractObjByDefFactory;

/**
 * Factory for InputEventChain, using InputEventChainDef
 */
public abstract class InputEventChainFactory<T> extends AbstractObjByDefFactory<InputEventChainDef,InputEventChain<T>> {

	// ------------------------------------------------------------------------
	
	public InputEventChainFactory(String displayName) {
		super(displayName);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public abstract boolean accepts(InputEventChainDef def);

//	@Override
//	public abstract InputEventChain<T> create(InputEventChainDef def,
//			ObjectByDefRepository<InputEventChainDef,InputEventChain<T>> repository);

	
}
