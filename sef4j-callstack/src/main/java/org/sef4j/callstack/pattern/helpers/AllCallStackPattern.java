package org.sef4j.callstack.pattern.helpers;

import org.sef4j.callstack.CallStackElt;
import org.sef4j.callstack.pattern.CallStackMatcher;
import org.sef4j.callstack.pattern.CallStackPattern;

/**
 * CallStackPattern for always matching rule
 * 
 * this class is final & stateless & multi-thread safe(!)  => singleton
 */
public final class AllCallStackPattern extends CallStackPattern {

	public static final AllCallStackPattern INSTANCE = new AllCallStackPattern();
	public static final AllCallStackPattern instance() { return INSTANCE; }

	// ------------------------------------------------------------------------
	
	private AllCallStackPattern() {
	}

	// ------------------------------------------------------------------------

	@Override
	public CallStackMatcher matcher() {
		return AllCallStackMatcher.INSTANCE;
	}

	@Override
	public String toString() {
		return "AllCallStackPattern";
	}

	// ------------------------------------------------------------------------
	
	private static final class AllCallStackMatcher extends CallStackMatcher {
		private static AllCallStackMatcher INSTANCE = new AllCallStackMatcher();

		@Override
		public boolean isMatchPrefix() {
			return true;
		}

		@Override
		public boolean matches() {
			return true;
		}

		@Override
		public void onPush(CallStackElt stackElt) {
			// do nothing!
		}

		@Override
		public void onPop(CallStackElt stackElt) {
			// do nothing!
		}
		
	}
	
}
