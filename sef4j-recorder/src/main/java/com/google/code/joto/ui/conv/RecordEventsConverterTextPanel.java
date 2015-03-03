package com.google.code.joto.ui.conv;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessorFactory;
import com.google.code.joto.util.ui.ScrolledTextPane;

/**
 * a TextPanel (with ScrolledPane and misc toolbars), 
 * for displaying list of RecordEvent as text
 *
 */
public class RecordEventsConverterTextPanel {

	private RecordEventsProcessorFactory<PrintStream> converterFactory;

	private ScrolledTextPane textPane;

	protected List<RecordEventData> eventDataList;
	protected boolean needRecalc = false;
	
	//-------------------------------------------------------------------------

	public RecordEventsConverterTextPanel(RecordEventsProcessorFactory<PrintStream> converterFactory) {
		this.converterFactory = converterFactory;
		textPane = new ScrolledTextPane();
		
		
		JButton recalcButton = new JButton("calc");
		recalcButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				recalc();
			}
		});
		textPane.addToolbarComp(recalcButton);
		
		textPane.getJComponent().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				if (needRecalc) {
					recalc();
				}
			}
		});
	}

	//-------------------------------------------------------------------------

	public JComponent getJComponent() {
		return textPane.getJComponent();
	}
	
	public void setRecordEventDataList(List<RecordEventData> eventDataList) {
		this.eventDataList = eventDataList;
		if (textPane.getJComponent().isVisible()) {
			recalc();
		} else {
			needRecalc = true;
		}
	}
	
	protected void recalc() {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(buffer);
		try {
			RecordEventsProcessor converter = converterFactory.create(out);
			for(RecordEventData eventData : eventDataList) {
				RecordEventSummary event = eventData.getEventSummary();
				Object eventObjectData = eventData.getObjectData();
				converter.processEvent(event, eventObjectData);
			}
		} catch(Exception ex) {
			out.println();
			out.print("Failed to convert RecordEvent(s) to text!\n");
			ex.printStackTrace(out);
		}
		out.flush();
		String textResult = buffer.toString();
		textPane.setText(textResult);
	}
	
}
