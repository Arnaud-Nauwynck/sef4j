package com.google.code.joto.eventrecorder.writer;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 * callback interface for asynchronous write to 
 * "RecordEventWriter.addEvent(RecordEventSummary info, Serializable objData, RecordEventWriterCallback callback);" 
 *
 */
public interface RecordEventWriterCallback {

	public void onStore(RecordEventData stored);
	
	// -------------------------------------------------------------------------

	/**
	 * default implemetation of RecordEventWriterCallback 
	 * for setting setCorrelatedEventId() on <code>eventToFill</code> object to fill
	 */
	public static class CorrelatedEventSetterCallback implements RecordEventWriterCallback {

		private RecordEventSummary eventToFill;
		
		public CorrelatedEventSetterCallback(RecordEventSummary eventToFill) {
			super();
			this.eventToFill = eventToFill;
		}

		@Override
		public void onStore(RecordEventData event) {
			eventToFill.setCorrelatedEventId(event.getEventId());
		}
		
	}
}
