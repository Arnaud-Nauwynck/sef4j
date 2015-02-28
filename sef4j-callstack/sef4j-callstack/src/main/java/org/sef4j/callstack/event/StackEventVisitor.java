package org.sef4j.callstack.event;

import org.sef4j.callstack.event.StackEvent.CompoundPopPushStackEvent;
import org.sef4j.callstack.event.StackEvent.PopStackEvent;
import org.sef4j.callstack.event.StackEvent.ProgressStepStackEvent;
import org.sef4j.callstack.event.StackEvent.PushStackEvent;

/**
 * Visitor design-pattern for StackEvent AST class hierarchy
 *
 */
public abstract class StackEventVisitor {

	public abstract void acceptPushStackEvent(PushStackEvent pushStackEvent);

	public abstract void acceptPopStackEvent(PopStackEvent popStackEvent);

	public abstract void acceptProgressStackEvent(ProgressStepStackEvent progressStackEvent);

	public abstract void acceptCompoundStackEvent(CompoundPopPushStackEvent compoundStackEvent);

}
