package org.sef4j.callstack.handlers;

import java.util.Iterator;
import java.util.Map;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.CallStackPushPopHandler;
import org.sef4j.callstack.event.StackEvent.PopStackEvent;
import org.sef4j.callstack.event.StackEvent.ProgressStepStackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;
import org.sef4j.callstack.stats.ThreadTimeUtils;
import org.slf4j.Logger;

/**
 * adapter CallStackHandler push()/pop() -> Slf4j
 * 
 * cf similar "copy&paste code" Slf4jLoggerAdapterStackEventListener
 * (CallStackHandler -> StackEventListener -> Slf4j)
 */
public class Slf4jLoggerAdapterCallStackHandler extends CallStackPushPopHandler {

	private Logger slf4jLogger;
	
	// ------------------------------------------------------------------------
	
	public Slf4jLoggerAdapterCallStackHandler(Logger slf4jLogger) {
		this.slf4jLogger = slf4jLogger;
	}

	// ------------------------------------------------------------------------

	@Override
	public void onPush(CallStackElt stackElt) {
		String name = stackElt.getName();
		Map<String, Object> inheritedProps = stackElt.getInheritedProps();
		Map<String, Object> params = stackElt.getParams();
		int progressExpectedCount = stackElt.getProgressExpectedCount();
		String msg = formatLogMessagePush(name, inheritedProps, params, progressExpectedCount);
		slf4jLogger.info(msg);

		// register self handler on pushed stack elt
		stackElt.onPushAddCallStackPushPopHandler(this);
	}

	@Override
	public void onPop(CallStackElt stackElt) {
		String name = stackElt.getName();
		long elapsedTime = stackElt.getElapsedTime();
		String msg = formatLogMessagePop(name, elapsedTime);
		slf4jLogger.info(msg);
	}

	@Override
	public void onProgressStep(CallStackElt stackElt) {
		int progressIndex = stackElt.getProgressIndex();
		int progressExpectedCount = stackElt.getProgressExpectedCount();
		String progressMessage = stackElt.getProgressMessage();
		String msg = formatLogMessageProgress(progressIndex, progressExpectedCount, progressMessage);
		slf4jLogger.info(msg);
	}

	// ------------------------------------------------------------------------

	public static String formatLogMessagePush(String name, 
			Map<String, Object> inheritedProps, 
			Map<String, Object> params,
			int progressExpectedCount) {
		StringBuilder sb = new StringBuilder();
		sb.append("> ");
		sb.append(name);
		
		if (inheritedProps != null && !inheritedProps.isEmpty()) {
			sb.append(" [");
			mapToString(sb, inheritedProps);
			sb.append("]");
		}

		if (params != null && !params.isEmpty()) {
			sb.append(" (");
			mapToString(sb, params);
			sb.append(")");
		}
		
		if (progressExpectedCount != 0) {
			sb.append(" .. [0/");
			sb.append(progressExpectedCount);
			sb.append("]");
		}
		return sb.toString();
	}

	public static String formatLogMessagePop(String name, long elapsedTime) {
		StringBuilder sb = new StringBuilder();
		sb.append("< ");
		sb.append(name);

		sb.append(", took ");
		long millis = ThreadTimeUtils.nanosToApproxMillis(elapsedTime);
		sb.append(millis);
		sb.append(" ms");
		
		String msg = sb.toString();
		return msg;
	}

	public static String formatLogMessageProgress(int progressIndex, int progressExpectedCount, String messageProgress) {
		StringBuilder sb = new StringBuilder();

		sb.append(" .. [");
		sb.append(progressIndex);
		sb.append("/");
		sb.append(progressExpectedCount);
		if (messageProgress != null) {
			sb.append(": ");
			sb.append(messageProgress);
		}
		sb.append("]");
	
		String msg = sb.toString();
		return msg;
	}

	public static String formatLogMessageCompound(
			final PopStackEvent[] popEvents, 
			final PushStackEvent[] pushEvents,
			final ProgressStepStackEvent[] progressSteps) {
		StringBuilder sb = new StringBuilder();
		if (popEvents != null) {
			final int popEventsLength = popEvents.length;
			for (int i = 0; i < popEventsLength; i++) {
				sb.append("< ");
				sb.append(popEvents[i].getName());
				sb.append(" ");
			}
		}
		if (pushEvents != null) {
			final int pushEventsLength = pushEvents.length;
			for (int i = 0; i < pushEventsLength; i++) {
				sb.append("> ");
				PushStackEvent pushEvent = pushEvents[i];
				sb.append(pushEvent.getName());
				
				if (progressSteps != null 
						&& progressSteps[i] != null
						) {
					ProgressStepStackEvent progressStep = progressSteps[i];
					sb.append(" ...[");
					sb.append(progressStep.getProgressIndex());
					sb.append("/");
					sb.append(progressStep.getProgressExpectedCount());

					String progressMessage = progressStep.getProgressMessage();
					if (progressMessage != null) {
						sb.append(": ");
						sb.append(progressMessage);
					}
					sb.append("]");
				}
				sb.append(" ");
			}
		}
		String msg = sb.toString();
		return msg;
	}

	private static void mapToString(StringBuilder sb, Map<String, Object> map) {
		for (Iterator<Map.Entry<String, Object>> iter = map.entrySet().iterator(); iter.hasNext();) {
			Map.Entry<String, Object> e = iter.next();
			String key = e.getKey();
			Object value = e.getValue();
			sb.append(key);
			sb.append("=");
			sb.append(value);
			if (iter.hasNext()) {
				sb.append(", ");
			}
		}
	}

}
