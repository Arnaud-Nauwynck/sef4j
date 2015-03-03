package com.google.code.joto.ui;

import org.junit.Test;

import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.JotoContextFacadePanel;
import com.google.code.joto.util.io.ui.UiTestUtils;

public class JotoContextFacadePanelTest extends AbstractJotoUiTestCase {

	@Test
	public void testDoNothing() {
	}
	
	@Test
	public void testOpenClosePanel() {
		JotoContext context = new JotoContext();
		JotoContextFacadePanel obj = new JotoContextFacadePanel(context);
		UiTestUtils.showInFrame(obj.getJComponent());
	}
	
}
