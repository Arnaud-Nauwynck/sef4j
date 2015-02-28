package org.sef4j.callstack.event.impl;

import java.util.ArrayList;
import java.util.List;

import org.sef4j.callstack.event.StackEvent;
import org.sef4j.callstack.event.StackEvent.CompoundPopPushStackEvent;
import org.sef4j.callstack.event.StackEventVisitor;

/**
 * StackEvent aggregator: accpt sequence of StackEvents and build a corresponding CompoundPopPushStackEvent
 * 
 * design-patterns: Visitor of StackEvent AST class + Builder of CompoundPopPushStackEvent
 */
public class CompoundPopPushStackEventBuilder extends StackEventVisitor {
	
	private Object lock = new Object();
	
	private int levelDiff;
	private int skipPushPopEventsCount;
	private List<StackEvent.PopStackEvent> popEvents = new ArrayList<StackEvent.PopStackEvent>();
	private List<StackEvent.PushStackEvent> pushedEvents = new ArrayList<StackEvent.PushStackEvent>();
	private List<StackEvent.ProgressStepStackEvent> lastProgressSteps = new ArrayList<StackEvent.ProgressStepStackEvent>();
	
	// ------------------------------------------------------------------------
	
	public CompoundPopPushStackEventBuilder() {
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * @return null if empty (no event added), otherwise compound event
	 */
	public StackEvent.CompoundPopPushStackEvent clearAndBuildOrNull() {
		synchronized(lock) {
			if (doIsEmpty()) {
				return null;
			}
			StackEvent.CompoundPopPushStackEvent res = build();
			doClear();
			return res;
		}
	}
	
	public StackEvent.CompoundPopPushStackEvent build() {
		synchronized(lock) {
			return doBuild();
		}
	}

	private void doClear() {
		levelDiff = 0;
		skipPushPopEventsCount = 0;
		popEvents.clear();
		pushedEvents.clear();
		lastProgressSteps.clear();		
	}

	private boolean doIsEmpty() {
		return levelDiff == 0
			&& skipPushPopEventsCount == 0
			&& popEvents.isEmpty()
			&& pushedEvents.isEmpty()
			&& lastProgressSteps.isEmpty();		
	}

	private StackEvent.CompoundPopPushStackEvent doBuild() {
		StackEvent.PopStackEvent[] popEventsArray = popEvents.toArray(new StackEvent.PopStackEvent[popEvents.size()]);
		StackEvent.PushStackEvent[] pushedEventsArray = pushedEvents.toArray(new StackEvent.PushStackEvent[pushedEvents.size()]);
		StackEvent.ProgressStepStackEvent[] lastProgressStepsArray = lastProgressSteps.toArray(new StackEvent.ProgressStepStackEvent[lastProgressSteps.size()]);
		return new CompoundPopPushStackEvent(skipPushPopEventsCount, 
				popEventsArray, pushedEventsArray, lastProgressStepsArray);
	}
	
	@Override
	public void acceptPushStackEvent(StackEvent.PushStackEvent pushStackEvent) {
		pushedEvents.add(pushStackEvent);
		lastProgressSteps.add(null);
		levelDiff++;
	}
	
	@Override
	public void acceptPopStackEvent(StackEvent.PopStackEvent popStackEvent) {
		if (levelDiff > 0) {
			// pop previously pushed elt ... suppress both
			int lastIndex = pushedEvents.size() - 1;
			pushedEvents.remove(lastIndex);
			lastProgressSteps.remove(lastIndex);
			skipPushPopEventsCount += 2;
		} else {
			popEvents.add(popStackEvent);
		}
		levelDiff--;
	}
	
	@Override
	public void acceptProgressStackEvent(StackEvent.ProgressStepStackEvent progressStackEvent) {
		int lastIndex = pushedEvents.size() - 1;
		lastProgressSteps.set(lastIndex, progressStackEvent);
	}
	
	@Override
	public void acceptCompoundStackEvent(StackEvent.CompoundPopPushStackEvent compoundStackEvent) {
		// merge compound with compound (should not occurs!..)
		
	}
	
}