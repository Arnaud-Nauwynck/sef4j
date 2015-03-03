package com.google.code.joto.eventrecorder.predicate;

import org.apache.commons.collections.Predicate;

import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 * interface for filtering RecordEventSummary
 * sub-interface of commons-collections Predicate interface
 *
 */
public interface RecordEventSummaryPredicate extends Predicate {

	/**
	 * redundant with org.apache.commons.collections.Predicate, but type safe (?!!)
	 * @param object
	 * @return
	 */
	public boolean evaluate(RecordEventSummary object);
	
}
