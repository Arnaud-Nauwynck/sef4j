package org.sef4j.core.helpers.export;

import java.util.Collection;
import java.util.Map;


/**
 * callback to give to ExportFragmentsProvider(s) to ask then for fragments
 *
 * @param <T>
 */
public interface ExportFragmentsAdder<T> {
	
	/**
	 * add a fragment with an associated natural id... 
	 * when failing to export, new fragment values with same key will override previous value
	 * 
	 * @param key
	 * @param fragment
	 */
	public void putIdentifiableFragment(ExportFragmentsProvider<T> provider, Object key, T fragment, int priority);

	/**
	 * add a fragment with no associated natural id (not a metric?!)
	 * when failing to export ... this kind of fragment will be lost
	 * 
	 * @param fragment
	 */
	public void addNonIdentifiableFragment(ExportFragmentsProvider<T> provider, T fragment, int priority);

	/** idem putFragmentValue(), for multiple elements */
	public void putAllIdentifiableFragments(ExportFragmentsProvider<T> provider, Map<Object,T> other, int priority);
	
	/** idem addNonIdentifiableFragment(), for multiple elements */
	public void addAllNonIdentifiableFragments(ExportFragmentsProvider<T> provider, Collection<T> other, int priority);

	/** add either identifiable or nonIdentifiable fragment */
	public void addEntry(ExportFragment<T> entry);
	
	/** idem addEntry(), for multiple elements  */
	public void addAllEntries(Collection<ExportFragment<T>> entries);

}
