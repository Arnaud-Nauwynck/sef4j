package com.google.code.joto.testobj;

import java.io.Serializable;

public class SimpleRefBean implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;
	
	private int fieldId;
	private SimpleRefBean ref;

	// -------------------------------------------------------------------------
	
	public SimpleRefBean() {
	}

	// -------------------------------------------------------------------------

	public int getFieldId() {
		return fieldId;
	}

	public void setFieldId(int fieldId) {
		this.fieldId = fieldId;
	}

	public SimpleRefBean getRef() {
		return ref;
	}

	public void setRef(SimpleRefBean ref) {
		this.ref = ref;
	}
	
}
