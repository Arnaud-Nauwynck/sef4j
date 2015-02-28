package org.sef4j.callstack.event;

import java.util.Collection;

/**
 * listener for StackEvent
 * 
 * (abstract class instead of interface, for performance and helper method)
 */
public abstract class StackEventListener {

	public abstract void onEvent(StackEvent event);
	
	public void onEvents(Collection<StackEvent> events) {
		for(StackEvent e : events) {
			onEvent(e);
		}
	}
	
}
