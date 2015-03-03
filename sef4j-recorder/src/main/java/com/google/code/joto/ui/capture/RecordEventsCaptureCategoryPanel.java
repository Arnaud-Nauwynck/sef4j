package com.google.code.joto.ui.capture;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.google.code.joto.eventrecorder.writer.FilteringRecordEventWriter;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.filter.RecordEventFilterCategoryModel;
import com.google.code.joto.ui.filter.RecordEventFilterFileExternalFrameHolder;
import com.google.code.joto.ui.filter.RecordEventFilterFileTableModel;
import com.google.code.joto.ui.filter.RecordEventFilterFileTablePanel;
import com.google.code.joto.util.ui.GridBagLayoutFormBuilder;
import com.google.code.joto.util.ui.JCheckBoxUtils;

/**
 * abstract base-class for Category Capture Panel
 * <p/>
 * typical sub-classes: MethCall Capture, AWT-Event capture, Log,  ... 
 */
public abstract class RecordEventsCaptureCategoryPanel {

	protected JotoContext context;
	protected final String categoryName;
	protected RecordEventFilterCategoryModel filterCategoryModel;

	private JPanel panel;

	protected JCheckBox filterEnableEventsCheckBox;

	private JCheckBox showEmbeddedFilterTablePanelCheckBox;
	private RecordEventFilterFileTablePanel filtersPanel;

	private RecordEventFilterFileExternalFrameHolder externalFiltersFrameHolder;
	private JButton openExternalFilterTableFrameButton;

	private JCheckBox showDetailsPanelCheckBox;
	protected JPanel specificPanel;

	// ------------------------------------------------------------------------
	
	public RecordEventsCaptureCategoryPanel(JotoContext context, String categoryName) {
		this.context = context;
		this.categoryName = categoryName;
		this.filterCategoryModel = context.getOrCreateFilterCategoryModel(categoryName);
		initComponents();
	}

	public String getCategoryName() {
		return categoryName;
	}
	
	private void initComponents() {
		this.panel = new JPanel(new BorderLayout());

		JPanel northPanel = new JPanel(new GridBagLayout());
		panel.add(northPanel, BorderLayout.NORTH);
		GridBagLayoutFormBuilder b = new GridBagLayoutFormBuilder(northPanel);
		
		filterEnableEventsCheckBox = JCheckBoxUtils.snew("Enable Events", true, this, "onCheckboxFilterEnableEvents");
		b.addCompFillRow(filterEnableEventsCheckBox);

		RecordEventFilterFileTableModel filterTableModel = filterCategoryModel.getFilterItemTableModel();

		{
			JPanel showFiltersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
			showEmbeddedFilterTablePanelCheckBox = JCheckBoxUtils.snew("show embedded filters table", false, this, "onCheckboxShowEmbeddedFilterTablePanel");
			showFiltersPanel.add(showEmbeddedFilterTablePanelCheckBox);
			
			externalFiltersFrameHolder = new RecordEventFilterFileExternalFrameHolder(filterTableModel);
			openExternalFilterTableFrameButton = externalFiltersFrameHolder.createShowExternalFrameButton("open filters table view");
			showFiltersPanel.add(openExternalFilterTableFrameButton);
			
			b.addCompFillRow(showFiltersPanel);
		}
		
		filtersPanel = new RecordEventFilterFileTablePanel(filterTableModel);
		b.addCompFillRow(filtersPanel.getJComponent());
		filtersPanel.getJComponent().setVisible(showEmbeddedFilterTablePanelCheckBox.isSelected());

		showDetailsPanelCheckBox = JCheckBoxUtils.snew("show details", true, this, "onCheckBoxShowDetailsPanel");
		b.addCompFillRow(showDetailsPanelCheckBox);
		
		specificPanel = new JPanel();
		panel.add(specificPanel, BorderLayout.CENTER);
	}

	// ------------------------------------------------------------------------
	

	public JComponent getJComponent() {
		return panel;
	}

	public String getTabName() {
		return filterCategoryModel.getName();
	}
	
	public RecordEventFilterCategoryModel getFilterCategoryModel() {
		return filterCategoryModel;
	}

	public FilteringRecordEventWriter getFilterCategoryEventWriter() {
		return filterCategoryModel.getResultFilteringEventWriter();
	}

	/** called by introspection, GUI callback for JCheckBox showEmbeddedFilterTablePanelCheckBox */
	public void onCheckboxFilterEnableEvents(ActionEvent event) {
		filterCategoryModel.getResultFilteringEventWriter().setEnable(filterEnableEventsCheckBox.isSelected());
	}
	
	/** called by introspection, GUI callback for JCheckBox showEmbeddedFilterTablePanelCheckBox */
	public void onCheckboxShowEmbeddedFilterTablePanel(ActionEvent event) {
		filtersPanel.getJComponent().setVisible(showEmbeddedFilterTablePanelCheckBox.isSelected());
	}
	
	/** called by introspection, GUI callback for JCheckBox */
	public void onCheckBoxShowDetailsPanel(ActionEvent event) {
		specificPanel.setVisible(showDetailsPanelCheckBox.isSelected());
	}
}
