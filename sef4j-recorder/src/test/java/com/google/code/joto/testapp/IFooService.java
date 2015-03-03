package com.google.code.joto.testapp;

public interface IFooService {

	public void foo();
	
	public int methInt(int arg1, int arg2);

	public double methDouble(double arg1, double arg2);

	public java.util.Date methDate(java.util.Date arg1, int offset);

	public Object methObj(Object obj);

}
