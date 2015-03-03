package com.google.code.joto.ui.table;

import java.util.Iterator;
import java.util.List;

import com.google.code.joto.eventrecorder.DefaultVisitorRecordEventListener;
import com.google.code.joto.eventrecorder.RecordEventChangeVisitor;
import com.google.code.joto.eventrecorder.RecordEventListener;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.StartRecordingEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.StopRecordingEvent;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.TruncateRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.util.ArrayList2;

/**
 *
 */
public class RecordEventStoreTableModel extends DefaultRecordEventTableModel {

	/** */
	private static final long serialVersionUID = 1L;
	
	private RecordEventStore eventStore;
		
	private int maxEventRows = -1;
	
	private RecordEventChangeVisitor eventHandler = new InnerRecordEventChangeVisitor();
	private RecordEventListener eventListener = new DefaultVisitorRecordEventListener(eventHandler);
	
	//-------------------------------------------------------------------------

	public RecordEventStoreTableModel(RecordEventStore eventStore) {
		this.eventStore = eventStore;
		// TODO should check to wrap listener for SwingUtilities.invokeLater?
		int availableFirstEventId = eventStore.getFirstEventId();
		int availableLastEventId = eventStore.getLastEventId();
		int len = availableLastEventId - availableFirstEventId;
		int requestFromEventId = availableFirstEventId; 
		if (maxEventRows != -1 && len > maxEventRows) {
			len = maxEventRows;
			requestFromEventId = availableLastEventId - len; 
		}
		reloadEventRows(requestFromEventId, -1);
		eventStore.addRecordEventListener(eventListener);
	}

	//-------------------------------------------------------------------------

	/** callback from inner eventStore listener */
	private void onAddEvent(AddRecordEventStoreEvent p) {
		addEventRow(p.getEventSummary());
	}

	
	
	/** callback from inner eventStore listener */
	private void onEventStoreTruncate(TruncateRecordEventStoreEvent p) {
		List<RecordEventSummary> optTruncateEventSummaries = p.getOptTruncateEventSummaries();
		if (optTruncateEventSummaries != null) {
			removeRows(optTruncateEventSummaries);
		} else {
			// truncated events not available... scan from range eventId
			int fromEventId = p.getFromEventId();
			int toEventId = p.getFromEventId();
			List<RecordEventSummary> eventRows = super.getEventRows();
			for (Iterator<RecordEventSummary> iter = eventRows.iterator(); iter.hasNext();) {
				RecordEventSummary e = iter.next();
				int eId = e.getEventId();
				if (eId < fromEventId) {
					// strange?.. should have been removed already...
					iter.remove();
				} else if (fromEventId <= eId && eId < toEventId) {
					iter.remove();
				} else {
					// reached end of truncation
					break;
				}
			}
		}
		fireTableDataChanged();
	}

	// -------------------------------------------------------------------------

	
	public int getMaxEventRows() {
		return maxEventRows;
	}

	public void setMaxEventRows(int p) {
		this.maxEventRows = p;
		super.truncateEventRows(maxEventRows);
	}
	
	public void reloadEventRows(int fromEventId, int toEventId) {
		// TODO use SwingWorker ... 
		List<RecordEventSummary> events = eventStore.getEvents(fromEventId, toEventId);
		ArrayList2<RecordEventSummary> tmpNewRows = new ArrayList2<RecordEventSummary>(); 
		tmpNewRows.addAll(events);
		tmpNewRows.truncateHeadForMaxRows(maxEventRows);
		super.setRowElts(tmpNewRows);
	}


	// -------------------------------------------------------------------------
	
	private class InnerRecordEventChangeVisitor implements RecordEventChangeVisitor {

		public void caseAddEvent(AddRecordEventStoreEvent p) {
			onAddEvent(p);
		}

		public void caseTruncateEvent(TruncateRecordEventStoreEvent p) {
			onEventStoreTruncate(p);
		}

		@Override
		public void caseStartRecording(StartRecordingEvent p) {
			// do nothing?
		}

		@Override
		public void caseStopRecording(StopRecordingEvent p) {
			// do nothing?
		}
		
	}

}
