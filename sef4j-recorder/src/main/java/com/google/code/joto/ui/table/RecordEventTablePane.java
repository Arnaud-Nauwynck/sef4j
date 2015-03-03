package com.google.code.joto.ui.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.spy.awtspy.AWTRecordEventWriterSpy;

/**
 * swing Panel containing a table of RecordEvent  
 */
public class RecordEventTablePane {

	private AbstractRecordEventTableModel recordEventTableModel;
	private JScrollPane recordEventScrollPane;
	private JTable recordEventTable;
	
	// ------------------------------------------------------------------------

	public RecordEventTablePane(AbstractRecordEventTableModel recordEventTableModel) {
		this.recordEventTableModel = recordEventTableModel;
		
		this.recordEventTable = new JTable(recordEventTableModel);
//		recordEventTable.setRowSorter(new TableRowSorter<AbstractRecordEventTableModel>(recordEventTableModel));
		
		this.recordEventScrollPane = new JScrollPane(recordEventTable);
		AWTRecordEventWriterSpy.setIgnoreComponentAwtEventSpy(recordEventScrollPane);
	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return recordEventScrollPane;
	}

	public JTable getRecordEventTable() {
		return recordEventTable;
	}

	public List<RecordEventSummary> getSelectedEventRows() {
		List<RecordEventSummary> res = new ArrayList<RecordEventSummary>();
		int[] viewRows = recordEventTable.getSelectedRows();
		for(int viewIndex : viewRows) {
			int modelIndex = recordEventTable.convertRowIndexToModel(viewIndex);
			RecordEventSummary eventRow = recordEventTableModel.getEventRow(modelIndex);
			res.add(eventRow);
		}
		return res;
	}
	
}
