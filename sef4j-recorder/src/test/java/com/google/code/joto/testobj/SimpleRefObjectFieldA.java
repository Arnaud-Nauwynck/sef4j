package com.google.code.joto.testobj;

import java.io.Serializable;

public class SimpleRefObjectFieldA implements Serializable {
	
	/** */
	private static final long serialVersionUID = 1L;
	
	private Object fieldObj;
	
	public SimpleRefObjectFieldA() {
	}

	public Object getFieldObj() {
		return fieldObj;
	}

	public void setFieldObj(Object p) {
		this.fieldObj = p;
	}
	
	
}
