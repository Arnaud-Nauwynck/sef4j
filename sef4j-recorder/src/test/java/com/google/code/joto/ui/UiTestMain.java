package com.google.code.joto.ui;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;

import com.google.code.joto.JotoConfig;
import com.google.code.joto.eventrecorder.RecordEventStore;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.ext.calls.DefaultSerializableFoo;
import com.google.code.joto.eventrecorder.ext.calls.IFoo;
import com.google.code.joto.eventrecorder.ext.calls.MethodEventWriterInvocationHandlerTest;
import com.google.code.joto.eventrecorder.impl.DefaultMemoryRecordEventStore;
import com.google.code.joto.eventrecorder.spy.log.EventStoreWriterLog4jAppender;
import com.google.code.joto.eventrecorder.spy.log.EventStoreWriterLogbackAppender;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;
import com.google.code.joto.testobj.Pt;
import com.google.code.joto.testobj.TestObjFactory;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.JotoContextFacadePanel;

public class UiTestMain {

	private static Logger log = LoggerFactory.getLogger(UiTestMain.class);
	
	public static void main(String[] args) {
		JotoConfig config = new JotoConfig();  
		RecordEventStore eventStore = new DefaultMemoryRecordEventStore();
		RecordEventWriter eventWriter = eventStore.getEventWriter(); 

		JotoContext context = new JotoContext(config, eventStore); 

		// record Serializable POJO
		doRecordEventObj(eventStore, "SimpleIntFieldA", TestObjFactory.createSimpleIntFieldA());
		// doRecordEventObj(eventStore, "SimpleRefObjectFieldA", TestObjFactory.createSimpleRefObjectFieldA());
		doRecordEventObj(eventStore, "A", TestObjFactory.createBeanA());
		doRecordEventObj(eventStore, "A2", TestObjFactory.createBeanA2());
		doRecordEventObj(eventStore, "SimpleRefBean_Cyclic", TestObjFactory.createSimpleRefBean_Cyclic());
		doRecordEventObj(eventStore, "Pt", new Pt(1, 2));

		// also record method calls using java.lang.reflect.Proxy + interface
		{
			IFoo fooImpl = new DefaultSerializableFoo();
			IFoo fooProxy =  MethodEventWriterInvocationHandlerTest.createFooProxyRecorder(fooImpl, eventWriter);
			
			MethodEventWriterInvocationHandlerTest.doCallFooMethods(fooProxy);
		}
		
		{ // record events using Logback event Writer
			String eventType = "logback";
			String loggerName = "a.b.Test";

			LoggerContext loggerContext = new LoggerContext();
			loggerContext.reset();
			Logger logger = loggerContext.getLogger(loggerName);

			EventStoreWriterLogbackAppender eventAppender = 
				new EventStoreWriterLogbackAppender(eventWriter, eventType);
			eventAppender.start();
			((ch.qos.logback.classic.Logger) logger).addAppender(eventAppender);
			
			// now test logging events from log4j
			logger.info("test info message");
			logger.warn("test warn message");
			logger.warn("test warn message with ex", new Exception());
			logger.error("test error message");
			logger.info("test info message multiline\n... message line 2\n...message line 3");

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
			cal.set(1999, 12, 31, 23, 59, 59);
			logger.info("test info message with arguments: str={} int={} date={}", new Object[] { "test", 123, cal.getTime() });
		}

		{ // record events using deprecated log4j event Writer
			String eventType = "log4j";
			String loggerName = "a.b.Test";

			org.apache.log4j.Logger logger = 
				org.apache.log4j.Logger.getLogger(loggerName);

			EventStoreWriterLog4jAppender eventAppender = 
				new EventStoreWriterLog4jAppender(eventWriter, eventType);
			((org.apache.log4j.Logger) logger).addAppender(eventAppender);
			
			// now test logging events from log4j
			logger.info("test info message");
			logger.warn("test warn message");
			logger.warn("test warn message with ex", new Exception());
			logger.error("test error message");
			logger.info("test info message multiline\n... message line 2\n...message line 3");
		}
		
		JotoContextFacadePanel jotoPanel = new JotoContextFacadePanel(context);
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(jotoPanel.getJComponent());
		frame.pack();
		frame.setVisible(true);
		
	}

	private static void doRecordEventObj(RecordEventStore eventStore,
			String methodName, Serializable objData) {
		
		RecordEventSummary evt = new RecordEventSummary(-1);
		evt.setEventDate(new Date());
		evt.setEventType("testObj");
		evt.setEventSubType("testObj SubType"); 
		evt.setEventMethodName(methodName);

		try {
			eventStore.addEvent(evt, objData);
		} catch(Exception ex) {
			log.warn("Failed to serialize?.. ignore", ex);
		}
	}
}
