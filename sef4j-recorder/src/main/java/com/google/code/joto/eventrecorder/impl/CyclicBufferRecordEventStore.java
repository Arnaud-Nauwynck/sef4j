package com.google.code.joto.eventrecorder.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventStoreChange;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.TruncateRecordEventStoreEvent;

/**
 * in-memory cyclic implementation of RecordEventStore
 */
public class CyclicBufferRecordEventStore extends DefaultMemoryRecordEventStore {

	public static final int DEFAULT_MAX_EVENT_COUNT = 500;
	
	/** Factory pattern for RecordEventStore */
	public static class CyclicBufferRecordEventStoreFactory implements RecordEventStoreFactory {
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		int maxEventCount = DEFAULT_MAX_EVENT_COUNT;
		
		public CyclicBufferRecordEventStoreFactory() {
		}

		public CyclicBufferRecordEventStoreFactory(int maxEventCount) {
			this();
			this.maxEventCount = maxEventCount;
		}

		public RecordEventStore create() {
			return new CyclicBufferRecordEventStore();
		}
	}

	private int maxEventCount = DEFAULT_MAX_EVENT_COUNT;
	
	// ------------------------------------------------------------------------

	public CyclicBufferRecordEventStore() {
	}

	public CyclicBufferRecordEventStore(int maxEventCount) {
		this();
		this.maxEventCount = maxEventCount;
	}

	
	// ------------------------------------------------------------------------

	public int getMaxEventCount() {
		return maxEventCount;
	}

	public void setMaxEventCount(int p) {
		this.maxEventCount = p;
		checkNeedTruncate();
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public synchronized RecordEventData addEvent(RecordEventSummary eventInfo, Serializable objData) {
		RecordEventData eventData = doAddEvent(eventInfo, objData);
		
		AddRecordEventStoreEvent addEvent = new AddRecordEventStoreEvent(eventData);
		if (maxEventCount != -1 || eventDataList.size() < maxEventCount) {
			// no need truncate
			fireStoreEvent(addEvent);
		} else {
			// need truncate
			RecordEventData truncatedEvent = super.eventDataList.remove(0);
			int truncatedEventId = truncatedEvent.getEventId();
			List<RecordEventSummary> truncatedEvents = new ArrayList<RecordEventSummary>(1);
			truncatedEvents.add(truncatedEvent.getEventSummary());
			TruncateRecordEventStoreEvent truncEvent = onTruncateSetFirstEventId(truncatedEventId + 1, truncatedEvents);
			
			List<RecordEventStoreChange> firedEvents = new ArrayList<RecordEventStoreChange>(2);
			firedEvents.add(truncEvent);
			firedEvents.add(addEvent);
			fireStoreEvents(firedEvents);
		}
		return eventData;
	}

	// internal
	// ------------------------------------------------------------------------
	
	protected void checkNeedTruncate() {
		if (maxEventCount == -1 || eventDataList.size() < maxEventCount) {
			return;
		}
		// get truncated info
		int truncatedLen = eventDataList.size() - maxEventCount;
		List<RecordEventSummary> truncateEvents = eventDataListToEventHandleList(eventDataList.subList(0, truncatedLen));
		int fromEventId = truncateEvents.get(0).getEventId();
		int toEventId = truncateEvents.get(truncatedLen - 1).getEventId() +  1;
		
		// do truncate
		int checkTruncatedLen = eventDataList.truncateHeadForMaxRows(maxEventCount);
		assert checkTruncatedLen == truncatedLen;

		// fire truncated event
		fireStoreEvent(new TruncateRecordEventStoreEvent(fromEventId, toEventId, truncateEvents));
	}

}
