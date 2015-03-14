package org.sef4j.callstack.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * list of fragments ... split in 2 parts: fragments with id, and fragments without id
 * 
 * @param <T>
 */
public class ExportFragmentList<T> implements ExportFragmentsAdder<T> {

	private Map<Object,T> fragmentIdValues;
	private List<T> nonIdentifiableFragments;
	
	// ------------------------------------------------------------------------

	public ExportFragmentList() {
		this(new HashMap<Object,T>(), new ArrayList<T>());
	}

	public ExportFragmentList(Map<Object,T> fragmentIdValues, List<T> nonIdentifiableFragments) {
		this.fragmentIdValues = fragmentIdValues;
		this.nonIdentifiableFragments = nonIdentifiableFragments;
	}

	// ------------------------------------------------------------------------

	public List<T> copyToList() {
    	List<T> flatList = new ArrayList<T>(size());
    	if (! fragmentIdValues.isEmpty()) {
    		flatList.addAll(fragmentIdValues.values());
    	}
    	if (! nonIdentifiableFragments.isEmpty()) {
    		flatList.addAll(nonIdentifiableFragments);
    	}
    	return flatList;
	}
	
	public int size() {
		return fragmentIdValues.size() + nonIdentifiableFragments.size();
	}

	public boolean isEmpty() {
		return fragmentIdValues.isEmpty() && nonIdentifiableFragments.isEmpty();
	}

	public Map<Object, T> getFragmentIdValues() {
		return fragmentIdValues;
	}
	
	public List<T> getNonIdentifiableFragments() {
		return nonIdentifiableFragments;
	}
	
	public void addAll(ExportFragmentList<T> other) {
		fragmentIdValues.putAll(other.fragmentIdValues);
		nonIdentifiableFragments.addAll(other.nonIdentifiableFragments);
	}

	// implements ExportFragmentsAdder
	// ------------------------------------------------------------------------
	
	public void putFragmentValue(Object key, T fragment) {
		fragmentIdValues.put(key, fragment);
	}

	public void addNonIdentifiableFragment(T fragment) {
		nonIdentifiableFragments.add(fragment);
	}

	public void putAllFragmentValue(Map<Object,T> other) {
		fragmentIdValues.putAll(other);
	}
	
	public void addAllNonIdentifiableFragment(Collection<T> other) {
		nonIdentifiableFragments.addAll(other);
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "ExportFragmentList [" 
				+ fragmentIdValues.size() + " value(s)"
				+ ((! nonIdentifiableFragments.isEmpty())? 
						" + " + nonIdentifiableFragments.size() + " elt(s) with no ids" : "")
				+ "]";
	}
	
}
