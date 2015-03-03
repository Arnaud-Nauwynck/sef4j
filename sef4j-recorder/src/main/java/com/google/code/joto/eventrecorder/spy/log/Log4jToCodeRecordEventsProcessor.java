package com.google.code.joto.eventrecorder.spy.log;

import java.io.PrintStream;

import com.google.code.joto.ast.beanstmt.impl.BeanASTPrettyPrinter;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessorFactory;

/**
 * Formatter for processing RecordEvent(s) corresponding to Log4j LoggingEvent
 * and converting to String as Java "log.info(msg)" code, or "// msg" comment code
 *
 */
public class Log4jToCodeRecordEventsProcessor implements RecordEventsProcessor {

	public static class Factory implements RecordEventsProcessorFactory<PrintStream> {
		private boolean convertToComment;
		public Factory(boolean convertToComment) {
			super();
			this.convertToComment = convertToComment;
		}

		@Override
		public RecordEventsProcessor create(PrintStream out) {
			return new Log4jToCodeRecordEventsProcessor(convertToComment, out);
		}
	}

	private boolean convertToComment;
	private PrintStream out;
	
	//-------------------------------------------------------------------------

	public Log4jToCodeRecordEventsProcessor(
			boolean convertToComment,
			PrintStream out) {
		this.convertToComment = convertToComment;
		this.out = out;
	}

	//-------------------------------------------------------------------------

	@Override
	public boolean needEventObjectData() {
		return true;
	}

	@Override
	public void processEvent(RecordEventSummary event, Object eventObjectData) {
		Log4jEventData eventData = (Log4jEventData) eventObjectData;
		
		String formattedMsg = eventData.getFormattedMessage(event);
		
		if (convertToComment) {
			String escapedMsg = formattedMsg.replace("\n", "\n//");  
			out.println("// " + escapedMsg);
			// TODO add more info (stack trace, ..)
		} else {
			String levelMeth = eventData.getLevel(event).toLowerCase();
			out.print("log." + levelMeth + "(");
			out.print(BeanASTPrettyPrinter.litteralToJava(formattedMsg));
			// TODO add more info (stack trace, ..)
			out.println(");");
		}
	}

	// -------------------------------------------------------------------------

	@Override
	public String toString() {
		return "LogbackToCodeRecordEventsProcessor[" 
			+ "convertToComment=" + convertToComment 
			+ "]";
	}
	
}
