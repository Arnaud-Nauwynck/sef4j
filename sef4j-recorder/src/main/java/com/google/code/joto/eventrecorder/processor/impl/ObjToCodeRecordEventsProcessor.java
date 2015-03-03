package com.google.code.joto.eventrecorder.processor.impl;

import java.io.PrintStream;

import com.google.code.joto.ObjectToCodeGenerator;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessorFactory;

/**
 * Formatter for converting RecordEvent(s) -> String as Java "new/call" code, 
 * using ObjectToCodeGenerator
 */
public class ObjToCodeRecordEventsProcessor implements RecordEventsProcessor {

	public static class Factory implements RecordEventsProcessorFactory<PrintStream> {
		private ObjectToCodeGenerator objToCode;
		
		public Factory(ObjectToCodeGenerator objToCode) {
			super();
			this.objToCode = objToCode;
		}

		@Override
		public RecordEventsProcessor create(PrintStream out) {
			return new ObjToCodeRecordEventsProcessor(objToCode, out);
		}
	}

	private ObjectToCodeGenerator objToCode;
	private PrintStream out;
	
	//-------------------------------------------------------------------------

	public ObjToCodeRecordEventsProcessor(ObjectToCodeGenerator objToCode, PrintStream out) {
		this.objToCode = objToCode;
		this.out = out;
	}

	// -------------------------------------------------------------------------
	
	@Override
	public boolean needEventObjectData() {
		return true;
	}

	@Override
	public void processEvent(RecordEventSummary event, Object eventData) {
		String simpleClassMethName = "";  
		String simpleClassName = event.getEventClassName();
		if (simpleClassName != null) {
			int indexLastDot = simpleClassName.lastIndexOf(".");
			if (indexLastDot != -1) {
				simpleClassName = simpleClassName.substring(indexLastDot + 1);
			}
			simpleClassMethName = simpleClassName + "."; 
		}
		simpleClassMethName += event.getEventMethodName();
		out.print("{ // evt:" + event.getEventId() 
				+ ", meth: " + simpleClassMethName + "\n");
		
		String stmtsStr = objToCode.objToStmtsString(eventData, "eventData");
		out.print(stmtsStr);
		
		out.print("\n} // \n");
		
		out.print("\n");
	}

	//-------------------------------------------------------------------------

	@Override
	public String toString() {
		return "ObjToCodeRecordEventsProcessor[..]";
	}
	
}
