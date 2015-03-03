package com.google.code.joto.ui.spy.calls;

import com.google.code.joto.eventrecorder.spy.calls.MethodCallEventUtils;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.capture.RecordEventsCaptureCategoryPanel;

/**
 *
 */
public class MethodCallCaptureCategoryPanel extends RecordEventsCaptureCategoryPanel {

	public static final String METHODCALL_CAPTURE_CATEGORY = MethodCallEventUtils.METHODCALL_EVENT_TYPE;

	// ------------------------------------------------------------------------
	
	public MethodCallCaptureCategoryPanel(JotoContext context) {
		super(context, METHODCALL_CAPTURE_CATEGORY);

//		specificPanel.setLayout(new GridBagLayout());
//		GridBagLayoutFormBuilder b = new GridBagLayoutFormBuilder(specificPanel);
		
	}

	// ------------------------------------------------------------------------
	
}
