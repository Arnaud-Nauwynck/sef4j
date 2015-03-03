package com.google.code.joto.testobj;

import java.io.Serializable;

public class B implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	int fieldInt;

	public B() {
		super();
	}

	public int getFieldInt() {
		return fieldInt;
	}

	public void setFieldInt(int fieldInt) {
		this.fieldInt = fieldInt;
	}
	
}
