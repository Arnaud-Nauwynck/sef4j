package com.google.code.joto.ui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.RecordEventSummary;

/**
 * a working copy of a  TableModel, synchronized for icnoming events, but with 
 */
public class SubSelectionRecordEventTableModel extends DefaultRecordEventTableModel {
	
	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(SubSelectionRecordEventTableModel.class);
	
	private boolean syncToUnderlying = true;
	private AbstractRecordEventTableModel underlyingModel;
	private TableModelListener innerTableModelListener;
	
	// ------------------------------------------------------------------------
	
	public SubSelectionRecordEventTableModel(AbstractRecordEventTableModel underlyingModel) {
		super();
		this.underlyingModel = underlyingModel;
		this.innerTableModelListener = new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				onUnderlyingTableChanged(e);
			}
		};
		underlyingModel.addTableModelListener(innerTableModelListener);
	}
	
	// ------------------------------------------------------------------------
	

	public boolean isSyncToUnderlying() {
		return syncToUnderlying;
	}

	public void setSyncToUnderlying(boolean p) {
		this.syncToUnderlying = p;
	}

	public void reloadAllEvents() {
		super.clearEventRows();
		List<RecordEventSummary> rows = underlyingModel.getEventRowsSubList(0, underlyingModel.getRowCount()-1);
		super.setRowElts(rows);
	}

	public void reloadNthLastEvents(int n) {
		super.clearEventRows();
		int underlyingRowLen = underlyingModel.getRowCount();
		int underlyingFirstRow = (n >= underlyingRowLen)? 0 : (underlyingRowLen-n);   
		List<RecordEventSummary> rows = underlyingModel.getEventRowsSubList(underlyingFirstRow, underlyingRowLen-1);
		super.setRowElts(rows);
	}


	// ------------------------------------------------------------------------

	private void onUnderlyingTableChanged(TableModelEvent e) {
		if (!syncToUnderlying) {
			return;
		}
		int underlyingModelLen = underlyingModel.getRowCount();
		int firstRow = e.getFirstRow();
		int lastRow = e.getLastRow();
		switch(e.getType()) {
		case TableModelEvent.INSERT: {
			if (firstRow == lastRow && lastRow < underlyingModelLen) {
				// standard case: added 1 elt
				RecordEventSummary addedRow = underlyingModel.getEventRow(lastRow);
				super.addEventRow(addedRow);
			} else if (firstRow >= 0 && lastRow < underlyingModelLen) {
				// append multiple events
				List<RecordEventSummary> addedRows = new ArrayList<RecordEventSummary>();
				for (int i = firstRow; i <= lastRow; i++) {
					addedRows.add(underlyingModel.getEventRow(i));
				}
				super.addEventRows(addedRows);
			} else {
				log.warn("non standard INSERT NOT supported yet ... ignore");
			}
		} break;
		case TableModelEvent.UPDATE:
			if (firstRow == 0 && lastRow == Integer.MAX_VALUE) {
				log.warn("full table UPDATE NOT supported yet ... ignore");
			} else if (firstRow == lastRow && lastRow < underlyingModelLen) {
				// update 1 row
				RecordEventSummary row = underlyingModel.getEventRow(lastRow);
				int foundIndex = super.findEventRowIndex(row);
				if (foundIndex != -1) {
					super.updateRowAt(foundIndex, row);
				}// else row already filtered out => do nothing
			} else if (firstRow >= 0 && lastRow < underlyingModelLen) {
				// update several rows (should not occur)
				for (int i = firstRow; i <= lastRow; i++) {
					RecordEventSummary row = underlyingModel.getEventRow(i);
					int foundIndex = super.findEventRowIndex(row);
					if (foundIndex != -1) {
						super.updateRowAt(foundIndex, row);
					}// else row already filtered out => do nothing
				}
			} else {
				log.warn("non standard UPDATE NOT supported yet ... ignore");
			}
			break;
		case TableModelEvent.DELETE:
			if (firstRow >= 0 && lastRow < underlyingModelLen) {
				// remove multiple events ??? 
//				List<RecordEventSummary> removedRows = new ArrayList<RecordEventSummary>();
//				for (int i = firstRow; i <= lastRow; i++) {
//					removedRows.add(underlyingModel.getEventRow(i));
//				}
//				super.removeEventRows(removedRows);
				log.warn("DELETE NOT supported yet (event mapping for deleted rows?) ... ignore");
			} else {
				log.warn("non standard DELETE NOT supported yet ... ignore");
			}
			break;
			
		default:
			break;
		}
		
	}


}
