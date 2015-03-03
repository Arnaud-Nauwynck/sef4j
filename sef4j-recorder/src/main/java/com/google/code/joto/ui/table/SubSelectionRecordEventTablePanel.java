package com.google.code.joto.ui.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.filter.RecordEventFilterFileExternalFrameHolder;
import com.google.code.joto.ui.filter.RecordEventFilterFileTableModel;
import com.google.code.joto.ui.filter.RecordEventFilterFileTablePanel;
import com.google.code.joto.util.ui.GridBagLayoutFormBuilder;
import com.google.code.joto.util.ui.JButtonUtils;
import com.google.code.joto.util.ui.JCheckBoxUtils;

/**
 *
 */
public class SubSelectionRecordEventTablePanel {

	private JotoContext context;
	
	private JPanel panel;
	
	private SubSelectionRecordEventTableModel subSelectionTableModel;
	private RecordEventTablePane subSelectionTablePane;

	private JCheckBox syncCheckBox;
	private JFormattedTextField nthLastField;
	
	private RecordEventFilterFileTableModel filterTableModel;
	private JCheckBox showEmbeddedFilterTablePanelCheckBox;
	private RecordEventFilterFileTablePanel filtersPanel;
	
	private RecordEventFilterFileExternalFrameHolder externalfiltersFrameHolder;
	private JButton openExternalFilterTableFrameButton;

	
	// ------------------------------------------------------------------------
	
	public SubSelectionRecordEventTablePanel(JotoContext context, SubSelectionRecordEventTableModel subSelectionTableModel) {
		super();
		this.context = context;
		this.subSelectionTableModel = subSelectionTableModel;
		this.filterTableModel = new RecordEventFilterFileTableModel();

		initComponents();
	}

	private void initComponents() {
		this.panel = new JPanel(new BorderLayout());
		
		{
			JPanel northPanel = new JPanel(new GridBagLayout());
			panel.add(northPanel, BorderLayout.NORTH);
			GridBagLayoutFormBuilder b = new GridBagLayoutFormBuilder(northPanel);

			syncCheckBox = JCheckBoxUtils.snew("Synchronize incoming events", true, this, "onCheckBoxSync");
			b.addCompFillRow(syncCheckBox);

			{
	//			JToolBar toolbar = new JToolBar();
	//			toolbar.setFloatable(false);
				JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
				b.addCompFillRow(toolbar);
						
				toolbar.add(JButtonUtils.snew("Clear All", this, "onButtonClearAllEvents"));
				toolbar.add(JButtonUtils.snew("Reload All", this, "onButtonReloadAllEvents"));
				
				nthLastField = new JFormattedTextField(NumberFormat.getIntegerInstance());
				nthLastField.setPreferredSize(new Dimension(50, 21));
				nthLastField.setText("500");
				toolbar.add(nthLastField);
				toolbar.add(JButtonUtils.snew("Reload Nth Last", this, "onButtonReloadNthLastEvents"));
				
				toolbar.add(JButtonUtils.snew("Remove Selected", this, "onButtonRemoveSelectedEvents"));
			}			
	
			{
				JPanel showFiltersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
				showEmbeddedFilterTablePanelCheckBox = JCheckBoxUtils.snew("show embedded filters table", false, this, "onCheckboxShowEmbeddedFilterTablePanel");
				showFiltersPanel.add(showEmbeddedFilterTablePanelCheckBox);
				
				externalfiltersFrameHolder = new RecordEventFilterFileExternalFrameHolder(filterTableModel);
				openExternalFilterTableFrameButton = externalfiltersFrameHolder.createShowExternalFrameButton("open filters table view");
				showFiltersPanel.add(openExternalFilterTableFrameButton);
				b.addCompFillRow(showFiltersPanel);
			}
			
			filtersPanel = new RecordEventFilterFileTablePanel(filterTableModel);
			b.addCompFillRow(filtersPanel.getJComponent());
			filtersPanel.getJComponent().setVisible(showEmbeddedFilterTablePanelCheckBox.isSelected());
		}
		
		subSelectionTablePane = new RecordEventTablePane(subSelectionTableModel);
		panel.add(subSelectionTablePane.getJComponent(), BorderLayout.CENTER);
	}
	
	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}
	
	public JotoContext getContext() {
		return context;
	}

	/** called by introspection, GUI callback */
	public void onCheckBoxSync(ActionEvent event) {
		subSelectionTableModel.setSyncToUnderlying(syncCheckBox.isSelected());
	}
	
	/** called by introspection, GUI callback */
	public void onButtonClearAllEvents(ActionEvent event) {
		subSelectionTableModel.clearEventRows();
	}

	/** called by introspection, GUI callback */
	public void onButtonReloadAllEvents(ActionEvent event) {
		subSelectionTableModel.reloadAllEvents();	
	}
	
	/** called by introspection, GUI callback */
	public void onButtonReloadNthLastEvents(ActionEvent event) {
		int nthLast;
		try {
			nthLast = Integer.parseInt(nthLastField.getText());
		} catch(NumberFormatException ex) {
			nthLast = 500; // should not occur
		}
		subSelectionTableModel.reloadNthLastEvents(nthLast);	
	}
	
	
	/** called by introspection, GUI callback */
	public void onButtonRemoveSelectedEvents(ActionEvent event) {
		List<RecordEventSummary> selectedEventRows = subSelectionTablePane.getSelectedEventRows();
		subSelectionTableModel.removeRows(selectedEventRows);
	}

	/** called by introspection, GUI callback for JCheckBox showEmbeddedFilterTablePanelCheckBox */
	public void onCheckboxShowEmbeddedFilterTablePanel(ActionEvent event) {
		filtersPanel.getJComponent().setVisible(showEmbeddedFilterTablePanelCheckBox.isSelected());
	}

}
