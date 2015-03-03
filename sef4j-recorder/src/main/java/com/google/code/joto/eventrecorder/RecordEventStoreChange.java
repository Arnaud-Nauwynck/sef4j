package com.google.code.joto.eventrecorder;

import java.io.Serializable;
import java.util.List;

/**
 * abstract class for all events occuring on a RecordEventStore
 * This includes:
 * <ul>
 * <li>AddEvent (for user defined/recorded event</li>
 * <li>TruncateEvents (purge)</li>
 * <li>StartRecording</li>
 * <li>StopRecoding</li>
 * ...
 * </ul>
 */
public abstract class RecordEventStoreChange implements Serializable {
	
	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	public abstract void accept(RecordEventChangeVisitor visitor);
	
	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class AddRecordEventStoreEvent extends RecordEventStoreChange {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private RecordEventSummary eventSummary;

		/** optionnally set... recall eventStore.getEventData() if needing it */
		private RecordEventData eventData; 

		public AddRecordEventStoreEvent(RecordEventSummary event) {
			super();
			this.eventSummary = event;
		}

		public AddRecordEventStoreEvent(RecordEventData eventData) {
			this(eventData.getEventSummary());
			this.eventData = eventData;
		}

		public void accept(RecordEventChangeVisitor visitor) {
			visitor.caseAddEvent(this);
		}
		
		public RecordEventSummary getEventSummary() {
			return eventSummary;
		}

		public RecordEventData getEventData(RecordEventStore eventStore) {
			if (eventData == null) {
				eventData = eventStore.getEventData(eventSummary);
			}
			return eventData;
		}
		
	}

	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class TruncateRecordEventStoreEvent extends RecordEventStoreChange {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private final int fromEventId; 
		private final int toEventId; // exclusive

		/** optionnally set... */
		private List<RecordEventSummary> optTruncateEventSummaries;

		public TruncateRecordEventStoreEvent(int fromEventId, int toEventId, List<RecordEventSummary> optTruncateEventSummaries) {
			this.fromEventId = fromEventId;
			this.toEventId = toEventId;
			this.optTruncateEventSummaries = optTruncateEventSummaries;
		}

		public void accept(RecordEventChangeVisitor visitor) {
			visitor.caseTruncateEvent(this);
		}
		
		public int getFromEventId() {
			return fromEventId;
		}

		public int getToEventId() {
			return toEventId;
		}

		public List<RecordEventSummary> getOptTruncateEventSummaries() {
			return optTruncateEventSummaries;
		}
		
	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class StartRecordingEvent extends RecordEventStoreChange {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		public StartRecordingEvent() {
		}

		public void accept(RecordEventChangeVisitor visitor) {
			visitor.caseStartRecording(this);
		}

	}
	
	// -------------------------------------------------------------------------
	
	/**
	 * 
	 */
	public static class StopRecordingEvent extends RecordEventStoreChange {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		public StopRecordingEvent() {
		}

		public void accept(RecordEventChangeVisitor visitor) {
			visitor.caseStopRecording(this);
		}

	}
	
}