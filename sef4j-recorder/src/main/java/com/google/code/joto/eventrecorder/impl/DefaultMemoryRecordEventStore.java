package com.google.code.joto.eventrecorder.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.TruncateRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.util.ArrayList2;

/**
 * in-memory implementation of RecordEventStore
 */
public class DefaultMemoryRecordEventStore extends AbstractRecordEventStore {
	

	/** Factory pattern for RecordEventStore */
	public static class DefaultMemoryRecordEventStoreFactory implements RecordEventStoreFactory {
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		public DefaultMemoryRecordEventStoreFactory() {
		}

		public RecordEventStore create() {
			return new DefaultMemoryRecordEventStore();
		}
	}
	
	protected ArrayList2<RecordEventData> eventDataList = new ArrayList2<RecordEventData>();
	
	// ------------------------------------------------------------------------

	public DefaultMemoryRecordEventStore() {
	}

	// ------------------------------------------------------------------------

	@Override
	public List<RecordEventSummary> getEvents(int fromEventId, int toEventId) {
		List<RecordEventSummary> res = new ArrayList<RecordEventSummary>();
		int availableFirstEventId = getFirstEventId();
		if (fromEventId < availableFirstEventId) {
			fromEventId = availableFirstEventId;
		}
		int availableLastEventId = getLastEventId();
		if (toEventId == -1 || toEventId  >= availableLastEventId) {
			toEventId = availableLastEventId;
		}
		for(RecordEventData e : eventDataList) {
			if (e.getEventId() < fromEventId) {
				continue;
			}
			if (toEventId != -1 && e.getEventId() > toEventId) {
				break;
			}
			res.add(e.getEventSummary());
		}
		return res;
	}

	public synchronized List<RecordEventSummary> getEvents() {
		List<RecordEventSummary> res = eventDataListToEventHandleList(eventDataList);
		return res;
	}

	public synchronized RecordEventData getEventData(RecordEventSummary eventHandle) {
		if (eventDataList.isEmpty()) {
			return null;
		}
		int eventId = eventHandle.getEventId();
		int firstEventId = eventDataList.get(0).getEventId(); 
		int index = eventId - firstEventId;
		RecordEventData res;
		if (index < eventDataList.size()) {
			res = eventDataList.get(index);
		} else {
			// Should not occur!
			res = null;
		}
		return res;
	}

	
	protected RecordEventData doAddEvent(RecordEventSummary eventInfo, Serializable objData) {
		RecordEventData eventData = createNewEventData(eventInfo, objData);
		eventDataList.add(eventData);
		return eventData;
	}


	@Override
	public void purgeEvents(int toEventId) {
		int firstEventId = getFirstEventId();
		if (toEventId == -1) {
			toEventId = getLastEventId();
		}
		int truncatedLen = toEventId - firstEventId;
		
		List<RecordEventSummary> truncateEvents = eventDataListToEventHandleList(eventDataList.subList(0, truncatedLen));
		
		// do truncate
		eventDataList.removeRange(0, truncatedLen);

		// fire truncated event
		fireStoreEvent(new TruncateRecordEventStoreEvent(firstEventId, toEventId, truncateEvents));
	}

	// override java.lang.Object
	// -------------------------------------------------------------------------

	@Override
	public String toString() {
		return "DefaultMemoryRecordEventStore[" 
			+ "eventIds:" + getFirstEventId() + "-" + getLastEventId()
			+ "]";
	}
	
}
