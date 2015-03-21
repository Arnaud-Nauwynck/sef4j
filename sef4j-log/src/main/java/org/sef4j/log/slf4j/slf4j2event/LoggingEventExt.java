package org.sef4j.log.slf4j.slf4j2event;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sef4j.log.slf4j.LogLevel;
import org.sef4j.log.slf4j.LoggerExt;

import ch.qos.logback.classic.spi.IThrowableProxy;

/**
 * immutable serializable event representing an enriched Logback event 
 * with additionnal CallStack info  (applicative CallStackElt path, CallStackElt parameter and inherited properties)
 */
public class LoggingEventExt implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	private final long timeStamp;
	private final LogLevel level; // cf also ch.qos.logback.classic.Level ...
	private final String threadName;
	private final String loggerName;

	private final String message;
	private final Object[] argumentArray;
	
	private final String formattedMessage;
	  
	// ignored... ch.qos.logback.classic.spi.LoggerContextVO loggerContextVO;
	  
	private final ch.qos.logback.classic.spi.IThrowableProxy throwable;
	  
	// ignore ... private org.slf4j.Marker marker;
	  
	// ignore ... private Map getMDCPropertyMap();
	  
	// ignore ... private Map getMdc();
	
	// implicit from LocalCallStack (not in slf4j)
	private final String[] callStackPath;
	// implicit from LocalCallStack (not in slf4j)
	private final Map<String,Object> props;
	// implicit from LocalCallStack (not in slf4j)
	private final Map<String,Object> params;
	
	// ------------------------------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public LoggingEventExt(long timeStamp, LogLevel level, String threadName, String loggerName, String message, Object[] argumentArray,
			String formattedMessage, IThrowableProxy throwable,
			String[] callStackPath, Map<String,Object> props, Map<String,Object> params) {
		this.timeStamp = timeStamp;
		this.level = level;
		this.threadName = threadName;
		this.loggerName = loggerName;
		this.message = message;
		this.argumentArray = argumentArray;
		this.formattedMessage = formattedMessage;
		this.throwable = throwable;
		this.callStackPath = callStackPath;
		this.props = (props != null)? props : (Map<String,Object>)Collections.EMPTY_MAP;
		this.params = (params != null)? params : (Map<String,Object>)Collections.EMPTY_MAP;
	}

	// ------------------------------------------------------------------------

	public long getTimeStamp() {
		return timeStamp;
	}

	public LogLevel getLevel() {
		return level;
	}

	public String getThreadName() {
		return threadName;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public String getMessage() {
		return message;
	}

	public Object[] getArgumentArray() {
		return argumentArray;
	}

	public String getFormattedMessage() {
		return formattedMessage;
	}

	public ch.qos.logback.classic.spi.IThrowableProxy getThrowable() {
		return throwable;
	}
	
	public String[] getCallStackPath() {
		return callStackPath;
	}

	public Map<String, Object> getProps() {
		return props;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	// ------------------------------------------------------------------------

	public static class Builder {
		private long timeStamp;
		private LogLevel level;
		private String threadName;
		private String loggerName;
		private String message;
		private Object[] argumentArray;
		private String formattedMessage;
		// ignored... ch.qos.logback.classic.spi.LoggerContextVO loggerContextVO;
		private ch.qos.logback.classic.spi.IThrowableProxy throwable;
		// ignore ... private org.slf4j.Marker marker;
		// ignore ... private Map getMDCPropertyMap();
		// ignore ... private Map getMdc();

		// implicit from LocalCallStack (not in slf4j)
		private String[] callStackPath;
		// implicit from LocalCallStack (not in slf4j)
		private Map<String,Object> props;
		// implicit from LocalCallStack (not in slf4j)
		private Map<String,Object> params;
		
		public Builder() {
		}

		
		public LoggingEventExt build() {
			return new LoggingEventExt(timeStamp, level, threadName, loggerName, 
					message, argumentArray, formattedMessage, throwable, 
					callStackPath, props, params);
		}

		public Builder withTimeStamp(long timeStamp) {
			this.timeStamp = timeStamp;
			return this;
		}
		public Builder withLevel(LogLevel level) {
			this.level = level;
			return this;
		}
		public Builder withThreadName(String threadName) {
			this.threadName = threadName;
			return this;
		}
		public Builder withLoggerName(String loggerName) {
			this.loggerName = loggerName;
			return this;
		}
		public Builder withMessage(String message) {
			this.message = message;
			return this;
		}
		public Builder withArgumentArray(Object[] argumentArray) {
			this.argumentArray = argumentArray;
			return this;
		}
		public Builder withFormattedMessage(String formattedMessage) {
			this.formattedMessage = formattedMessage;
			return this;
		}
		public Builder withThrowable(ch.qos.logback.classic.spi.IThrowableProxy throwable) {
			this.throwable = throwable;
			return this;
		}

		public Builder withCallStackPath(String[] p) {
			this.callStackPath = p;
			return this;
		}

		public Builder withProps(Map<String,Object> p) {
			if (this.props == null) this.props = new HashMap<String,Object>();
			this.props.putAll(p);
			return this;
		}
		public Builder withParams(Map<String,Object> p) {
			if (this.params == null) this.params= new HashMap<String,Object>();
			this.params.putAll(p);
			return this;
		}

	}
	
}
