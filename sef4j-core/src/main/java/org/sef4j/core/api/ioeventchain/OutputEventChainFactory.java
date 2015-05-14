package org.sef4j.core.api.ioeventchain;

import org.sef4j.core.api.def.ioevenchain.OutputEventChainDef;
import org.sef4j.core.util.factorydef.AbstractSharedObjByDefFactory;

/**
 * Factory for OutputEventChain, using OutputEventChainDef
 */
public abstract class OutputEventChainFactory<TDef extends OutputEventChainDef, T extends OutputEventChain<?>> extends AbstractSharedObjByDefFactory<TDef,T> {

	// ------------------------------------------------------------------------
	
	public OutputEventChainFactory(String displayName, Class<TDef> defClass) {
		super(displayName, defClass);
	}

	// ------------------------------------------------------------------------
		
}
