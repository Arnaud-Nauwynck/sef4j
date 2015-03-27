package org.sef4j.core.helpers.exporters.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ExportFragment<T> {
	
	private final ExportFragmentsProvider<T> provider;
	
	/** if of fragment ... may be null for non-identifaible fragments */
	private final Object id;
	
	private T value;

	private int priority;
	
	
	// ------------------------------------------------------------------------

	public ExportFragment(ExportFragmentsProvider<T> provider, Object id, T value) {
		this(provider, id, value, 1);
	}
	
	public ExportFragment(ExportFragmentsProvider<T> provider, Object id, T value, int priority) {
		this.provider = provider;
		this.id = id;
		this.value = value;
		this.priority = priority;
	}

	// ------------------------------------------------------------------------
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public ExportFragmentsProvider<T> getProvider() {
		return provider;
	}

	public Object getId() {
		return id;
	}
	
	// static utilities
	// ------------------------------------------------------------------------

	public static <T> List<T> lsToValues(Collection<ExportFragment<T>> src) {
		List<T> res = new ArrayList<T>(src.size());
		copyValuesTo(res, src);
		return res;
	}
	
	public static <T> void copyValuesTo(Collection<T> res, Collection<ExportFragment<T>> src) {
		for(ExportFragment<T> e : src) {
			res.add(e.getValue());
		}
	}

	public static class ByDescPriorityComparator<T> implements Comparator<ExportFragment<T>> {
		private static final ByDescPriorityComparator<Object> INSTANCE = new ByDescPriorityComparator<Object>();
		@SuppressWarnings("unchecked")
		public static <T> ByDescPriorityComparator<T> instance() { return (ByDescPriorityComparator<T>) INSTANCE; }

		@Override
		public int compare(ExportFragment<T> o1, ExportFragment<T> o2) {
			int y = o1.getPriority();
			int x = o2.getPriority();
			// compare x <> y  (in descending order for o1 <> o2)
			// return Integer.compare(o2Prio, o2Prio); .. method since jdk7
	        return (x < y) ? -1 : ((x == y) ? 0 : 1);
	    }
		
	}
	
	public static <T> void addAllToPriorityList(Map<Integer,List<ExportFragment<T>>> dest, Collection<ExportFragment<T>> fragments) {
		for (ExportFragment<T> fragment : fragments) {
			addToPriorityList(dest, fragment);
		}
	}
	
	public static <T> void addToPriorityList(Map<Integer,List<ExportFragment<T>>> dest, ExportFragment<T> fragment) {
		int priority = fragment.getPriority();
		List<ExportFragment<T>> prioList = dest.get(priority);
		if (prioList == null) {
			prioList = new ArrayList<ExportFragment<T>>();
			dest.put(priority, prioList);
		}
		prioList.add(fragment);
	}

}