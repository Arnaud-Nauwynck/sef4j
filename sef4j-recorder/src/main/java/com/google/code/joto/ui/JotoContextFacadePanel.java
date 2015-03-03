package com.google.code.joto.ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.google.code.joto.eventrecorder.spy.awtspy.AWTRecordEventWriterSpy;
import com.google.code.joto.ui.capture.RecordEventsCapturePanel;
import com.google.code.joto.ui.config.JotoConfigPanel;
import com.google.code.joto.ui.conv.RecordEventsTableAndConvertersPanel;
import com.google.code.joto.ui.table.AbstractRecordEventTableModel;
import com.google.code.joto.ui.table.RecordEventStoreTableModel;
import com.google.code.joto.ui.table.SubSelectionRecordEventTableModel;
import com.google.code.joto.ui.table.SubSelectionRecordEventTablePanel;
import com.google.code.joto.ui.tree.AggrRecordEventTreeModel;
import com.google.code.joto.ui.tree.AggrRecordEventTreeView;

/**
 * Main UI facade for Joto
 */
public class JotoContextFacadePanel {

	protected JotoContext context;
	
	protected JPanel panel;
	
	protected JTabbedPane tabbedPane;

	protected JotoConfigPanel configPanel;

	protected RecordEventsCapturePanel capturePanel;
	
	protected AggrRecordEventTreeView aggrTreeView;
	
	protected AbstractRecordEventTableModel recordEventTableModel;
	
	protected SubSelectionRecordEventTableModel subSelection1TableModel;
	protected SubSelectionRecordEventTablePanel subSelection1TablePanel;

	protected SubSelectionRecordEventTableModel subSelection2TableModel;
	protected SubSelectionRecordEventTablePanel subSelection2TablePanel;

	protected RecordEventsTableAndConvertersPanel resultsConverterPanel;
	
	// ------------------------------------------------------------------------

	public JotoContextFacadePanel(JotoContext context) {
		this.context = context;
		initComponents();
	}

	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		AWTRecordEventWriterSpy.setIgnoreComponentAwtEventSpy(panel);
		
		tabbedPane = new JTabbedPane();
		panel.add(tabbedPane, BorderLayout.CENTER);

		{ // tab : general / config
			configPanel = new JotoConfigPanel(context);
			tabbedPane.add("Config", configPanel.getJComponent());
		}

		{ // tab : capture (+ capture filter)
			capturePanel = new RecordEventsCapturePanel(context);
			tabbedPane.add("Capture", capturePanel.getJComponent());
		}

		{ // tab : aggregated display
			AggrRecordEventTreeModel aggrTreeModel = new AggrRecordEventTreeModel(context);
			aggrTreeView = new AggrRecordEventTreeView(aggrTreeModel);
			tabbedPane.add("Aggr Tree", aggrTreeView.getJComponent());
		}

		recordEventTableModel = new RecordEventStoreTableModel(context.getEventStore());

		{ // tab : selection table
			subSelection1TableModel = new SubSelectionRecordEventTableModel(recordEventTableModel);
			subSelection1TablePanel = new SubSelectionRecordEventTablePanel(context, subSelection1TableModel);
			tabbedPane.add("Selection1", subSelection1TablePanel.getJComponent());
		}
		{ // tab : selection table 
			subSelection2TableModel = new SubSelectionRecordEventTableModel(recordEventTableModel);
			subSelection2TablePanel = new SubSelectionRecordEventTablePanel(context, subSelection2TableModel);
			tabbedPane.add("Selection2", subSelection2TablePanel.getJComponent());
		}

		{ // tab : result converters
			resultsConverterPanel = new RecordEventsTableAndConvertersPanel(context, subSelection1TableModel);
			tabbedPane.add("Results", resultsConverterPanel.getJComponent());
		}
		
		
	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}
	
	
}
