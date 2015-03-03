package com.google.code.joto.ui.filter;

import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.util.ui.JButtonUtils;

/**
 * helper class to store a JFrame for a RecordEventFilterFileTablePane, and reopen it on demand
 */
public class RecordEventFilterFileExternalFrameHolder {
	
	private static Logger log = LoggerFactory.getLogger(RecordEventFilterFileExternalFrameHolder.class);
	
	private RecordEventFilterFileTableModel filterTableModel;
	
	private RecordEventFilterFileTablePanel filtersPanel;
	private JFrame filtersFrame;
	
	// ------------------------------------------------------------------------

	public RecordEventFilterFileExternalFrameHolder(RecordEventFilterFileTableModel filterTableModel) {
		this.filterTableModel = filterTableModel;
	}

	// ------------------------------------------------------------------------

	public JButton createShowExternalFrameButton(String label) {
		if (label == null) {
			label = "open filters table view";
		}
		return JButtonUtils.snew(label, this, "onButtonOpenExternalFilterTableFrame");
	}

	/** called by introspectin, GUI callback */
	public void onButtonOpenExternalFilterTableFrame(ActionEvent event) {
		showFilterFrame();
	}
	
	public void showFilterFrame() {
		if (filtersFrame == null) {
			filtersFrame = new JFrame();
			filtersPanel = new RecordEventFilterFileTablePanel(filterTableModel);
			filtersFrame.getContentPane().add(filtersPanel.getJComponent());
			filtersFrame.pack();
			
			filtersFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			filtersFrame.setVisible(true);
		} else {
			if (!filtersFrame.isVisible()) {
				filtersFrame.setVisible(true);
			}
		}
		filtersFrame.requestFocus();
	}

	public void dispose() {
		if (filtersFrame != null) {
			filtersPanel = null;
			try {
				filtersFrame.dispose();
			} catch(Exception ex) {
				log.warn("Failed to dispose ... ignore", ex);
			}
			filtersFrame = null;
		}
	}
}