package com.google.code.joto.ui.filter;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.code.joto.util.ui.JCheckBoxUtils;

/**
 * panel for embedding a RecordEventFilterFileTablePanel
 * with show/hide / show-external buttons
 */
public class RecordEventFilterFileEmbeddedPanel {

	private JPanel panel;
	
	private RecordEventFilterFileTableModel filterTableModel;
	
	private JCheckBox showEmbeddedFilterTablePanelCheckBox;
	private JButton openExternalFilterTableFrameButton;

	private RecordEventFilterFileTablePanel embeddedFiltersPanel;

	private RecordEventFilterFileExternalFrameHolder externalFiltersFrameHolder;

	
	
	// ------------------------------------------------------------------------

	public RecordEventFilterFileEmbeddedPanel(RecordEventFilterFileTableModel filterTableModel) {
		this.filterTableModel = filterTableModel;
		
		panel = new JPanel(new BorderLayout());
		
		{
			JPanel showFiltersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
			showEmbeddedFilterTablePanelCheckBox = JCheckBoxUtils.snew("show embedded filters table", false, this, "onCheckboxShowEmbeddedFilterTablePanel");
			showFiltersPanel.add(showEmbeddedFilterTablePanelCheckBox);
			
			externalFiltersFrameHolder = new RecordEventFilterFileExternalFrameHolder(filterTableModel);
			openExternalFilterTableFrameButton = externalFiltersFrameHolder.createShowExternalFrameButton("open filters table view"); 
			showFiltersPanel.add(openExternalFilterTableFrameButton);
			
			panel.add(showFiltersPanel, BorderLayout.NORTH);
		}
		
		embeddedFiltersPanel = new RecordEventFilterFileTablePanel(filterTableModel);
		embeddedFiltersPanel.getJComponent().setVisible(showEmbeddedFilterTablePanelCheckBox.isSelected());
		panel.add(embeddedFiltersPanel.getJComponent(), BorderLayout.CENTER);

	}

	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}

	public RecordEventFilterFileTableModel getFilterTableModel() {
		return filterTableModel;
	}
	
}
