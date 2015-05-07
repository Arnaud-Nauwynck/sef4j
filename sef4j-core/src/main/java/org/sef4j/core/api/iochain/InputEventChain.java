package org.sef4j.core.api.iochain;

public abstract class InputEventChain {
	
	private final InputEventChainDef def;

	// ------------------------------------------------------------------------
	
	protected InputEventChain(InputEventChainDef def) {
		this.def = def;
	}

	// ------------------------------------------------------------------------

	public InputEventChainDef getDef() {
		return def;
	}
	
}
