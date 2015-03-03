package com.google.code.joto.ui;

import org.junit.Test;

import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.capture.RecordEventsCapturePanel;
import com.google.code.joto.util.io.ui.UiTestUtils;

public class RecordEventsCapturePanelTest extends AbstractJotoUiTestCase {

	@Test
	public void testDoNothing() {
	}
	
	@Test
	public void testOpenClosePanel() {
		JotoContext context = new JotoContext();
		RecordEventsCapturePanel obj = new RecordEventsCapturePanel(context);
		UiTestUtils.showInFrame(obj.getJComponent());
	}
}
