package org.sef4j.log.slf4j;

import java.util.Map;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.LocalCallStack;
import org.sef4j.core.api.EventLogger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.AppenderBase;

/**
 * Adapter Slf4j -> EventLogger
 * 
 * slf4j logger appender, to transform log events into structured event to send to EventLogger
 * 
 * this class transform the logging event ILoggingEvent into a richer event LoggingEventExt, 
 * by adding information from LocalCallSTack : path + parameters + inherited properties 
 */
public class EventLoggerAdapterAppender extends AppenderBase<ILoggingEvent> {

	private EventLogger targetEventLogger;
	
	// ------------------------------------------------------------------------
	
	public EventLoggerAdapterAppender(EventLogger targetEventLogger) {
		this.targetEventLogger = targetEventLogger;
	}

	// ------------------------------------------------------------------------
	
	@Override
	protected void append(ILoggingEvent slf4jEvent) {
		LoggingEventExt.Builder evtB = new LoggingEventExt.Builder();
		
		evtB.withTimeStamp(slf4jEvent.getTimeStamp());
		evtB.withLevel(slf4jEvent.getLevel());
		evtB.withThreadName(slf4jEvent.getThreadName());
		evtB.withLoggerName(slf4jEvent.getLoggerName());

		evtB.withMessage(slf4jEvent.getMessage());
		evtB.withArgumentArray(slf4jEvent.getArgumentArray());
		evtB.withFormattedMessage(slf4jEvent.getFormattedMessage());

		IThrowableProxy throwableProxy = slf4jEvent.getThrowableProxy();
		if(throwableProxy != null) {
			evtB.withThrowable(throwableProxy);
		}

		
		CallStackElt currThreadStackElt = LocalCallStack.currThreadStackElt();
		String[] path = currThreadStackElt.getPath();
		evtB.withCallStackPath(path);
		
		Map<String, Object> inheritedProps = currThreadStackElt.getInheritedProps();
		if (inheritedProps != null && !inheritedProps.isEmpty()) {
			evtB.withProps(inheritedProps);
		}
		Map<String, Object> params = currThreadStackElt.getParams();
		if (params != null && !params.isEmpty()) {
			evtB.withParams(params);
		}
		
		LoggingEventExt evt = evtB.build();
		
		// *** delegate to ***
		targetEventLogger.sendEvent(evt);
	}

}
