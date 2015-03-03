package com.google.code.joto.eventrecorder.predicate;

import org.apache.commons.collections.Predicate;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.predicate.RecordEventSummaryPredicateUtils.AbstractRecordEventSummaryPredicate;

/**
 * default Predicate for RecordEventSummary with (optional) sub-predicate for
 * each field
 */
public class DefaultEventTypeRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	private Predicate/* <int> */eventIdPredicate;
	private Predicate/* <Date> */eventDatePredicate;
	private Predicate/* <String> */threadNamePredicate;
	private Predicate/* <String> */eventTypePredicate;
	private Predicate/* <String> */eventSubTypePredicate;
	private Predicate/* <String> */eventClassNamePredicate;
	private Predicate/* <String> */eventMethodNamePredicate;
	private Predicate/* <String> */eventMethodDetailPredicate;
	private Predicate/* <int> */correlatedEventIdPredicate;

	// ------------------------------------------------------------------------
	
	public DefaultEventTypeRecordEventSummaryPredicate() {
	}

	public DefaultEventTypeRecordEventSummaryPredicate(
			Predicate eventIdPredicate, Predicate eventDatePredicate,
			Predicate threadNamePredicate, Predicate eventTypePredicate,
			Predicate eventSubTypePredicate, Predicate eventClassNamePredicate,
			Predicate eventMethodNamePredicate,
			Predicate eventMethodDetailPredicate,
			Predicate correlatedEventIdPredicate) {
		super();
		this.eventIdPredicate = eventIdPredicate;
		this.eventDatePredicate = eventDatePredicate;
		this.threadNamePredicate = threadNamePredicate;
		this.eventTypePredicate = eventTypePredicate;
		this.eventSubTypePredicate = eventSubTypePredicate;
		this.eventClassNamePredicate = eventClassNamePredicate;
		this.eventMethodNamePredicate = eventMethodNamePredicate;
		this.eventMethodDetailPredicate = eventMethodDetailPredicate;
		this.correlatedEventIdPredicate = correlatedEventIdPredicate;
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean evaluate(Object obj) {
		if (obj == null || !(obj instanceof RecordEventSummary)) return false;
		else return evaluate((RecordEventSummary) obj);
	}
	
	@Override
	public boolean evaluate(RecordEventSummary evt) {
		if (eventIdPredicate != null
				&& !eventIdPredicate
						.evaluate(Integer.valueOf(evt.getEventId()))) {
			return false;
		}
		if (eventDatePredicate != null
				&& !eventDatePredicate.evaluate(evt.getEventDate())) {
			return false;
		}
		if (threadNamePredicate != null
				&& !threadNamePredicate.evaluate(evt.getThreadName())) {
			return false;
		}
		if (eventTypePredicate != null
				&& !eventTypePredicate.evaluate(evt.getEventType())) {
			return false;
		}
		if (eventSubTypePredicate != null
				&& !eventSubTypePredicate.evaluate(evt.getEventSubType())) {
			return false;
		}
		if (eventClassNamePredicate != null
				&& !eventClassNamePredicate.evaluate(evt.getEventClassName())) {
			return false;
		}
		if (eventMethodNamePredicate != null
				&& !eventMethodNamePredicate.evaluate(evt.getEventMethodName())) {
			return false;
		}
		if (eventMethodDetailPredicate != null
				&& !eventMethodDetailPredicate.evaluate(evt
						.getEventMethodDetail())) {
			return false;
		}
		if (correlatedEventIdPredicate != null
				&& !correlatedEventIdPredicate.evaluate(evt
						.getCorrelatedEventId())) {
			return false;
		}

		return true;
	}

	public Predicate getEventIdPredicate() {
		return eventIdPredicate;
	}

	public void setEventIdPredicate(Predicate p) {
		this.eventIdPredicate = p;
	}

	public Predicate getEventDatePredicate() {
		return eventDatePredicate;
	}

	public void setEventDatePredicate(Predicate p) {
		this.eventDatePredicate = p;
	}

	public Predicate getThreadNamePredicate() {
		return threadNamePredicate;
	}

	public void setThreadNamePredicate(Predicate p) {
		this.threadNamePredicate = p;
	}

	public Predicate getEventTypePredicate() {
		return eventTypePredicate;
	}

	public void setEventTypePredicate(Predicate p) {
		this.eventTypePredicate = p;
	}

	public Predicate getEventSubTypePredicate() {
		return eventSubTypePredicate;
	}

	public void setEventSubTypePredicate(Predicate p) {
		this.eventSubTypePredicate = p;
	}

	public Predicate getEventClassNamePredicate() {
		return eventClassNamePredicate;
	}

	public void setEventClassNamePredicate(Predicate p) {
		this.eventClassNamePredicate = p;
	}

	public Predicate getEventMethodNamePredicate() {
		return eventMethodNamePredicate;
	}

	public void setEventMethodNamePredicate(Predicate p) {
		this.eventMethodNamePredicate = p;
	}

	public Predicate getEventMethodDetailPredicate() {
		return eventMethodDetailPredicate;
	}

	public void setEventMethodDetailPredicate(Predicate p) {
		this.eventMethodDetailPredicate = p;
	}

	public Predicate getCorrelatedEventIdPredicate() {
		return correlatedEventIdPredicate;
	}

	public void setCorrelatedEventIdPredicate(Predicate p) {
		this.correlatedEventIdPredicate = p;
	}

}