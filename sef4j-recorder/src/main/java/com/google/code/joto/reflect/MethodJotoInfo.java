package com.google.code.joto.reflect;

import java.lang.reflect.Method;

/**
 *
 */
public class MethodJotoInfo extends MemberJotoInfo {

	private final Method targetMethod;

	// -------------------------------------------------------------------------
	
	public MethodJotoInfo(ClassJotoInfo parent, Method targetMethod) {
		super(parent);
		this.targetMethod = targetMethod;
	}
	
	// -------------------------------------------------------------------------

	public Method getTargetMethod() {
		return targetMethod;
	}
	
	
}
