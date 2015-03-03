package com.google.code.joto.eventrecorder.spy.log;

import java.io.PrintStream;
import java.text.MessageFormat;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessor;
import com.google.code.joto.eventrecorder.processor.RecordEventsProcessorFactory;

/**
 * Processor for handling RecordEventSummary+Object eventData,
 * when Type is a LogEventData... and reformat it as a Log4j event message
 */
public class Log4jFormatEventRecordEventsProcessor implements RecordEventsProcessor {

	public static class Factory implements RecordEventsProcessorFactory<PrintStream> {
		MessageFormat messageFormat;
		
		public Factory(MessageFormat messageFormat) {
			super();
			this.messageFormat = messageFormat;
		}

		@Override
		public RecordEventsProcessor create(PrintStream out) {
			return new Log4jFormatEventRecordEventsProcessor(messageFormat, out);
		}
	}

	// -------------------------------------------------------------------------
	
	private PrintStream out;
	
	/**
	 * use:
	 * 
	 * {0} date
	 * {1} threadName
	 * {2} severity
	 * {3} loggerName
	 * {4} msg
	 * 
	 *  default value: DEFAULT_MESSAGE_FORMAT
	 */
	private MessageFormat messageFormat = DEFAULT_MESSAGE_FORMAT;
	
	private static final MessageFormat DEFAULT_MESSAGE_FORMAT =
		new MessageFormat("{0} [{1}] {2} {3} {4}");
	
	//-------------------------------------------------------------------------

	public Log4jFormatEventRecordEventsProcessor(MessageFormat messageFormat, PrintStream out) {
		this.messageFormat = messageFormat;
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

		Object args= new Object[] {
				event.getEventDate(),
				event.getThreadName(),
				event.getEventSubType(),
				event.getEventMethodName(),
				event.getEventMethodDetail()
		};
		String logEventText = messageFormat.format(args);
		out.println(logEventText);
		
		if (eventData.getThrowable() != null) {
			eventData.getThrowable().printStackTrace(out);
		} else if (eventData.getThrowableStrRep() != null) {
			String[] lines = eventData.getThrowableStrRep();
			for (String line : lines) {
				out.println(line);
			}
		}
	}

	 
}
