package com.google.code.joto.ui.conv;

import java.awt.Component;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.code.joto.ObjectToCodeGenerator;
import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.processor.DispatcherRecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessorFactory;
import com.google.code.joto.eventrecorder.processor.impl.ObjToCodeRecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.impl.XStreamFormatterRecordEventsProcessor;
import com.google.code.joto.eventrecorder.spy.awtspy.AWTRecordEventWriterSpy;
import com.google.code.joto.eventrecorder.spy.calls.MethodCallEventUtils;
import com.google.code.joto.eventrecorder.spy.calls.MethodCallToCodeRecordEventsProcessor;
import com.google.code.joto.eventrecorder.spy.calls.ObjectReplacementMap;
import com.google.code.joto.eventrecorder.spy.log.Log4jEventData;
import com.google.code.joto.eventrecorder.spy.log.Log4jToCodeRecordEventsProcessor;
import com.google.code.joto.eventrecorder.spy.log.LogbackEventData;
import com.google.code.joto.eventrecorder.spy.log.LogbackToCodeRecordEventsProcessor;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.table.AbstractRecordEventTableModel;
import com.google.code.joto.ui.table.RecordEventTablePane;
import com.thoughtworks.xstream.XStream;

/**
 * Swing panel for selecting RecordEvent in table, 
 * and show pluggeable representations as Xml / JavaCode / JUnit / ... 
 */
public class RecordEventsTableAndConvertersPanel {

	private JotoContext context;
	
	private JSplitPane splitPane;
	
	private RecordEventTablePane recordEventTablePane;
	
	/**
	 * contains child component for displaying selected RecordEvent as Text.
	 * predefined tabs:
	 * <ul>
	 * <li>XStream dump (RecordEvent ObjectData -> Xml)</li>
	 * <li>Joto Reverse for Java code construction (RecordEvent ObjectData -> Java "new/call" code)</li>
	 */
	private JTabbedPane selectionTabbedPane;

	/** downcast helper... currently redundant with selectionTabbedPane! */
	private List<RecordEventsConverterTextPanel> selectedEventConverterTextPanes = 
		new ArrayList<RecordEventsConverterTextPanel>();
		
	// -------------------------------------------------------------------------

	public RecordEventsTableAndConvertersPanel(JotoContext context, AbstractRecordEventTableModel recordEventTableModel) {
		this.context = context;
		this.recordEventTablePane = new RecordEventTablePane(recordEventTableModel); 
		
		this.selectionTabbedPane = new JTabbedPane();

		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				recordEventTablePane.getJComponent(), selectionTabbedPane);
		splitPane.setDividerLocation(0.4);
		AWTRecordEventWriterSpy.setIgnoreComponentAwtEventSpy(splitPane);

		
		recordEventTablePane.getRecordEventTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				onRecordEventSelectionChanged(e);
			}
		});

		// predefined text converters:
		addDefaultXmlTextPanelConverter();
		addDefaultJavaConverterTextPanel();

	}

	public void addTextPanelConverter(String name, RecordEventsConverterTextPanel comp) {
		selectionTabbedPane.add(name, comp.getJComponent());
		selectedEventConverterTextPanes.add(comp);
	}

	public void removeTextPanelConverter(String name) {
		int tabLen = selectionTabbedPane.getTabCount();
		Component comp = null;
		for (int i = 0; i < tabLen; i++) {
			String n = selectionTabbedPane.getTitleAt(i);
			if (n != null && n.equals(name)) {
				comp = selectionTabbedPane.getTabComponentAt(i);
				selectionTabbedPane.remove(i);
				break;
			}
		}
		RecordEventsConverterTextPanel foundTextPane = null;
		for(RecordEventsConverterTextPanel elt : selectedEventConverterTextPanes) {
			if (elt.getJComponent() == comp) {
				foundTextPane = elt;
				break;
			}
		}
		selectedEventConverterTextPanes.remove(foundTextPane);
	}
	

	public void addDefaultXmlTextPanelConverter() {
		// Xml XStream text converter
		RecordEventsProcessorFactory<PrintStream> eventConverterFactory =
			new XStreamFormatterRecordEventsProcessor.Factory(new XStream());
		RecordEventsConverterTextPanel comp = 
			new RecordEventsConverterTextPanel(eventConverterFactory);
		addTextPanelConverter("xml", comp);
	}

	public void addDefaultJavaConverterTextPanel() {

		// Reverse Java "new/call" text converter
		ObjectToCodeGenerator objToCode = new ObjectToCodeGenerator(context.getConfig());
		
		RecordEventsProcessorFactory<PrintStream> objConverterFactory =
			new ObjToCodeRecordEventsProcessor.Factory(objToCode);
		
		ObjectReplacementMap objectReplacementMap = context.getObjReplMap();
		RecordEventsProcessorFactory<PrintStream> methCallConverterFactory =
			new MethodCallToCodeRecordEventsProcessor.Factory(
				objToCode, objectReplacementMap);

		RecordEventsProcessorFactory<PrintStream> logbackToCommentConverterFactory =
			new LogbackToCodeRecordEventsProcessor.Factory(true);

		RecordEventsProcessorFactory<PrintStream> log4jToCommentConverterFactory =
			new Log4jToCodeRecordEventsProcessor.Factory(true);

		Map<String,RecordEventsProcessorFactory<PrintStream>> eventTypeToFactory =
			new HashMap<String,RecordEventsProcessorFactory<PrintStream>>();
		eventTypeToFactory.put("testObj", objConverterFactory);
		eventTypeToFactory.put(MethodCallEventUtils.METHODCALL_EVENT_TYPE, methCallConverterFactory);
		eventTypeToFactory.put(LogbackEventData.EVENT_TYPE, logbackToCommentConverterFactory);
		eventTypeToFactory.put(Log4jEventData.EVENT_TYPE, log4jToCommentConverterFactory);
		
		RecordEventsProcessorFactory<PrintStream> dispatcherConverterFactory =
			new DispatcherRecordEventsProcessor.Factory<PrintStream>(
					eventTypeToFactory, objConverterFactory);
		
		RecordEventsConverterTextPanel comp = 
			new RecordEventsConverterTextPanel(dispatcherConverterFactory);
		addTextPanelConverter("java", comp);
	}

	//-------------------------------------------------------------------------

	public JComponent getJComponent() {
		return splitPane;
	}

	// -------------------------------------------------------------------------
	
	private void onRecordEventSelectionChanged(ListSelectionEvent e) {
		List<RecordEventSummary> selectedRows = recordEventTablePane.getSelectedEventRows();
		
		List<RecordEventData> selectedEventDataList = new ArrayList<RecordEventData>();
		RecordEventStore eventStore = context.getEventStore();
		for (RecordEventSummary eventRow : selectedRows) {
			RecordEventData eventData = eventStore.getEventData(eventRow);
			selectedEventDataList.add(eventData);
		}
		
		// display event data list in detailed tabbed pane
		for(RecordEventsConverterTextPanel comp : selectedEventConverterTextPanes) {
			comp.setRecordEventDataList(selectedEventDataList);
		}
	
	}

}
