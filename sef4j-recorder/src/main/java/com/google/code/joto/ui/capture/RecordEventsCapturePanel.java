package com.google.code.joto.ui.capture;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.spy.awtspy.AWTEventCaptureCategoryPanel;
import com.google.code.joto.ui.spy.calls.MethodCallCaptureCategoryPanel;
import com.google.code.joto.ui.spy.logs.LogCallCaptureCategoryPanel;

/**
 * swing Panel for controling Capture of RecordEvent in RecordEventStore
 * => for filtering + enable / disable ... 
 * 
 * contains configuration editor for capture filters (!= table view filter)
 * => may contains pluggeable configurations sub panels: for AOP settings, ...   
 *
 */
public class RecordEventsCapturePanel {

	protected JPanel panel;

	protected JotoContext context;
	
	protected EventRecorderToolbar toolbar;
	
	protected JTabbedPane tabbedCategoryPane;
	
	// ------------------------------------------------------------------------

	public RecordEventsCapturePanel(JotoContext context) {
		this.context = context;
		initComponents();
	}

	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		
		toolbar = new EventRecorderToolbar(context);
		panel.add(toolbar.getJComponent(), BorderLayout.NORTH);
	
		tabbedCategoryPane = new JTabbedPane();
		panel.add(tabbedCategoryPane, BorderLayout.CENTER);
		
		addDefaultsCategoryPanels();
	}

	protected void addDefaultsCategoryPanels() {
		addCategoryPanel(new MethodCallCaptureCategoryPanel(context));
		addCategoryPanel(new AWTEventCaptureCategoryPanel(context));
		addCategoryPanel(new LogCallCaptureCategoryPanel(context));
	}
	
	// ------------------------------------------------------------------------


	public JComponent getJComponent() {
		return panel;
	}
	
	public void setContext(JotoContext p) {
		this.context = p;
	}
	
	public void addCategoryPanel(RecordEventsCaptureCategoryPanel p) {
		tabbedCategoryPane.add(p.getTabName(), p.getJComponent());
	}
}
