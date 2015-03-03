package com.google.code.joto.eventrecorder.ext.calls;

import java.util.Date;

public class DefaultFoo implements IFoo {

	//-------------------------------------------------------------------------

	public DefaultFoo() {
	}

	//-------------------------------------------------------------------------
	
	public String methSimple(String str, int i) {
		return str + i;
	}

	public void methVoid() {
	}

	public void methVoid(int i){
	}

	public void methVoidFromPrimitives(int i, float f, double d, char c, boolean b, byte byt) {
	}

	public void methVoidFromPrimitivesArrays(int[] ints, float[] fs, double[] ds, char[] cs, boolean[] bs, byte[] byts) {
	}

	public void methVoidFromObj(Object obj, String str, Date date, Integer i, Float f, Double d) {
	}

	public void methVoidFromObjArrays(Object[] obj, String[] str, Date[] dates, Integer[] i, Float[] f, Double[] d) {
	}
	
	public Object methObj() {
		return "a"; 
	}
	
	public int methInt() {
		return 1;
	}

	public float methFloat() {
		return 1.0f;
	}

	public double methDouble() {
		return 1.5;
	}

	public String methString() {
		return "test";
	}
	
	public Object[] methObjArray() {
		Object[] res = new Object[2];
		res[0] = "a";
		res[1] = new Integer(1);
		return res;
	}
	public int[] methIntArray() {
		int[] res = new int[2];
		res[0] = 1;
		res[1] = 2;
		return res;
	}
	
	public float[] methFloatArray() {
		float[] res = new float[2];
		res[0] = 1.0f;
		res[1] = 2.5f;
		return res;
	}

	public String[] methStringArray() {
		String[] res = new String[2];
		res[0] = "test1";
		res[1] = "test2";
		return res;
	}
	
}
