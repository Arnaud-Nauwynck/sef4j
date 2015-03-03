package com.google.code.joto.testobj;


public class C extends B {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	int fieldInt2;

	public C() {
		super();
	}

	public int getFieldInt2() {
		return fieldInt2;
	}

	public void setFieldInt(int p) {
		this.fieldInt2 = p;
	}
	
}
