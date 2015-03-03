package com.google.code.joto.eventrecorder.processor.impl;

import java.io.PrintStream;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessorFactory;
import com.thoughtworks.xstream.XStream;

/**
 * a simple Formatter, for converting RecordEvent(s) -> String as XStream dump 
 */
public class XStreamFormatterRecordEventsProcessor implements RecordEventsProcessor {

	public static class Factory implements RecordEventsProcessorFactory<PrintStream> {
		XStream xstream;
		
		public Factory(XStream xstream) {
			super();
			this.xstream = xstream;
		}

		@Override
		public RecordEventsProcessor create(PrintStream out) {
			return new XStreamFormatterRecordEventsProcessor(xstream, out);
		}
	}
	
	private XStream xstream;
	
	private PrintStream out;
	
	//-------------------------------------------------------------------------

	public XStreamFormatterRecordEventsProcessor(XStream xstream, PrintStream out) {
		this.xstream = xstream;
		this.out = out;
	}

	// -------------------------------------------------------------------------
	
	@Override
	public boolean needEventObjectData() {
		return true;
	}

	@Override
	public void processEvent(RecordEventSummary event, Object eventData) {
		out.print("<event "
				+ " id=\"" + event.getEventId() + "\""
				+ " type=\"" + event.getEventType() + "\""
				+ " subtype=\"" + event.getEventSubType() + "\""
				+ "\n"
				+ ((event.getEventMethodName() != null)? 
						" meth=\"" + event.getEventMethodName() + "\"" : "")
				+ ((event.getEventMethodDetail() != null)? 
						" meth=\"" + event.getEventMethodDetail() + "\"" : "")
				+ ">\n");
		
		String eventXml = xstream.toXML(eventData);
		out.print(eventXml);
		out.print("\n");
		
		out.print("</event>\n");
		out.print("\n");
	}
	
	//-------------------------------------------------------------------------

	@Override
	public String toString() {
		return "XStreamFormatterRecordEventsProcessor[..]";
	}
	
}
