package com.google.code.joto.ui.filter;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.functors.NotPredicate;
import org.apache.commons.collections.functors.OrPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.predicate.DefaultEventTypeRecordEventSummaryPredicate;
import com.google.code.joto.eventrecorder.predicate.RecordEventSummaryPredicateUtils;
import com.google.code.joto.eventrecorder.predicate.RecordEventSummaryPredicateUtils.ClassMethodPatternRecordEventSummaryPredicate;
import com.google.code.joto.eventrecorder.predicate.RecordEventSummaryPredicateUtils.TypeSubTypePatternRecordEventSummaryPredicate;
import com.google.code.joto.util.ui.GridBagLayoutFormBuilder;
import com.google.code.joto.util.ui.IconUtils;
import com.google.code.joto.util.ui.JButtonUtils;
import com.google.code.joto.util.ui.ScrolledTextPane;
import com.thoughtworks.xstream.XStream;

/**
 * simple swing View/Editor for RecordEventFilter model
 */
public class RecordEventFilterFileEditor {
	
	private static Logger log = LoggerFactory.getLogger(RecordEventFilterFileEditor.class);
	
	private RecordEventFilterFile model;

	private JPanel panel;
	
	private JTabbedPane tabbedPane;
	
	private JCheckBox activeField;

	private JTextField nameField;
	private JTextField descriptionField;
	private JTextField tagsField;
	
	private JTextField persistentFileField;

	private JTextField eventIdPredicateDescription;
	private JTextField eventDatePredicateDescription;
	private JTextField threadNamePredicateDescription;
	private JTextField eventTypePredicateDescription;
	private JTextField eventSubTypePredicateDescription;
	private JTextField eventClassNamePredicateDescription;
	private JTextField eventMethodNamePredicateDescription;
	private JTextField eventMethodDetailPredicateDescription;
	private JTextField correlatedEventIdPredicateDescription;

	private ScrolledTextPane detailedXmlPredicateTextPane;
	private JToolBar detailedViewerToolbar;
	
	private XStream predicateXStream = RecordEventSummaryPredicateUtils.createDefaultPredicateXStream();

	private JButton applyEditFilterFileButton;
	private JButton undoEditFilterFileButton;
	private JButton saveFilterFileButton;
	private JButton reloadFilterFileButton;


	// ------------------------------------------------------------------------

	public RecordEventFilterFileEditor() {
		initComponents();
	}


	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		
		tabbedPane = new JTabbedPane();
		panel.add(tabbedPane, BorderLayout.CENTER);
				
		{ // Tab1 : General info
			JPanel tab1 = new JPanel(new GridBagLayout());
			tabbedPane.add(tab1, "General");
			GridBagLayoutFormBuilder generalForm = new GridBagLayoutFormBuilder(tab1); 
	
			activeField = new JCheckBox();
			generalForm.addLabelComp("Active", activeField);
	
			nameField = new JTextField();
			generalForm.addLabelComp("Name", nameField);
			descriptionField = new JTextField();
			generalForm.addLabelComp("Description", descriptionField);
			tagsField = new JTextField ();
			generalForm.addLabelComp("Tags", tagsField);
	
			persistentFileField = new JTextField();
			generalForm.addLabelComp("File", persistentFileField);
		
		}

		{ // Tab 2 : Columns comments info
			JPanel tab2 = new JPanel(new GridBagLayout());
			tabbedPane.add(tab2, "Columns");
			GridBagLayoutFormBuilder columnsForm = new GridBagLayoutFormBuilder(tab2); 

			eventIdPredicateDescription = new JTextField();
			columnsForm.addLabelComp("Id ~~", eventIdPredicateDescription);
			eventDatePredicateDescription = new JTextField();
			columnsForm.addLabelComp("Date ~~", eventDatePredicateDescription);
			threadNamePredicateDescription = new JTextField();
			columnsForm.addLabelComp("ThreadName ~~", threadNamePredicateDescription);
			eventTypePredicateDescription = new JTextField();
	
			columnsForm.addLabelComp("eventType ~~", eventTypePredicateDescription);
			eventSubTypePredicateDescription = new JTextField();
			columnsForm.addLabelComp("eventSubType ~~", eventSubTypePredicateDescription);
			eventClassNamePredicateDescription = new JTextField();
			columnsForm.addLabelComp("eventClassName ~~", eventClassNamePredicateDescription);
			eventMethodNamePredicateDescription = new JTextField();
			columnsForm.addLabelComp("eventMethodName ~~", eventMethodNamePredicateDescription);
			eventMethodDetailPredicateDescription = new JTextField();
			columnsForm.addLabelComp("eventMethodDetail ~~", eventMethodDetailPredicateDescription);
	
			correlatedEventIdPredicateDescription = new JTextField();
			columnsForm.addLabelComp("correlatedEventId ~~", correlatedEventIdPredicateDescription);
		
		}

		{ // Tab 3: XML (XStream) editor 
			JPanel xmlTabPanel = new JPanel(new GridBagLayout());
			tabbedPane.add(xmlTabPanel, "Xml");
			GridBagLayoutFormBuilder xmlForm = new GridBagLayoutFormBuilder(xmlTabPanel);

			detailedXmlPredicateTextPane = new ScrolledTextPane();
			xmlForm.addLabelCompFill2Rows("xml text (XStream format)", detailedXmlPredicateTextPane.getJComponent());
		
			JToolBar toolbar = detailedXmlPredicateTextPane.getToolbar();
			addStandardXmlInsertTextActions(toolbar);
		}
		
		detailedViewerToolbar = new JToolBar();
		panel.add(detailedViewerToolbar, BorderLayout.SOUTH);
		
		{
			// toolbar buttons for Save File / Reload File
			// toolbar buttons for Save File / Reload File
			applyEditFilterFileButton = JButtonUtils.snew(IconUtils.eclipseGif.get("apply"), "Apply", this, "onApplyEditFilterFileButton");
			detailedViewerToolbar.add(applyEditFilterFileButton);

			undoEditFilterFileButton = JButtonUtils.snew(IconUtils.eclipseGif.get("undo"), "Undo", this, "onUndoEditFilterFileButton");
			detailedViewerToolbar.add(undoEditFilterFileButton);

			saveFilterFileButton = JButtonUtils.snew(IconUtils.eclipseGif.get("save"), "Save File", this, "onSaveFilterFileButton");
			detailedViewerToolbar.add(saveFilterFileButton);

			reloadFilterFileButton = JButtonUtils.snew(IconUtils.eclipseGif.get("reload"), "Reload File", this, "onReloadFilterFileButton");
			detailedViewerToolbar.add(reloadFilterFileButton);

		}

	}
	
	private void addStandardXmlInsertTextActions(JToolBar toolbar) {
		toolbar.add(JButtonUtils.snew("CheckParse", this, "onButtonCheckParse"));
		
		Predicate dummy1 = PredicateUtils.truePredicate();
		Predicate dummy2 = PredicateUtils.truePredicate();
		addXmlInsertTextAction("and", AndPredicate.getInstance(dummy1, dummy2));
		addXmlInsertTextAction("or", OrPredicate.getInstance(dummy1, dummy2));
		addXmlInsertTextAction("not", NotPredicate.getInstance(dummy1));

		Predicate dummyEventTypeEquals = EqualPredicate.getInstance("eventType1");
		Predicate dummyEventSubTypeEquals = EqualPredicate.getInstance("eventSubType1");
		Predicate dummyClassNameEquals = EqualPredicate.getInstance("class1");
		Predicate dummyMethodNameEquals = EqualPredicate.getInstance("meth1");
		
		// default for compound
		addXmlInsertTextAction("MatchesEvent", new DefaultEventTypeRecordEventSummaryPredicate(
				null, null, null, 
				dummyEventTypeEquals, dummyEventSubTypeEquals, 
				dummyClassNameEquals, dummyMethodNameEquals, null, null));

		// simple patterns
		List<String> includes = new ArrayList<String>();
		includes.add(".*");
		addXmlInsertTextAction("Type,SubType", new TypeSubTypePatternRecordEventSummaryPredicate("eventType", includes, null));
		addXmlInsertTextAction("Class.Method", new ClassMethodPatternRecordEventSummaryPredicate(includes, null, includes, null));
	}

	private void addXmlInsertTextAction(String label, Predicate predicate) {
		String xml = predicateXStream.toXML(predicate);
		JButton button = detailedXmlPredicateTextPane.createInsertTextButton(label, xml);
		detailedXmlPredicateTextPane.getToolbar().add(button);
	}

	
	// ------------------------------------------------------------------------

	public JComponent getJComponent() {
		return panel;
	}

	public JToolBar getDetailedViewerToolbar() {
		return detailedViewerToolbar;
	}

	public RecordEventFilterFile getModel() {
		return model;
	}
	
	public void setModel(RecordEventFilterFile p) {
		if (model != null) {
//			model.removePropertyChangeListener(innerPropChangeListener);
		}
		this.model = p;
		if (model != null) {
//			model.addPropertyChangeListener(innerPropChangeListener);
		}		
		updateModel2View();
	}

	public void updateModel2View() {
		// use tmp new empty instead when null
		RecordEventFilterFile m = (model != null)? model : new RecordEventFilterFile(); 
		
		activeField.setSelected(m.isActive());
		
		nameField.setText(m.getName());
		descriptionField.setText(m.getDescription());

		File persistentFile = m.getPersistentFile();
		persistentFileField.setText((persistentFile != null)? persistentFile.getAbsolutePath() : "");

		eventIdPredicateDescription.setText(m.getEventIdPredicateDescription());
		eventDatePredicateDescription.setText(m.getEventDatePredicateDescription());
		threadNamePredicateDescription.setText(m.getThreadNamePredicateDescription());
		eventTypePredicateDescription.setText(m.getEventTypePredicateDescription());
		eventSubTypePredicateDescription.setText(m.getEventSubTypePredicateDescription());
		eventClassNamePredicateDescription.setText(m.getEventClassNamePredicateDescription());
		eventMethodNamePredicateDescription.setText(m.getEventMethodNamePredicateDescription());
		eventMethodDetailPredicateDescription.setText(m.getEventMethodDetailPredicateDescription());
		correlatedEventIdPredicateDescription.setText(m.getCorrelatedEventIdPredicateDescription());

		String xstreamPredicateXml = predicateXStream.toXML(m.getEventPredicate());
		detailedXmlPredicateTextPane.setText(xstreamPredicateXml);
	}
	
	public void updateView2Model(RecordEventFilterFile m) {
		if (m == null) return;
		
		m.setActive(activeField.isSelected());
        
		m.setName(nameField.getText());
		m.setDescription(descriptionField.getText());

		String tmpPersistentFilePath = persistentFileField.getText();
		File persistentFile = !"".equals(tmpPersistentFilePath) ? new File(tmpPersistentFilePath) : null;
		m.setPersistentFile(persistentFile);

		m.setEventIdPredicateDescription(eventIdPredicateDescription.getText());
		m.setEventDatePredicateDescription(eventDatePredicateDescription.getText());
		m.setThreadNamePredicateDescription(threadNamePredicateDescription.getText());
		m.setEventTypePredicateDescription(eventTypePredicateDescription.getText());
		m.setEventSubTypePredicateDescription(eventSubTypePredicateDescription.getText());
		m.setEventClassNamePredicateDescription(eventClassNamePredicateDescription.getText());
		m.setEventMethodNamePredicateDescription(eventMethodNamePredicateDescription.getText());
		m.setEventMethodDetailPredicateDescription(eventMethodDetailPredicateDescription.getText());
		m.setCorrelatedEventIdPredicateDescription(correlatedEventIdPredicateDescription.getText());

		String xstreamPredicateXml = detailedXmlPredicateTextPane.getText();
		Predicate eventPredicate;
		try {
			eventPredicate = (Predicate) predicateXStream.fromXML(xstreamPredicateXml);
		} catch(Exception ex) {
			log.error("Failed to parse Predicate as xml (XStream format) ... using null, text : " + xstreamPredicateXml, ex);
			eventPredicate = null; // Error, failed to parse !!!
		}
		m.setEventPredicate(eventPredicate);
		
	}

	public void onApplyEditFilterFileButton(ActionEvent event) {
		updateView2Model(model);
	}

	public void onUndoEditFilterFileButton(ActionEvent event) {
		updateModel2View();
	}

	public void onSaveFilterFileButton(ActionEvent event) {
		if (model == null) {
		    return;
		}
	    updateView2Model(model);
	    RecordEventFilterFileUtils.saveFilterFile(model);
	}

	public void onReloadFilterFileButton(ActionEvent event) {
		RecordEventFilterFileUtils.loadFilterFile(model);
		updateModel2View();
	}

	public void onButtonCheckParse(ActionEvent event) {
		String inputText = detailedXmlPredicateTextPane.getText();
		String reformatText; 
		try {
			Predicate parsedPredicate = (Predicate) predicateXStream.fromXML(inputText);
			reformatText = predicateXStream.toXML(parsedPredicate);
		} catch(Exception ex) {
			reformatText = "<!-- PARSE ERROR ... " + ex.getMessage() + " -->\n" + 
					inputText;
		}
		detailedXmlPredicateTextPane.setText(reformatText);
	}
	
}
