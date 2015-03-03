package com.google.code.joto.ui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 * swing TableModel implementation for List<RecordEventSummary>
 */
public abstract class AbstractRecordEventTableModel extends AbstractTableModel {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	// ------------------------------------------------------------------------

	public AbstractRecordEventTableModel() {
	}
	
	// ------------------------------------------------------------------------
	
//	public abstract List<RecordEventSummary> getEventRows();

	public abstract RecordEventSummary getEventRow(int row);
	
	public List<RecordEventSummary> getEventRowsSubList(int fromIndex, int toIndex) {
		List<RecordEventSummary> res = new ArrayList<RecordEventSummary>();
		for (int i = fromIndex; i <= toIndex; i++) {
			res.add(getEventRow(i));
		}
		return res;
	}

	
	/** implements TableModel */
	public int getColumnCount() {
		return 7;
	}

	/** implements TableModel */
	public Object getValueAt(int rowIndex, int columnIndex) {
		RecordEventSummary rowEvent = getEventRow(rowIndex);
		switch(columnIndex) {
		case 0: return rowEvent.getEventId();
		case 1: return rowEvent.getEventDate();
		case 2: return rowEvent.getEventType();
		case 3: return rowEvent.getEventSubType();
		case 4: return rowEvent.getEventClassName();
		case 5: return rowEvent.getEventMethodName();
		case 6: return rowEvent.getEventMethodDetail();
		case 7: return rowEvent.getInternalEventStoreDataAddress();
		default: return null;
		}
	}

}
