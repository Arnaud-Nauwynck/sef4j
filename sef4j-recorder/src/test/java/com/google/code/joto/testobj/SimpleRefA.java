package com.google.code.joto.testobj;

import java.io.Serializable;

public class SimpleRefA implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	private B fieldB;
	
	// ------------------------------------------------------------------------
	
	public SimpleRefA() {
	}

	// ------------------------------------------------------------------------
	
	public B getFieldB() {
		return fieldB;
	}

	public void setFieldB(B fieldB) {
		this.fieldB = fieldB;
	}

	// ------------------------------------------------------------------------
	
	
}
