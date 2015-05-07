package org.sef4j.core.api.iochain;

public abstract class InputEventChainFactory {

	public abstract boolean accepts(InputEventChainDef def);
	
	public abstract InputEventChain create(InputEventChainDef def);
	
}
