package org.sef4j.callstack;

/**
 * Listener class to attach to a CallStackElt to listen to push()/pop() event,
 * and propagate sub-listener (maybe itself) onto pushed child CallStackElt
 */
public abstract class CallStackPushPopHandler {

	public abstract void onPush(CallStackElt stackElt);
	
	public abstract void onPop(CallStackElt stackElt);

	public abstract void onProgressStep(CallStackElt stackElt);

}
