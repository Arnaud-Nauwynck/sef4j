package org.sef4j.core.helpers.exporters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * list of fragments ... split in 2 parts: fragments with id, and fragments without id
 * 
 * @param <T>
 */
public class ExportFragmentList<T> implements ExportFragmentsAdder<T> {

	private Map<Object,ExportFragment<T>> identifiableFragments;
	private List<ExportFragment<T>> nonIdentifiableFragments;
	
	// ------------------------------------------------------------------------

	public ExportFragmentList() {
		this(new HashMap<Object,ExportFragment<T>>(), new ArrayList<ExportFragment<T>>());
	}

	public ExportFragmentList(Map<Object,ExportFragment<T>> identifiableFragments, 
			List<ExportFragment<T>> nonIdentifiableFragments) {
		this.identifiableFragments = identifiableFragments;
		this.nonIdentifiableFragments = nonIdentifiableFragments;
	}

	// ------------------------------------------------------------------------

	public void copyTo(Collection<ExportFragment<T>> res) {
    	if (! identifiableFragments.isEmpty()) {
    		res.addAll(identifiableFragments.values());
    	}
    	if (! nonIdentifiableFragments.isEmpty()) {
    		res.addAll(nonIdentifiableFragments);
    	}
	}

	public List<ExportFragment<T>> copyToPriorityList() {
		List<ExportFragment<T>> res = new ArrayList<ExportFragment<T>>(size());
		Map<Integer,List<ExportFragment<T>>> tmpres = copyToPriorityListMap();
		for(List<ExportFragment<T>> prioList : tmpres.values()) {
			res.addAll(prioList);
		}
		return res;
	}
	
	/**
	 * useless when having MultiSet<> implementation (example in guava jar dependency)
	 * to compare non unique entry by descending priority 
	 * @return Map by descending priority
	 */
	public Map<Integer,List<ExportFragment<T>>> copyToPriorityListMap() {
		Map<Integer,List<ExportFragment<T>>> res = 
				new TreeMap<Integer,List<ExportFragment<T>>>(Collections.reverseOrder()); // sort descending by priority
    	if (! identifiableFragments.isEmpty()) {
    		ExportFragment.addAllToPriorityList(res, identifiableFragments.values());
    	}
    	if (! nonIdentifiableFragments.isEmpty()) {
    		ExportFragment.addAllToPriorityList(res, nonIdentifiableFragments);
    	}
    	return res;
	}

	public int size() {
		return identifiableFragments.size() + nonIdentifiableFragments.size();
	}

	public boolean isEmpty() {
		return identifiableFragments.isEmpty() && nonIdentifiableFragments.isEmpty();
	}

	public Map<Object, ExportFragment<T>> getIdentifiableFragments() {
		return identifiableFragments;
	}
	
	public List<ExportFragment<T>> getNonIdentifiableFragments() {
		return nonIdentifiableFragments;
	}

	public void addAll(ExportFragmentList<T> other) {
		identifiableFragments.putAll(other.identifiableFragments);
		nonIdentifiableFragments.addAll(other.nonIdentifiableFragments);
	}
	
	public List<T> toValues() {
		List<T> res = new ArrayList<T>();
		copyValuesTo(res);
		copyValuesTo(res);
		return res;
	}

	public void copyValuesTo(Collection<T> res) {
		ExportFragment.copyValuesTo(res, identifiableFragments.values());
		ExportFragment.copyValuesTo(res, nonIdentifiableFragments);
	}

	// implements ExportFragmentsAdder
	// ------------------------------------------------------------------------

	@Override
	public void putIdentifiableFragment(ExportFragmentsProvider<T> provider, Object id, T fragment, int priority) {
		ExportFragment<T> entry = new ExportFragment<T>(provider, id, fragment, priority);
		identifiableFragments.put(id, entry);
	}

	@Override
	public void addNonIdentifiableFragment(ExportFragmentsProvider<T> provider, T fragment, int priority) {
		ExportFragment<T> entry = new ExportFragment<T>(provider, null, fragment, priority);
		nonIdentifiableFragments.add(entry);
	}

	@Override
	public void putAllIdentifiableFragments(ExportFragmentsProvider<T> provider, Map<Object,T> fragments, int priority) {
		for(Map.Entry<Object,T> e : fragments.entrySet()) {
			Object id = e.getKey();
			ExportFragment<T> entry = new ExportFragment<T>(provider, id, e.getValue(), priority);
			identifiableFragments.put(id, entry);
		}
	}
	
	@Override
	public void addAllNonIdentifiableFragments(ExportFragmentsProvider<T> provider, Collection<T> fragments, int priority) {
		for(T fragment : fragments) {
			ExportFragment<T> entry = new ExportFragment<T>(provider, null, fragment, priority);
			nonIdentifiableFragments.add(entry);
		}
	}
	
	@Override
	public void addEntry(ExportFragment<T> entry) {
		Object id = entry.getId();
		if (id != null) {
			identifiableFragments.put(id, entry);
		} else {
			nonIdentifiableFragments.add(entry);
		}
	}

	@Override
	public void addAllEntries(Collection<ExportFragment<T>> entries) {
		for(ExportFragment<T> entry : entries) {
			addEntry(entry);
		}
	}


	// ------------------------------------------------------------------------
	
	@Override
	public String toString() {
		return "ExportFragmentList [" 
				+ identifiableFragments.size() + " value(s)"
				+ ((! nonIdentifiableFragments.isEmpty())? 
						" + " + nonIdentifiableFragments.size() + " elt(s) with no ids" : "")
				+ "]";
	}
	
}
