package com.google.code.joto.ui.filter;

import org.junit.Test;

import com.google.code.joto.ui.AbstractJotoUiTestCase;
import com.google.code.joto.ui.filter.RecordEventFilterFileTableModel;
import com.google.code.joto.ui.filter.RecordEventFilterFileTablePanel;
import com.google.code.joto.util.io.ui.UiTestUtils;

public class RecordEventFilterItemTablePanelTest extends AbstractJotoUiTestCase {

	@Test
	public void testOpenClosePane() {
		RecordEventFilterFileTableModel model = new RecordEventFilterFileTableModel();
		RecordEventFilterFileTablePanel pane = new RecordEventFilterFileTablePanel(model);
		UiTestUtils.showInFrame(pane.getJComponent());
	}
}
