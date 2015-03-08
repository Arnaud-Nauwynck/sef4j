package org.sef4j.callstack.pattern;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.CallStackPushPopHandler;

/**
 * analog to java.util.regex.Matcher ... but for matching CallStack
 * 
 * This class is Stateful, and single-threaded ... 
 * it contains intermediate result for the currently submitted CallSTack using onPush() and onPop()
 */
public abstract class CallStackMatcher extends CallStackPushPopHandler {

	public abstract boolean isMatchPrefix();
	
	/** @return true is the current stack matches this pattern */
	public abstract boolean matches();
	
	
	
	
	public Object getGroup(String name) {
		// do nothing, cf override
		return null;
	}

	@Override
	public void onProgressStep(CallStackElt stackElt) {
		// do nothing
	}
	

}
