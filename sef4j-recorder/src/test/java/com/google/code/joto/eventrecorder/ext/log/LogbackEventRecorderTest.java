package com.google.code.joto.eventrecorder.ext.log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import junit.framework.TestCase;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

import com.google.code.joto.eventrecorder.RecordEventData;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore;
import com.google.code.joto.eventrecorder.spy.log.EventStoreWriterLogbackAppender;
import com.google.code.joto.eventrecorder.spy.log.LogbackEventData;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;

/**
 * JUnit test for Logback extension for RecordEventStore mechanism
 */
public class LogbackEventRecorderTest extends TestCase {

	public LogbackEventRecorderTest(String name) {
		super(name);
	}

	public void test1() {
		String eventType = "log4j";
		String loggerName = "a.b.Test";
		RecordEventStore eventStore = new DefaultMemoryRecordEventStore();
		RecordEventWriter eventWriter = eventStore.getEventWriter();

		LoggerContext loggerContext = new LoggerContext();
		loggerContext.reset();
		Logger logger = loggerContext.getLogger(loggerName);

		// plug RecordEventStore into Log4j
		EventStoreWriterLogbackAppender eventAppender = 
			new EventStoreWriterLogbackAppender(eventWriter, eventType);
		eventAppender.start();
		logger.addAppender(eventAppender);
		
		// now test logging events from log4j
		logger.info("test message 1");
		
		assertEquals(1, eventStore.getEventsCount());
		List<RecordEventSummary> events = eventStore.getEvents(0, -1);
		assertEquals(1, events.size());
		RecordEventSummary event = events.get(0);
		RecordEventData eventData = eventStore.getEventData(event);
		assertEquals(1, event.getEventId());
		assertEquals(eventType, event.getEventType());
		String msg = "test message 1";
		assertEqualsLogEvent("INFO", loggerName, msg, msg, null, null, eventData);
		
		
		logger.info("test info message");
		logger.warn("test warn message");
		logger.warn("test warn message with ex", new Exception());
		logger.error("test error message");

		{ // log + assertion for message with Exception
			String msg5 = "test error message with ex";
			Exception ex5 = new Exception();
			logger.error(msg5, ex5);
			assertEquals(6, eventStore.getEventsCount());
	
			// assertion check for message 5 with ex 
			events = eventStore.getEvents(0, -1);
			assertEquals(6, events.size());
			RecordEventData eventData5 = eventStore.getEventData(events.get(5));
			assertEqualsLogEvent("ERROR", loggerName, msg5, msg5, null, ex5, eventData5);
		}
		
		{ // log + assertion check for message with formatted argument
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
			cal.set(1999, 12, 31, 23, 59, 59);
			Object[] argArray = new Object[] { "test", 123, cal.getTime() };
			String msg6 = "test info message with arguments: str={} int={} date={}";
			logger.info(msg6, argArray);
			assertEquals(7, eventStore.getEventsCount());

			events = eventStore.getEvents(0, -1);
			assertEquals(7, events.size());
			RecordEventData eventData6 = eventStore.getEventData(events.get(6));
			String formattedMsg6 = "test info message with arguments: str=test int=123 date=Tue Feb 01 00:59:59 GMT+01:00 2000";
			assertEqualsLogEvent("INFO", loggerName, formattedMsg6, msg6, argArray, null, eventData6);
		}
	}
	
	private void assertEqualsLogEvent(
			String expectedLevel, String expectedLoggerName, 
			String expectedFormattedMessage, 
			String expectedMessage, Object[] arguments, 
			Throwable expectedEx,
			RecordEventData actualEventData) {
		RecordEventSummary event = actualEventData.getEventSummary();
		LogbackEventData logEventData = (LogbackEventData) actualEventData.getObjectData();
		assertNotNull(logEventData);
		
		assertEquals(expectedLevel, event.getEventSubType());
		assertEquals(expectedLoggerName, event.getEventMethodName());
		assertEquals(expectedMessage, event.getEventMethodDetail());
		
		assertEquals(expectedFormattedMessage, logEventData.getFormattedMessage());
		Object[] actualArguments = logEventData.getArgumentArray();
		if (arguments != null && arguments.length != 0) {
			assertEquals(arguments.length, actualArguments.length); 
			// todo add assertion on args..
		} else {
			assertTrue(actualArguments == null || actualArguments.length == 0);
		}
		
		if (expectedEx != null) {
			assertNotNull(logEventData.getStackTraceElements());
			// TODO add more assertions...
		} else {
			assertNull(logEventData.getStackTraceElements());
		}
	}
	
}
