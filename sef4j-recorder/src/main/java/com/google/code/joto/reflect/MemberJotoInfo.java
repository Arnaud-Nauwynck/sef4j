package com.google.code.joto.reflect;

/**
 *
 */
public abstract class MemberJotoInfo {

	protected final ClassJotoInfo parent;

	// -------------------------------------------------------------------------
	
	public MemberJotoInfo(ClassJotoInfo parent) {
		super();
		this.parent = parent;
	}

	// -------------------------------------------------------------------------

	public ClassJotoInfo getParent() {
		return parent;
	}
	
	
	
}
