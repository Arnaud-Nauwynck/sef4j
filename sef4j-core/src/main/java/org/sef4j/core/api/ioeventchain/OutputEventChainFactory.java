package org.sef4j.core.api.ioeventchain;

import org.sef4j.core.util.factorydef.AbstractSharedObjByDefFactory;

/**
 * Factory for OutputEventChain, using OutputEventChainDef
 */
public abstract class OutputEventChainFactory<T> extends AbstractSharedObjByDefFactory<OutputEventChainDef,OutputEventChain<T>> {

	// ------------------------------------------------------------------------
	
	public OutputEventChainFactory(String displayName, Class<OutputEventChainDef> defClass) {
		super(displayName, defClass);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public abstract boolean accepts(OutputEventChainDef def);

//	@Override
//	public abstract OutputEventChain<T> create(OutputEventChainDef def,
//			ObjectByDefRepository<OutputEventChainDef,?,OutputEventChain<T>> repository);

	// ------------------------------------------------------------------------
	
}
