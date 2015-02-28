package org.sef4j.callstack.event;

import java.io.Serializable;
import java.util.Map;

import org.sef4j.callstack.CallStackElt;

/**
 * Base class for call stack events: push, pop, progress, compound events
 * 
 * Sub-classes are serializable and immutable.
 * Events can be safely deferred for asynchronous sending.
 * 
 * Any subsequence of successive events can be compressed (with lost) into 1 CompoundStackEvent
 * (cf CompoundPopPushStackEventBuilder)
 */
public abstract class StackEvent implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	public abstract void visit(StackEventVisitor visitor);
	
	// ------------------------------------------------------------------------
	
	/**
	 * StackEvent for push()
	 */
	public static class PushStackEvent extends StackEvent {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private final String name;
		private final Map<String,Object> params;
		private final Map<String,Object> inheritedProps;
		private final long startTime;
		private final long threadCpuStartTime;
		private final long threadUserStartTime;
		private final int progressExpectedCount;
		
		
		public PushStackEvent(String name, 
				Map<String, Object> params, Map<String, Object> inheritedProps, 
				long startTime, long threadCpuStartTime, long threadUserStartTime, 
				int progressExpectedCount) {
			super();
			this.name = name;
			this.params = params;
			this.inheritedProps = inheritedProps;
			this.startTime = startTime;
			this.threadCpuStartTime = threadCpuStartTime;
			this.threadUserStartTime = threadUserStartTime;
			this.progressExpectedCount = progressExpectedCount;
		}

		public PushStackEvent(CallStackElt elt) {
			this.name = elt.getName();
			this.params = elt.getParams();
			this.inheritedProps = elt.getInheritedProps();
			this.startTime = elt.getStartTime();
			this.threadCpuStartTime = elt.getThreadCpuStartTime();
			this.threadUserStartTime = elt.getThreadUserStartTime();
			this.progressExpectedCount = elt.getProgressExpectedCount();
		}

		public void visit(StackEventVisitor visitor) {
			visitor.acceptPushStackEvent(this);
		}

		public String getName() {
			return name;
		}

		public Map<String, Object> getParams() {
			return params;
		}

		public Map<String, Object> getInheritedProps() {
			return inheritedProps;
		}

		public long getStartTime() {
			return startTime;
		}

		public long getThreadCpuStartTime() {
			return threadCpuStartTime;
		}

		public long getThreadUserStartTime() {
			return threadUserStartTime;
		}

		public int getProgressExpectedCount() {
			return progressExpectedCount;
		}

		@Override
		public String toString() {
			return "PushStackEvent [" + name + "]";
		}
		
	}

	// ------------------------------------------------------------------------
	
	/**
	 * StackEvent for pop()
	 */
	public static class PopStackEvent extends StackEvent {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private final String name;
		private final long elapsedTime;
		private final long endTime;
		private final long threadCpuEndTime;
		private final long threadUserEndTime;

		
		public PopStackEvent(String name, 
				long elapsedTime, 
				long endTime, long threadCpuEndTime, long threadUserEndTime) {
			super();
			this.name = name;
			this.elapsedTime = elapsedTime;
			this.endTime = endTime;
			this.threadCpuEndTime = threadCpuEndTime;
			this.threadUserEndTime = threadUserEndTime;
		}

		public PopStackEvent(CallStackElt elt) {
			this.name = elt.getName();
			this.elapsedTime = elt.getElapsedTime();
			this.endTime = elt.getEndTime();
			this.threadCpuEndTime = elt.getThreadCpuEndTime();
			this.threadUserEndTime = elt.getThreadUserEndTime();
		}

		public void visit(StackEventVisitor visitor) {
			visitor.acceptPopStackEvent(this);
		}
		
		public String getName() {
			return name;
		}
		
		public long getElapsedTime() {
			return elapsedTime;
		}

		public long getEndTime() {
			return endTime;
		}

		public long getThreadCpuEndTime() {
			return threadCpuEndTime;
		}

		public long getThreadUserEndTime() {
			return threadUserEndTime;
		}

		@Override
		public String toString() {
			return "PopStackEvent [" + name + "]";
		}

	}

	// ------------------------------------------------------------------------
	
	/**
	 * StackEvent for progress()
	 */
	public static class ProgressStepStackEvent extends StackEvent {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private final int progressIndex;
		private final int progressExpectedCount;
		private final String progressMessage;
		
		
		public ProgressStepStackEvent(int progressIndex, int progressExpectedCount, String progressMessage) {
			super();
			this.progressIndex = progressIndex;
			this.progressExpectedCount = progressExpectedCount;
			this.progressMessage = progressMessage;
		}

		public ProgressStepStackEvent(CallStackElt stackElt) {
			this.progressIndex = stackElt.getProgressIndex();
			this.progressExpectedCount = stackElt.getProgressExpectedCount();
			this.progressMessage = stackElt.getProgressMessage();
		}
		
		public void visit(StackEventVisitor visitor) {
			visitor.acceptProgressStackEvent(this);
		}

		public int getProgressIndex() {
			return progressIndex;
		}

		public int getProgressExpectedCount() {
			return progressExpectedCount;
		}

		public String getProgressMessage() {
			return progressMessage;
		}

		@Override
		public String toString() {
			return "ProgressStackEvent [" + progressIndex 
					+ ((progressMessage != null)? ": " + progressMessage : "")
					+ "]";
		}

	}

	// ------------------------------------------------------------------------
	
	/**
	 * StackEvent for sub-sequence compression of events
	 */
	public static class CompoundPopPushStackEvent extends StackEvent {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private final int skipPushPopEventsCount;
		private final PopStackEvent[] popEvents;
		private final PushStackEvent[] pushedEvents;
		private final ProgressStepStackEvent[] lastProgressStepEvents;
		
		public CompoundPopPushStackEvent(int skipPushPopEventsCount, 
				PopStackEvent[] popEvents, PushStackEvent[] pushedEvents, 
				ProgressStepStackEvent[] lastProgressStepEvents) {
			this.skipPushPopEventsCount = skipPushPopEventsCount;
			this.popEvents = popEvents;
			this.pushedEvents = pushedEvents;
			this.lastProgressStepEvents = lastProgressStepEvents;
		}

		public void visit(StackEventVisitor visitor) {
			visitor.acceptCompoundStackEvent(this);
		}
		
		public int getSkipPushPopEventsCount() {
			return skipPushPopEventsCount;
		}

		public PopStackEvent[] getPopEvents() {
			return popEvents;
		}

		public PushStackEvent[] getPushedEvents() {
			return pushedEvents;
		}

		public ProgressStepStackEvent[] getLastProgressStepEvents() {
			return lastProgressStepEvents;
		}

		@Override
		public String toString() {
			return "CompoundPopPushStackEvent["
					+ ((popEvents != null)? " " + popEvents.length + " pop(s)" : "")
					+ ((skipPushPopEventsCount != 0)? " " + skipPushPopEventsCount + " skip(s)": "")
					+ ((pushedEvents != null)? " " + pushedEvents.length + " push(s)" : "")
					+ "]";
		}

	}

}
