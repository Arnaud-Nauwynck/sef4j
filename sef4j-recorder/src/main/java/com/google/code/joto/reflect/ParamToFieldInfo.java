package com.google.code.joto.reflect;

import java.lang.reflect.Field;

public class ParamToFieldInfo {

	private final Class<?> parameterType;
	private Field targetAssignedField;

	// -------------------------------------------------------------------------
	
	public ParamToFieldInfo(Class<?> parameterType, Field targetAssignedField) {
		super();
		this.parameterType = parameterType;
		this.targetAssignedField = targetAssignedField;
	}

	// -------------------------------------------------------------------------

	public Class<?> getParameterType() {
		return parameterType;
	}

	public Field getTargetAssignedField() {
		return targetAssignedField;
	}

	/*pp*/ void setTargetAssignedField(Field targetAssignedField) {
		this.targetAssignedField = targetAssignedField;
	}
	
}
