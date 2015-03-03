package com.google.code.joto.ui.filter;

import org.junit.Test;

import com.google.code.joto.ui.AbstractJotoUiTestCase;
import com.google.code.joto.ui.filter.RecordEventFilterFile;
import com.google.code.joto.ui.filter.RecordEventFilterFileEditor;
import com.google.code.joto.ui.filter.RecordEventFilterFileTableModel;
import com.google.code.joto.ui.filter.RecordEventFilterFileTablePanel;
import com.google.code.joto.util.io.ui.UiTestUtils;

/**
 * JUnit test for RecordEventFilterTablePanel
 */
public class RecordEventFilterTablePanelTest extends AbstractJotoUiTestCase {

	@Test
	public void testDoNothing() {
	}
	
	@Test
	public void openCloseEditorPane() throws Exception {
		RecordEventFilterFileEditor editor = new RecordEventFilterFileEditor();
		UiTestUtils.showInFrame(editor.getJComponent());
	}
	
//	@Test
	public void openCloseTablePane() throws Exception {
		RecordEventFilterFileTableModel tm = new RecordEventFilterFileTableModel();
		RecordEventFilterFile f1 = new RecordEventFilterFile();
		tm.addRow(f1);
		RecordEventFilterFile f2 = new RecordEventFilterFile();
		tm.addRow(f2);
		
		RecordEventFilterFileTablePanel tableView = new RecordEventFilterFileTablePanel(tm);
		
		UiTestUtils.showInFrame(tableView.getJComponent());
	}

}
