package org.sef4j.sample;


import java.util.Arrays;
import java.util.Map;

import org.sef4j.api.EventAppender;
import org.sef4j.slf4jbridge.LoggerExt;
import org.sef4j.slf4jbridge.LoggerExtFactory;
import org.sef4j.slf4jbridge.LoggingEventExt;
import org.sef4j.slf4jbridge.LoggingEventExt.Builder;
import org.sef4j.slf4jbridge.logback.EventAppenderFromLogbackAppender;
import org.sef4j.slf4jbridge.slf4j2event.EventEnricher;
import org.sef4j.slf4jbridge.slf4j2event.Slf4jToEventEnricher;
import org.sef4j.slf4jbridge.slf4j2event.Slf4jToLoggingEventExtMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class Sef4jSampleApp {

    private static final Logger log = LoggerFactory.getLogger(Sef4jSampleApp.class);
    private static final LoggerExt logExt;
    static {
	Slf4jToEventEnricher slf4jToEventEnricher = new Slf4jToEventEnricher() {
	    @Override
	    public void enrich(Builder eventBuilder, ILoggingEvent slf4jEvent) {
		Map<String, String> mdc = slf4jEvent.getMDCPropertyMap();
		String mdcParam1 = mdc.get("mdcParam1");
		if (mdcParam1 != null) {
		    eventBuilder.withParam("slf4j-mdcParam1", mdcParam1);
		}
	    }
	};
	
	EventEnricher eventEnricher = new EventEnricher() {
	    @Override
	    public void enrich(Builder eventBuilder) {
		eventBuilder.withProps("app", "sample-app");
	    }
	};
	Slf4jToLoggingEventExtMapper sl4fToEventMapper = new Slf4jToLoggingEventExtMapper(
		Arrays.asList(slf4jToEventEnricher),
		Arrays.asList(eventEnricher));
	LoggerExt.setDefaultFactory(new LoggerExtFactory(
		LoggerFactory.getILoggerFactory(), sl4fToEventMapper));
	logExt = LoggerExt.getLogger(Sef4jSampleApp.class);
    }
    
    
    public static void main(String[] args) {
	try {
	    new Sef4jSampleApp().run();
	} catch(Exception ex) {
	    System.err.println("Failed exiting");
	    ex.printStackTrace();
	}
    }
    
    public void run() {
	EventAppender<Object> eventToJsonSysout = createObjectToJsonSysoutEventAppender();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	EventAppender<LoggingEventExt> logEventToJsonSysout = (EventAppender<LoggingEventExt>) (EventAppender) eventToJsonSysout;
	
	EventAppenderFromLogbackAppender logbackCapturingAppender = 
		new EventAppenderFromLogbackAppender(logEventToJsonSysout, 
			LoggerExt.getDefaultFactory().getSlf4jToEventExtMapper());
	logbackCapturingAppender.start();
	ch.qos.logback.classic.Logger rootLogbackLogger = 
		(ch.qos.logback.classic.Logger) LoggerFactory.getLogger("ROOT");
	rootLogbackLogger.addAppender(logbackCapturingAppender);

	
	// send logs to logback 
	// ... also captured as LoggingEventExt in json (cf ElasticSearch)
	log.info("test");
	System.out.println();
	
	// send enriched logs with parameters in json (cf ElasticSearch)
	// ... also formatted to logback
	int intValue = 1;
	String strValue = "hello";
	logExt.infoNV("test intValue:", intValue, " strValue:", strValue);
	System.out.println();
	
	// direct send rich event in json (cf ElasticSearch)
	eventToJsonSysout.sendEvent(new DummyEvent(1, "abc"));
	System.out.println();
    }

    private EventAppender<Object> createObjectToJsonSysoutEventAppender() {
	EventAppender<Object> eventToJsonSysout = new EventAppender<Object>() {
	    ObjectMapper jsonMapper = new ObjectMapper();
	    public void sendEvent(Object event) {
		String json;
		try {
		    json = jsonMapper.writeValueAsString(event);
		    System.out.println("event->json " + json);
		} catch (JsonProcessingException e) {
		    System.out.println("event->json FAILED to convert object to json " + e.getMessage());
		}
	    }
	};
	return eventToJsonSysout;
    }

}
