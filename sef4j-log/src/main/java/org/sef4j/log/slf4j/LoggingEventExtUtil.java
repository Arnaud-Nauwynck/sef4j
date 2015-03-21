package org.sef4j.log.slf4j;

import java.util.Map;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.log.slf4j.LoggerExt.LogLevel;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;

public class LoggingEventExtUtil {

    public static LoggingEventExt slf4jEventToEvent(ILoggingEvent slf4jEvent) {
        LoggingEventExt.Builder evtB = new LoggingEventExt.Builder();
        
        evtB.withTimeStamp(slf4jEvent.getTimeStamp());
        evtB.withLevel(slf4jLevelToLogLevel(slf4jEvent.getLevel()));
        evtB.withThreadName(slf4jEvent.getThreadName());
        evtB.withLoggerName(slf4jEvent.getLoggerName());

        evtB.withMessage(slf4jEvent.getMessage());
        evtB.withArgumentArray(slf4jEvent.getArgumentArray());
        evtB.withFormattedMessage(slf4jEvent.getFormattedMessage());

        IThrowableProxy throwableProxy = slf4jEvent.getThrowableProxy();
        if(throwableProxy != null) {
            evtB.withThrowable(throwableProxy);
        }
        
        // *** also complete with current LocalCallStack ***
        fillWithCurrLocalCallStackProps(evtB, true, true, true);
        
        LoggingEventExt evt = evtB.build();
        return evt;
    }

    public static LoggingEventExt buildEvent(String loggerName,
            LogLevel logLevel, String text, String templateText, 
            boolean fillCallStackPath, boolean fillInheritedProps, boolean fillParams,
            Map<String, Object> values, Throwable ex) {
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
        
        // *** also complete with current LocalCallStack ***
        fillWithCurrLocalCallStackProps(evtB, fillCallStackPath, fillInheritedProps, fillParams);
        
        // override with explicit name-values
        if (values != null && !values.isEmpty()) {
            evtB.withParams(values);
        }
        
        LoggingEventExt evt = evtB.build();
        return evt;
    }
    

    
    /**
     * complete with current LocalCallStack = thread info, info not in slf4j .. should not use AsyncAppender to wrap this appender !
     * @param evtB
     */
    public static void fillWithCurrLocalCallStackProps(LoggingEventExt.Builder evtB, 
            boolean fillCallStackPath, boolean fillInheritedProps, boolean fillParams) {
        CallStackElt currThreadStackElt = LocalCallStack.currThreadStackElt();
        if (fillCallStackPath) {
            String[] path = currThreadStackElt.getPath();
            evtB.withCallStackPath(path);
        }
        if (fillInheritedProps) {
            Map<String, Object> inheritedProps = currThreadStackElt.getInheritedProps();
            if (inheritedProps != null && !inheritedProps.isEmpty()) {
                evtB.withProps(inheritedProps);
            }
        }
        if (fillParams) {
            Map<String, Object> params = currThreadStackElt.getParams();
            if (params != null && !params.isEmpty()) {
                evtB.withParams(params);
            }
        }
    }
}
