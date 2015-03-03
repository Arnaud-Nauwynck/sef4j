package com.google.code.joto.testobj;

public class NonSerializableA {

	private int fieldInt;
	private NonSerializableB b;
	
	// -------------------------------------------------------------------------
	
	public NonSerializableA() {
	}

	// -------------------------------------------------------------------------
	
	public int getFieldInt() {
		return fieldInt;
	}

	public void setFieldInt(int p) {
		this.fieldInt = p;
	}

	public NonSerializableB getB() {
		return b;
	}

	public void setB(NonSerializableB b) {
		this.b = b;
	}
	
	
	
}
