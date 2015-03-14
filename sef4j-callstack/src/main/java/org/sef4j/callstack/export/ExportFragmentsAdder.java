package org.sef4j.callstack.export;

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
	public void putFragmentValue(Object key, T fragment);

	/**
	 * add a fragment with no associated natural id (not a metric?!)
	 * when failing to export ... this kind of fragment will be lost
	 * 
	 * @param fragment
	 */
	public void addNonIdentifiableFragment(T fragment);

	/** idem putFragmentValue(), for multiple elements */
	public void putAllFragmentValue(Map<Object,T> other);
	
	/** idem addNonIdentifiableFragment(), for multiple elements */
	public void addAllNonIdentifiableFragment(Collection<T> other);

}
