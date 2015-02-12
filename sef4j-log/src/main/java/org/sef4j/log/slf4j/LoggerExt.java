package org.sef4j.log.slf4j;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;

/**
 * a slf4j Logger extension, for formatting name-values as a varargs list 
 * 
 * ... to be extended for enriching poor logback with MDC, or ElasticSearch-logback appender with richer document structure 
 * 
 * sample usages:
 * <code>
 * log.infoNV("text1 name1:", value1, " text2 name2:", value2); // <= detect ending as "<<name>>:", and append value
 * log.infoNV("text1 name1=", value1, " text2 name2=", value2); // detect ending as "<<name>>=", and append value
 * log.infoNV("text1 {{name1}}", value1, " text2 name2=", value2); // detect ending as "{{name}}", and replaced by value
 * 
 * log.info("text name1: ", value1, ", name2 :", value2, ", name3   :   ", value3);  // accept any whitespace before/after ':' and '=' 
 * </code> 
 *
 */
public class LoggerExt {

	public static enum LogLevel {
		OFF, TRACE, DEBUG, INFO, WARN, ERROR
	}

	
	private Logger slf4jLogger;
	
	// ------------------------------------------------------------------------

	public LoggerExt(Logger slf4jLogger) {
		this.slf4jLogger = slf4jLogger;
	}

	// ------------------------------------------------------------------------


	public void traceNV(String msgFragment0, Object value0) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		doLog(LogLevel.TRACE, b);
	}

	public void traceNV(String msgFragment0, Object value0, String msgFragment1, Object value1) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		doLog(LogLevel.TRACE, b);
	}

	public void traceNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		doLog(LogLevel.TRACE, b);
	}

	public void traceNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2, String msgFragment3, Object value3) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		appendMsgFragmentValue(b, 3, msgFragment3, value3);
		doLog(LogLevel.TRACE, b);
	}

	public void traceNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2, String msgFragment3, Object value3,
			Object... args) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		appendMsgFragmentValue(b, 3, msgFragment3, value3);
		appendMsgFragmentValues(b, 4, args);
		doLog(LogLevel.TRACE, b);
	}

	
	public void debugNV(String msgFragment0, Object value0) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		doLog(LogLevel.DEBUG, b);
	}

	public void debugNV(String msgFragment0, Object value0, String msgFragment1, Object value1) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		doLog(LogLevel.DEBUG, b);
	}

	public void debugNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		doLog(LogLevel.DEBUG, b);
	}

	public void debugNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2, String msgFragment3, Object value3) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		appendMsgFragmentValue(b, 3, msgFragment3, value3);
		doLog(LogLevel.DEBUG, b);
	}

	public void debugNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2, String msgFragment3, Object value3,
			Object... args) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		appendMsgFragmentValue(b, 3, msgFragment3, value3);
		appendMsgFragmentValues(b, 4, args);
		doLog(LogLevel.DEBUG, b);
	}

	
	
	public void infoNV(String msgFragment0, Object value0) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		doLog(LogLevel.INFO, b);
	}

	public void infoNV(String msgFragment0, Object value0, String msgFragment1, Object value1) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		doLog(LogLevel.INFO, b);
	}

	public void infoNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		doLog(LogLevel.INFO, b);
	}

	public void infoNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2, String msgFragment3, Object value3) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		appendMsgFragmentValue(b, 3, msgFragment3, value3);
		doLog(LogLevel.INFO, b);
	}

	public void infoNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2, String msgFragment3, Object value3,
			Object... args) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		appendMsgFragmentValue(b, 3, msgFragment3, value3);
		appendMsgFragmentValues(b, 4, args);
		doLog(LogLevel.INFO, b);
	}

	
	

	public void warnNV(String msgFragment0, Object value0) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		doLog(LogLevel.WARN, b);
	}

	public void warnNV(String msgFragment0, Object value0, String msgFragment1, Object value1) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		doLog(LogLevel.WARN, b);
	}

	public void warnNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		doLog(LogLevel.WARN, b);
	}

	public void warnNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2, String msgFragment3, Object value3) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		appendMsgFragmentValue(b, 3, msgFragment3, value3);
		doLog(LogLevel.WARN, b);
	}

	public void warnNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2, String msgFragment3, Object value3,
			Object... args) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		appendMsgFragmentValue(b, 3, msgFragment3, value3);
		appendMsgFragmentValues(b, 4, args);
		doLog(LogLevel.WARN, b);
	}

	

	public void errorNV(String msgFragment0, Object value0) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		doLog(LogLevel.ERROR, b);
	}

	public void errorNV(String msgFragment0, Object value0, String msgFragment1, Object value1) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		doLog(LogLevel.ERROR, b);
	}

	public void errorNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		doLog(LogLevel.ERROR, b);
	}

	public void errorNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2, String msgFragment3, Object value3) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		appendMsgFragmentValue(b, 3, msgFragment3, value3);
		doLog(LogLevel.ERROR, b);
	}

	public void errorNV(String msgFragment0, Object value0, String msgFragment1, Object value1, String msgFragment2, Object value2, String msgFragment3, Object value3,
			Object... args) {
		MsgBuilder b = new MsgBuilder();
		appendMsgFragmentValue(b, 0, msgFragment0, value0);
		appendMsgFragmentValue(b, 1, msgFragment1, value1);
		appendMsgFragmentValue(b, 2, msgFragment2, value2);
		appendMsgFragmentValue(b, 3, msgFragment3, value3);
		appendMsgFragmentValues(b, 4, args);
		doLog(LogLevel.ERROR, b);
	}


	// ------------------------------------------------------------------------

	protected void doLog(LogLevel logLevel, MsgBuilder b) {
		String text = b.text.toString();
		String templateText = b.templateText.toString();
		if (b.ex == null) {
			doLog(logLevel, text, templateText, b.values);
		} else {
			doLog(logLevel, text, templateText, b.values, b.ex);
		}
	}
	
	public boolean isEnabled(LogLevel logLevel) {
		switch(logLevel) {
		case OFF: return false;
		case TRACE: return slf4jLogger.isTraceEnabled();
		case DEBUG: return slf4jLogger.isDebugEnabled();
		case INFO: return slf4jLogger.isInfoEnabled();
		case WARN: return slf4jLogger.isWarnEnabled();
		case ERROR: return slf4jLogger.isErrorEnabled();
		default: return false;
		}
	}
	
	public void doLog(LogLevel logLevel, String text, String templateText, Map<String,Object> values, Throwable ex) {
		if (logLevel == null || ! isEnabled(logLevel)) {
			return;
		}
		switch(logLevel) {
		case OFF:
			break;
		case TRACE:
			slf4jLogger.trace(text, ex);
			break;
		case DEBUG:
			slf4jLogger.debug(text, ex);
			break;
		case INFO:
			slf4jLogger.info(text, ex);
			break;
		case WARN:
			slf4jLogger.warn(text, ex);
			break;
		case ERROR:
			slf4jLogger.error(text, ex);
			break;
		default:
			break;
		}
	}
	
	public void doLog(LogLevel logLevel, String text, String templateText, Map<String,Object> values) {
		if (logLevel == null || ! isEnabled(logLevel)) {
			return;
		}
		switch(logLevel) {
		case OFF:
			break;
		case TRACE:
			slf4jLogger.trace(text);
			break;
		case DEBUG:
			slf4jLogger.debug(text);
			break;
		case INFO:
			slf4jLogger.info(text);
			break;
		case WARN:
			slf4jLogger.warn(text);
			break;
		case ERROR:
			slf4jLogger.error(text);
			break;
		default:
			break;
		}
	}
	
	protected static void appendMsgFragmentValue(MsgBuilder b,
			int paramIndex, String msgFragment, Object value) {
		String paramName = parseEndingName(msgFragment, b);
		final StringBuilder text = b.text;
		final StringBuilder templateText = b.templateText;
		if (paramName != null) {
			if (b.endReplaceIndex != 0) {
				// substitute "{{name}}" with value in formatted text
				String textWithoutPlaceholder = msgFragment.substring(0, b.beginReplaceIndex);; 
				text.append(textWithoutPlaceholder);
				text.append(value);

				templateText.append(msgFragment);
			} else {
				// append value in formatted text, and append implicit "{{name}}" in templateText
				text.append(msgFragment);
				text.append(value);

				templateText.append(msgFragment);
				templateText.append("{{");
				templateText.append(paramName);
				templateText.append("}}");
			}
			b.values.put(paramName, value);
		} else {
			// error: bad formatted msgFragment, should ends with "{{name}}", "name:" or "name="
			// ... silently ignore name=value param !!! just append value
			text.append(msgFragment);
			text.append(value);

			templateText.append(msgFragment);
		}
	}
	
	protected static void appendMsgFragmentValues(MsgBuilder b, int paramIndex, Object... args) {
		if (args == null) return;
		final int argsLen = args.length;
		int i = 0;
		for(; i+1 < argsLen; i+=2, paramIndex++) {
			Object msgFragmentObj = args[i];
			Object value = args[i+1];
			if (msgFragmentObj == null || msgFragmentObj.getClass() != String.class) {
				// Bad args! should be "string", value, "string", value, ... 
				// => silently ignore!!
				continue;
			}
			String msgFragment = (String) msgFragmentObj;
			appendMsgFragmentValue(b, paramIndex, msgFragment, value);
		}
		if (i == argsLen-1) {
			// remaining argument should be an exception..  otherwised silently ignored
			Object remainingArgs = args[argsLen - 1];
			if (remainingArgs instanceof Throwable) {
				Throwable ex = (Throwable) remainingArgs;
				b.ex = ex;
			} else {
				// Bad remaining args.. should be an exception, or should insert a string before
				// => silently ignore!!
			}
		}
	}

	private static class MsgBuilder {
		StringBuilder text = new StringBuilder();
		StringBuilder templateText = new StringBuilder();
		Map<String,Object> values = new TreeMap<String,Object>();
		Throwable ex;

		int beginReplaceIndex;
		int endReplaceIndex;
		
	}

	protected static String parseEndingName(String msgFragment) {
		return parseEndingName(msgFragment, new MsgBuilder());
	}
	
	protected static String parseEndingName(String msgFragment, MsgBuilder msgBuilder) {
		msgBuilder.beginReplaceIndex = 0;
		msgBuilder.endReplaceIndex = 0;
		if (msgFragment == null) return null;
		String res;
		int idx = msgFragment.length() - 1;
		// skip leading whitespace
		while(Character.isWhitespace(msgFragment.charAt(idx))) {
			idx--;
			if (idx <= 0) return null;
		}
		char ch = msgFragment.charAt(idx);
		if (ch == '}') {
			// should ends with "{{..}}"
			idx--;
			if (idx-3 <= 0) return null;
			if (msgFragment.charAt(idx) == '}') {
				int endIndex = idx;
				// search matching "{{"
				while(msgFragment.charAt(idx) != '{') {
					idx--;
					if (idx <= 0) return null;
				}
				int beginIndex = idx + 1; 
				idx--;
				if (idx <= 0) return null;
				if (msgFragment.charAt(idx) != '{') return null;
				res = msgFragment.substring(beginIndex, endIndex);
				msgBuilder.beginReplaceIndex = beginIndex - 2;
				msgBuilder.endReplaceIndex = endIndex + 2;
			} else {
				return null;
			}
		} else if (ch == ':' || ch == '=') { // should ends with "<<name>>:" or "<<name>>="
			idx--;
			while(Character.isWhitespace(msgFragment.charAt(idx))) {
				idx--;
				if (idx <= 0) return null;
			}
			int endIndex = idx + 1;
			while(Character.isJavaIdentifierPart(msgFragment.charAt(idx))) {
				idx--;
				if (idx <= 0) return null;
			}
			int beginIndex = idx + 1; 
			res = msgFragment.substring(beginIndex, endIndex);
		} else {
			return null; // nothing detected!
		}
		return res;
	}
	
}
