package com.google.code.joto.testobj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class A implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	private boolean fieldBoolean;
	private boolean fieldBoolean2;
	private int fieldInt;
	private long fieldLong;
	private double fieldDouble;
	private String fieldString;

	private B fieldB;
	private B fieldB2;
	private B fieldBC;
	private List<B> fieldBList = new ArrayList<B>();
	private List<B> fieldBList2 = fieldBList;
	private Set<B> fieldBSet = new HashSet<B>();
	private Map<String,B> fieldBMap = new HashMap<String,B>();
	
	public A() {
	}

	public boolean isFieldBoolean() {
		return fieldBoolean;
	}

	public void setFieldBoolean(boolean fieldBoolean) {
		this.fieldBoolean = fieldBoolean;
	}

	public boolean isFieldBoolean2() {
		return fieldBoolean2;
	}

	public void setFieldBoolean2(boolean p) {
		this.fieldBoolean2 = p;
	}

	public int getFieldInt() {
		return fieldInt;
	}

	public void setFieldInt(int fieldInt) {
		this.fieldInt = fieldInt;
	}

	public long getFieldLong() {
		return fieldLong;
	}

	public void setFieldLong(long fieldLong) {
		this.fieldLong = fieldLong;
	}

	public double getFieldDouble() {
		return fieldDouble;
	}

	public void setFieldDouble(double fieldDouble) {
		this.fieldDouble = fieldDouble;
	}

	public String getFieldString() {
		return fieldString;
	}

	public void setFieldString(String fieldString) {
		this.fieldString = fieldString;
	}

	public B getFieldB() {
		return fieldB;
	}

	public void setFieldB(B fieldB) {
		this.fieldB = fieldB;
	}

	public B getFieldB2() {
		return fieldB2;
	}

	public void setFieldB2(B p) {
		this.fieldB2 = p;
	}

	public B getFieldBC() {
		return fieldBC;
	}

	public void setFieldBC(B p) {
		this.fieldBC = p;
	}

	public List<B> getFieldBList() {
		return fieldBList;
	}

	public void setFieldBList(List<B> fieldBList) {
		this.fieldBList = fieldBList;
	}

	public List<B> getFieldBList2() {
		return fieldBList2;
	}

	public void setFieldBList2(List<B> p) {
		this.fieldBList = p;
	}

	public void addFieldBSet(B p) {
		fieldBSet.add(p);
	}

	public void putFieldBMap(String key, B p) {
		fieldBMap.put(key, p);
	}

	public Set<B> getFieldBSet() {
		return fieldBSet;
	}

	public void setFieldBSet(Set<B> p) {
		this.fieldBSet = p;
	}

	public Map<String, B> getFieldBMap() {
		return fieldBMap;
	}

	public void setFieldBMap(Map<String, B> p) {
		this.fieldBMap = p;
	}
	
}
