package com.google.code.joto.ui.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * simple swing TableModel for List<RecordEventFilterItem> 
 *
 * used to display/edit filters (=predicate item) to apply at record time or display time.
 */
public class RecordEventFilterFileTableModel extends AbstractTableModel {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	public static enum ColumnInfo {
		active("active", Boolean.class, true),
		name("name", String.class, true),
		description("description", String.class, true),
		persistentFile("file", String.class, true), // use File.class ??
		
		eventPredicate("Pred", String.class, false),  // no cell renderer/editor... (RecordEventSummaryPredicate.class, false),

		eventIdPredicateDescription("Id ~~", String.class, true),
		eventDatePredicateDescription("Date ~~", String.class, true),
		threadNamePredicateDescription("ThreadName ~~", String.class, true),
		eventTypePredicateDescription("Type ~~", String.class, true),
		eventSubTypePredicateDescription("SubType ~~", String.class, true),
		eventClassNamePredicateDescription("ClassName ~~", String.class, true),
		eventMethodNamePredicateDescription("MethodName ~~", String.class, true),
		eventMethodDetailPredicateDescription("ClassDetail~~", String.class, true),
		correlatedEventIdPredicateDescription("CorrId ~~", String.class, true);

		private String columnName;
		private Class<?> columnClass;
		boolean isEditable;
		
		private ColumnInfo(String columnName, Class<?> columnClass, boolean isEditable) {
			this.columnName = columnName;
			this.columnClass = columnClass;
			this.isEditable = isEditable;
		}


		public String getColumnName() {
			return columnName;
		}

		public Class<?> getColumnClass() {
			return columnClass;
		}

		public boolean isEditable() {
			return isEditable;
		}

		public static ColumnInfo[] getSTD_COLS() {
			return STD_COLS;
		}




		private static ColumnInfo[] STD_COLS = ColumnInfo.values();

		public static ColumnInfo fromOrdinal(int i) {
			return STD_COLS[i];
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	
	private List<RecordEventFilterFile> rows = new ArrayList<RecordEventFilterFile>();
	
	private PropertyChangeListener innerPropertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			onRowEltPropertyChange(evt);
		}
	};
	
	// ------------------------------------------------------------------------

	public RecordEventFilterFileTableModel() {
	}

	// ------------------------------------------------------------------------

	public List<RecordEventFilterFile> getRows() {
	    return rows;
	}
	
	public RecordEventFilterFile getRow(int rowIndex) {
		if (rowIndex < 0 || rowIndex >= rows.size()) return null; // should not occur!
		return rows.get(rowIndex);
	}

	public void addRow(RecordEventFilterFile p) {
		int firstRow = rows.size();
		rows.add(p);
		p.addPropertyChangeSupport(innerPropertyChangeListener);
		int lastRow = firstRow + 1;
		super.fireTableRowsInserted(firstRow, lastRow);
	}

	public void removeRow(RecordEventFilterFile item) {
		int index = rows.indexOf(item);
		if (index != -1) {
			rows.remove(index);
			item.removePropertyChangeSupport(innerPropertyChangeListener);
			super.fireTableRowsDeleted(index, index + 1);
		}
	}

	public void addRows(List<RecordEventFilterFile> elts) {
		if (elts != null && !elts.isEmpty()) {
			for(RecordEventFilterFile elt : elts) {
				addRow(elt);
			}
		}
	}

	private void onRowEltPropertyChange(PropertyChangeEvent evt) {
		// smart index finding?
		int foundIndex = -1;
		if (evt.getSource() instanceof RecordEventFilterFile) {
			RecordEventFilterFile item = (RecordEventFilterFile) evt.getSource();
			foundIndex = rows.indexOf(item);
		}
		if (foundIndex != -1) {
			fireTableRowsUpdated(foundIndex, foundIndex+1);
		} else {
			fireTableDataChanged();
		}
	}

	// implements swing TableModel
	// ------------------------------------------------------------------------
	
	@Override
	public int getRowCount() {
		return rows.size();
	}

	@Override
	public int getColumnCount() {
		return ColumnInfo.STD_COLS.length;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex >= rows.size()) return null; // should not occur!
		RecordEventFilterFile row = getRow(rowIndex);
		switch(ColumnInfo.fromOrdinal(columnIndex)) {
		case active: return row.isActive();
		case name: return row.getName();
		case description: return row.getDescription();
		case persistentFile: {
			File persistentFile = row.getPersistentFile();
			return (persistentFile != null)? persistentFile.getName() : "";
		}
		case eventPredicate: return row.getEventPredicate();

		case eventIdPredicateDescription: return row.getEventIdPredicateDescription();
		case eventDatePredicateDescription: return row.getEventDatePredicateDescription();
		case threadNamePredicateDescription: return row.getThreadNamePredicateDescription();
		case eventTypePredicateDescription: return row.getEventTypePredicateDescription();
		case eventSubTypePredicateDescription: return row.getEventSubTypePredicateDescription();
		case eventClassNamePredicateDescription: return row.getEventClassNamePredicateDescription();
		case eventMethodNamePredicateDescription: return row.getEventMethodNamePredicateDescription();
		case eventMethodDetailPredicateDescription: return row.getEventMethodDetailPredicateDescription();
		case correlatedEventIdPredicateDescription: return row.getCorrelatedEventIdPredicateDescription();
		default: return null;
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		ColumnInfo col = ColumnInfo.fromOrdinal(columnIndex);
		return col.getColumnName();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		ColumnInfo col = ColumnInfo.fromOrdinal(columnIndex);
		return col.getColumnClass();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		ColumnInfo col = ColumnInfo.fromOrdinal(columnIndex);
		return col.isEditable();
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (rowIndex < 0 || rowIndex >= rows.size()) return; // should not occur!
		RecordEventFilterFile row = getRow(rowIndex);
		switch(ColumnInfo.fromOrdinal(columnIndex)) {
		case active: row.setActive(((Boolean) value).booleanValue()); break;
		case name: row.setName((String) value); break;
		case description: row.setDescription((String) value); break;
		case persistentFile: {
			File persistentFile = (value != null)? new File((String) value) : null; 
			row.setPersistentFile(persistentFile);
		} break;
		case eventPredicate: 
			// not editable .. return row.setEventPredicate(() value); 
			break;

		case eventIdPredicateDescription: row.setEventIdPredicateDescription((String) value); break;
		case eventDatePredicateDescription: row.setEventDatePredicateDescription((String) value); break;
		case threadNamePredicateDescription: row.setThreadNamePredicateDescription((String) value); break;
		case eventTypePredicateDescription: row.setEventTypePredicateDescription((String) value); break;
		case eventSubTypePredicateDescription: row.setEventSubTypePredicateDescription((String) value); break;
		case eventClassNamePredicateDescription: row.setEventClassNamePredicateDescription((String) value); break;
		case eventMethodNamePredicateDescription: row.setEventMethodNamePredicateDescription((String) value); break;
		case eventMethodDetailPredicateDescription: row.setEventMethodDetailPredicateDescription((String) value); break;
		case correlatedEventIdPredicateDescription: row.setCorrelatedEventIdPredicateDescription((String) value); break;
		default: 
			// should not occur
			break;
		}
	}
	
	
}
