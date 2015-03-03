package com.google.code.joto.testobj;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class Pt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int x;
	private int y;
	
	@ConstructorProperties({ "x", "y" })
	public Pt(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
}
