package org.sef4j.slf4jbridge.slf4j2event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sef4j.slf4jbridge.LoggingEventExt;
import org.sef4j.slf4jbridge.logback.Sef4jLogbackUtils;
import org.sef4j.slf4jbridge.utils.LogLevel;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;

/**
 * mapper for slf4j <code>ILoggingEvent</code> to <code>LoggingEventExt</code>

 * <PRE>
 *                               EventEnrichers
 *                                    |
 *                                    \/            
 *     ILoggingEvent - - - new - - ->    - -> LoggingEventExt
 *    
 * </PRE>
 *
 */
public class Slf4jToLoggingEventExtMapper {

    private List<Slf4jToEventEnricher> slf4jToEventEnrichers;
    private List<EventEnricher> eventEnrichers;
    
    public Slf4jToLoggingEventExtMapper() {
	this(new ArrayList<>(), new ArrayList<>());
    }
    
    public Slf4jToLoggingEventExtMapper(
	    List<Slf4jToEventEnricher> slf4jToEventEnrichers,
	    List<EventEnricher> eventEnrichers) {
	this.slf4jToEventEnrichers = slf4jToEventEnrichers;
	this.eventEnrichers = eventEnrichers;
    }

    public boolean addSlf4jToEventEnricher(Slf4jToEventEnricher p) {
	return slf4jToEventEnrichers.add(p);
    }

    public boolean removeSlf4jToEventEnricher(Slf4jToEventEnricher p) {
	return slf4jToEventEnrichers.remove(p);
    }
    
    public boolean addEventEnricher(EventEnricher e) {
	return eventEnrichers.add(e);
    }

    public boolean removeEventEnricher(EventEnricher p) {
	return eventEnrichers.remove(p);
    }

    public LoggingEventExt logbackEventToEventExt(ILoggingEvent slf4jEvent) {
	LoggingEventExt.Builder evtB = new LoggingEventExt.Builder();

	evtB.withTimeStamp(slf4jEvent.getTimeStamp());
	evtB.withLevel(Sef4jLogbackUtils.slf4jLevelToLogLevel(slf4jEvent.getLevel()));
	evtB.withThreadName(slf4jEvent.getThreadName());
	evtB.withLoggerName(slf4jEvent.getLoggerName());

	evtB.withMessage(slf4jEvent.getMessage());
	evtB.withArgumentArray(slf4jEvent.getArgumentArray());
	evtB.withFormattedMessage(slf4jEvent.getFormattedMessage());

	IThrowableProxy throwableProxy = slf4jEvent.getThrowableProxy();
	if (throwableProxy != null) {
	    evtB.withThrowable(throwableProxy);
	}

	// enrich event
	for(EventEnricher enricher : eventEnrichers) {
	    enricher.enrich(evtB);
	}
	// enrich event
	for(Slf4jToEventEnricher enricher : slf4jToEventEnrichers) {
	    enricher.enrich(evtB, slf4jEvent);
	}

//	// *** also complete with current LocalCallStack ***
//	fillWithCurrLocalCallStackProps(evtB, true, true, true);

	LoggingEventExt evt = evtB.build();
	return evt;
    }

    public LoggingEventExt buildEvent(String loggerName, LogLevel logLevel, String text, String templateText,
	    boolean fillCallStackPath, boolean fillInheritedProps, boolean fillParams, Map<String, Object> values,
	    Throwable ex) {
	LoggingEventExt.Builder evtB = new LoggingEventExt.Builder();

	evtB.withTimeStamp(System.currentTimeMillis());
	evtB.withLevel(logLevel);
	evtB.withThreadName(Thread.currentThread().getName());
	evtB.withLoggerName(loggerName);

	evtB.withMessage(templateText);
	// evtB.withArgumentArray(argumentArray);
	evtB.withFormattedMessage(text);

	if (ex != null) {
	    IThrowableProxy throwableProxy = new ThrowableProxy(ex);
	    evtB.withThrowable(throwableProxy);
	}

	// enrich event
	for(EventEnricher enricher : eventEnrichers) {
	    enricher.enrich(evtB);
	}
	
	
//	// *** also complete with current LocalCallStack ***
//	fillWithCurrLocalCallStackProps(evtB, fillCallStackPath, fillInheritedProps, fillParams);

	// override with explicit name-values
	if (values != null && !values.isEmpty()) {
	    evtB.withParams(values);
	}

	LoggingEventExt evt = evtB.build();
	return evt;
    }

//    /**
//     * complete with current LocalCallStack = thread info, info not in slf4j ..
//     * should not use AsyncAppender to wrap this appender !
//     * 
//     * @param evtB
//     */
//    public static void fillWithCurrLocalCallStackProps(LoggingEventExt.Builder evtB, boolean fillCallStackPath,
//	    boolean fillInheritedProps, boolean fillParams) {
//	CallStackElt currThreadStackElt = LocalCallStack.currThreadStackElt();
//	if (fillCallStackPath) {
//	    String[] path = currThreadStackElt.getPath();
//	    evtB.withCallStackPath(path);
//	}
//	if (fillInheritedProps) {
//	    Map<String, Object> inheritedProps = currThreadStackElt.getInheritedProps();
//	    if (inheritedProps != null && !inheritedProps.isEmpty()) {
//		evtB.withProps(inheritedProps);
//	    }
//	}
//	if (fillParams) {
//	    Map<String, Object> params = currThreadStackElt.getParams();
//	    if (params != null && !params.isEmpty()) {
//		evtB.withParams(params);
//	    }
//	}
//    }
}
