package com.google.code.joto.ui.table;

import java.util.List;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.util.ArrayList2;

/**
 * swing TableModel implementation for List<RecordEventSummary>
 */
public class DefaultRecordEventTableModel extends AbstractRecordEventTableModel {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	/** in-memory event rows ... similar to DefaultTableModel.dataVector  */
	private ArrayList2<RecordEventSummary> eventRows = new ArrayList2<RecordEventSummary>();

	// ------------------------------------------------------------------------

	public DefaultRecordEventTableModel() {
	}
	
	// ------------------------------------------------------------------------
	
	public List<RecordEventSummary> getEventRows() {
		return eventRows;
	}

	public RecordEventSummary getEventRow(int row) {
		return (RecordEventSummary) eventRows.get(row);
	}

	/** implements TableModel */
	public int getRowCount() {
		return eventRows.size();
	}

	// ------------------------------------------------------------------------
	
	public void addEventRow(RecordEventSummary row) {
		int index = eventRows.size(); 
		eventRows.add(index, row); 
	    fireTableRowsInserted(index, index);
	}
	
	public void addEventRows(List<RecordEventSummary> rows) {
		int index = eventRows.size();
		if (!rows.isEmpty()) {
			eventRows.addAll(index, rows);
			int lastRow = eventRows.size(); // index + rows.size() - 1
		    fireTableRowsInserted(index, lastRow);
		}
	}
	
	public void removeRows(List<RecordEventSummary> rows) {
		eventRows.removeAll(rows);
		fireTableDataChanged();
	}

	public void clearEventRows() {
		eventRows.clear();
		fireTableDataChanged();
	}

	public void setRowElts(List<RecordEventSummary> elts) {
		eventRows.clear();
		eventRows.addAll(elts);
		fireTableDataChanged();
	}

	public void truncateEventRows(int maxEventRows) {
		int truncatedLen = eventRows.truncateHeadForMaxRows(maxEventRows);
		if (truncatedLen != 0) {
			fireTableRowsDeleted(0, truncatedLen);
		}
	}

	public int findEventRowIndex(RecordEventSummary row) {
		return eventRows.indexOf(row);
	}

	public void updateRowAt(int index, RecordEventSummary row) {
		eventRows.set(index, row);
		fireTableRowsUpdated(index, index);		
	}

}
