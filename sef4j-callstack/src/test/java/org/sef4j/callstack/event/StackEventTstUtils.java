package org.sef4j.callstack.event;

import org.sef4j.callstack.event.StackEvent.PopStackEvent;
import org.sef4j.callstack.event.StackEvent.ProgressStepStackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;


public class StackEventTstUtils {


	public static PushStackEvent newPush(String name) {
		return new PushStackEvent(name,
				null, null, // params, props
				0, 0, 0, // <= start times
				0);
	}

	public static PopStackEvent newPop(String name) {
		return new PopStackEvent(name,
				0, 0, 0, 0);// <= elapsed, end times
	}

	public static ProgressStepStackEvent newProgress(int progressIndex, int progressExpectedCount, String progressMessage) {
		return new ProgressStepStackEvent(progressIndex, progressExpectedCount, progressMessage);
	}

}
