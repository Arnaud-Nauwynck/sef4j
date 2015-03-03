package com.google.code.joto.eventrecorder.ext.calls;

import java.util.Date;

/**
 * a simple interface, for JUnit tests.
 * 
 * contains many methods with combinations of primitive/objects/arrays
 * as paramters or return type
 */
public interface IFoo {

	String methSimple(String str, int i);

	void methVoid();
	void methVoid(int i);
	void methVoidFromPrimitives(int i, float f, double d, char c, boolean b, byte byt);
	void methVoidFromPrimitivesArrays(int[] ints, float[] fs, double[] ds, char[] cs, boolean[] bs, byte[] byts);
	void methVoidFromObj(Object obj, String str, Date date, Integer i, Float f, Double d);
	void methVoidFromObjArrays(Object[] obj, String[] str, Date[] dates, Integer[] i, Float[] f, Double[] d);
	
	int methInt();
	float methFloat();
	double methDouble();

	Object methObj();
	String methString();
	
	Object[] methObjArray();
	int[] methIntArray();
	float[] methFloatArray();
	String[] methStringArray();
	
}
