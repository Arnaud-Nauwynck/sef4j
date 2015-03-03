package com.google.code.joto.eventrecorder.ext.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore;
import com.google.code.joto.eventrecorder.spy.log.EventStoreWriterLog4jAppender;
import com.google.code.joto.eventrecorder.spy.log.Log4jEventData;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;

/**
 * JUnit test for Log4j extension for RecordEventStore mechanism
 */
public class Log4jEventRecorderTest extends TestCase {

	public Log4jEventRecorderTest(String name) {
		super(name);
	}

	public void test1() {
		String eventType = "log4j";
		String loggerName = "a.b.Test";
		RecordEventStore eventStore = new DefaultMemoryRecordEventStore();
		RecordEventWriter eventWriter = eventStore.getEventWriter();

//		Hierarchy log4jHierarchy = new Hierarchy(null); // ??
		Logger eventLogger = Logger.getLogger(loggerName);

		// plug RecordEventStore into Log4j
		EventStoreWriterLog4jAppender eventAppender = 
			new EventStoreWriterLog4jAppender(eventWriter, eventType);
		eventLogger.addAppender(eventAppender);

		// now test logging events from log4j
		eventLogger.info("test message 1");
		
		assertEquals(1, eventStore.getEventsCount());
		List<RecordEventSummary> events = eventStore.getEvents(0, -1);
		assertEquals(1, events.size());
		RecordEventSummary event = events.get(0);
		RecordEventData eventData = eventStore.getEventData(event);
		assertEquals(1, event.getEventId());
		assertEquals(eventType, event.getEventType());
		assertEqualsLogEvent("INFO", loggerName, "test message 1", null, 
				eventData);
		
		
		eventLogger.info("test info message");
		eventLogger.warn("test warn message");
		eventLogger.warn("test warn message with ex", new Exception());
		eventLogger.error("test error message");
		Exception ex5 = new Exception();
		eventLogger.error("test error message with ex", ex5);

		assertEquals(6, eventStore.getEventsCount());
		events = eventStore.getEvents(0, -1);
		assertEquals(6, events.size());
		
		// assertion check for message with ex 
		RecordEventData eventData5 = eventStore.getEventData(events.get(5));
		assertEqualsLogEvent("ERROR", loggerName, "test error message with ex", ex5, eventData5);

		
	}
	
	private void assertEqualsLogEvent(
			String expectedLevel, String expectedLoggerName, 
			String expectedMessage, Throwable expectedEx,
			RecordEventData actualEventData) {
		RecordEventSummary event = actualEventData.getEventSummary();
		Log4jEventData log4jEventData = (Log4jEventData) actualEventData.getObjectData();
		assertNotNull(log4jEventData);
		
		assertEquals(expectedLevel, event.getEventSubType());
		assertEquals(expectedLoggerName, event.getEventMethodName());
		assertEquals(expectedMessage, event.getEventMethodDetail());

		if (expectedEx != null) {
			Throwable actualExCopy = log4jEventData.getThrowable();
			String[] actualExStrRep = log4jEventData.getThrowableStrRep();
			assertTrue(actualExCopy != null || actualExStrRep != null);
			String expectedExStr = exToString(expectedEx);
			// if not serializable...  assertNotNull(logEventData.getThrowableStrRep());
			if (actualExCopy != null) {
				// compare Exception (copy!)
				String actualExStr = exToString(actualExCopy);
				assertEquals(expectedExStr, actualExStr);
			} else {
				// TODO compare (non serializable?) Ex with string representation
				StringBuilder sb = new StringBuilder();
				for(String line : actualExStrRep) {
					sb.append(line);
					sb.append("\n");
				}
				assertEquals(expectedExStr, sb.toString());
			}
			
		} else {
			assertNull(log4jEventData.getThrowable());
			assertNull(log4jEventData.getThrowableStrRep());
		}
	}

	private static String exToString(Throwable ex) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
		PrintStream out = new PrintStream(buffer);
		ex.printStackTrace(out);
		out.flush();
		return buffer.toString();
	}
	
}
