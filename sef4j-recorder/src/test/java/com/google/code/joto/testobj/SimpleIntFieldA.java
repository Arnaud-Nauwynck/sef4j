package com.google.code.joto.testobj;

import java.io.Serializable;

public class SimpleIntFieldA implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;
	
	private int fieldInt1;
	
	public SimpleIntFieldA() {
	}

	public int getFieldInt1() {
		return fieldInt1;
	}

	public void setFieldInt1(int fieldInt1) {
		this.fieldInt1 = fieldInt1;
	}
	
	
}
