package com.google.code.joto.ui.capture;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.filter.RecordEventFilterFileExternalFrameHolder;
import com.google.code.joto.ui.filter.RecordEventFilterFileTableModel;
import com.google.code.joto.util.ui.IconUtils;
import com.google.code.joto.util.ui.JButtonUtils;


/**
 * Swing panel for record/pause/continue buttons + show details ... 
 */
public class EventRecorderToolbar {
	
	private static Logger log = LoggerFactory.getLogger(EventRecorderToolbar.class);
	
	private JotoContext context;
	
	private PropertyChangeListener modelChangeListener;
	
	private JToolBar toolbar;
	private JButton startRecordButton;
	private JButton stopRecordButton;
	private JButton showCaptureFiltersButton;
//	private JButton clearButton;

	private RecordEventFilterFileExternalFrameHolder captureFiltersFrameHolder;

	// -------------------------------------------------------------------------

	public EventRecorderToolbar(JotoContext context) {
		this.context = context;
		
		modelChangeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				onModelPropertyChange(evt);
			}
		};
		context.addPropertyChangeListener(modelChangeListener); 
		
		toolbar = new JToolBar();
		toolbar.setFloatable(false);
		
		ImageIcon playIcon = IconUtils.getBasic32().get("play");
		startRecordButton = JButtonUtils.snew(playIcon, "start record", this, "onButtonStartRecord");
		toolbar.add(startRecordButton);

		ImageIcon pauseIcon = IconUtils.getBasic32().get("pause");
		stopRecordButton = JButtonUtils.snew(pauseIcon, "pause record", this, "onButtonPauseRecord");
		toolbar.add(stopRecordButton);


		{ // capture filter
			RecordEventFilterFileTableModel captureFiltersTableModel = context.getCaptureFilterCategoryModel().getFilterItemTableModel(); 
			captureFiltersFrameHolder = new RecordEventFilterFileExternalFrameHolder(captureFiltersTableModel);
			showCaptureFiltersButton = captureFiltersFrameHolder.createShowExternalFrameButton("filter");
			toolbar.add(showCaptureFiltersButton);
		}

//		{ // methodCall filter (shortcut for tab "methodCall" -> button)
//			RecordEventFilterFileTableModel methodCaptureFilterModel = context.getMethodCallFilterCategoryModel().getFilterItemTableModel();
//			methodCaptureFiltersFrameHolder = new RecordEventFilterFileExternalFrameHolder(methodCaptureFilterModel);
//			showMethodCaptureFiltersButton = methodCaptureFiltersFrameHolder.createShowExternalFrameButton("meth filter");
//			toolbar.add(showMethodCaptureFiltersButton);
//		}

	}

	public void dispose() {
		if (context != null && modelChangeListener != null) {
			context.removePropertyChangeListener(modelChangeListener);
		}
		modelChangeListener = null;
		context = null;
		if (captureFiltersFrameHolder != null) {
			try {
				captureFiltersFrameHolder.dispose();
			} catch(Exception ex) {
				log.warn("Failed to dispose ... ignore", ex);
			}
		}
	}
	
	//-------------------------------------------------------------------------

	public JComponent getJComponent() {
		return toolbar;
	}

	// -------------------------------------------------------------------------
	

	public void onButtonStartRecord(ActionEvent event) {
		context.startRecord();
	}

	public void onButtonPauseRecord(ActionEvent event) {
		context.stopRecord();
	}
	
	private void onModelPropertyChange(PropertyChangeEvent evt) {
		String prop = evt.getPropertyName();
		if (prop.equals(JotoContext.PROP_RECORDING_STATUS)) {
			stopRecordButton.setEnabled(context.isEnableStopRecord());
			startRecordButton.setEnabled(context.isEnableStartRecord());
		}
	}

}
	