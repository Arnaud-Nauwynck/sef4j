package org.sef4j.core.api.ioeventchain;

import org.sef4j.core.util.factorydef.AbstractObjByDefFactory;

/**
 * Factory for OutputEventChain, using OutputEventChainDef
 */
public abstract class OutputEventChainFactory<T> extends AbstractObjByDefFactory<OutputEventChainDef,OutputEventChain<T>> {

	// ------------------------------------------------------------------------
	
	public OutputEventChainFactory(String displayName) {
		super(displayName);
	}

	// ------------------------------------------------------------------------
	
	@Override
	public abstract boolean accepts(OutputEventChainDef def);

//	@Override
//	public abstract OutputEventChain<T> create(OutputEventChainDef def,
//			ObjectByDefRepository<OutputEventChainDef,?,OutputEventChain<T>> repository);

	// ------------------------------------------------------------------------
	
}
