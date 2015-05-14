package org.sef4j.core.api.ioeventchain;

import org.sef4j.core.api.def.ioevenchain.InputEventChainDef;
import org.sef4j.core.util.factorydef.AbstractSharedObjByDefFactory;

/**
 * Factory for InputEventChain, using InputEventChainDef
 */
public abstract class InputEventChainFactory<TDef extends InputEventChainDef, T extends InputEventChain<?>> 
	extends AbstractSharedObjByDefFactory<TDef,T> {

	// ------------------------------------------------------------------------
	
	public InputEventChainFactory(String displayName, Class<TDef> clss) {
		super(displayName, clss);
	}

	// ------------------------------------------------------------------------
	
}
