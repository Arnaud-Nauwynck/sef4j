package org.sef4j.core.api.ioeventchain;

import org.sef4j.core.util.factorydef.AbstractSharedObjByDefFactory;

/**
 * Factory for InputEventChain, using InputEventChainDef
 */
public abstract class InputEventChainFactory<T> 
	extends AbstractSharedObjByDefFactory<InputEventChainDef,InputEventChain<T>> {

	// ------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public InputEventChainFactory(String displayName, Class<? extends InputEventChainDef> clss) {
		super(displayName, (Class<InputEventChainDef>) clss);
	}

	// ------------------------------------------------------------------------
	
}
