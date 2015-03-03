package com.google.code.joto.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.collections.iterators.IteratorEnumeration;

/**
 * a (simplified) Map, with data duplicated as a fast read-only List, for sequential and indexed traversal 
 *
 */
public class SortedListTreeMap<K,V> {

	private TreeMap<K,V> mapData = new TreeMap<K,V>();

	private List<V> listData = null; // = Collections.unmodifiableList(new ArrayList<V>());
	
	// ------------------------------------------------------------------------
	
	public SortedListTreeMap() {
	}
	
	// ------------------------------------------------------------------------

	private void setDirtyListData() {
		listData = null;
	}

	private void checkCleanListData() {
		if (listData == null) {
			listData = Collections.unmodifiableList(new ArrayList<V>(mapData.values()));
		}
	}

	public List<V> valuesAsList() {
		checkCleanListData();
		return listData;
	}

	@SuppressWarnings("unchecked")
	public Enumeration<V> enumeration() {
		checkCleanListData();
		return new IteratorEnumeration(listData.iterator());
	}
	
	public V getAt(int index) {
		checkCleanListData();
		try {
			return listData.get(index);	
		} catch(IndexOutOfBoundsException ex) {
			throw ex; // should not occur!!
		}
	}
	
	public int indexOf(Object obj) {
		checkCleanListData();
		return listData.indexOf(obj);
	}
	
	// ------------------------------------------------------------------------
	
	public int size() {
		return mapData.size();
	}

	public V get(Object key) {
		return mapData.get(key);
	}

	public V put(K key, V value) {
		V res = mapData.put(key, value);
		setDirtyListData();
		return res;
	}

	public V remove(K key) {
		V res = mapData.remove(key);
		setDirtyListData();
		return res;
	}
	
}
