package org.sef4j.callstack.pattern.helpers;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.callstack.pattern.CallStackMatcher;
import org.sef4j.callstack.pattern.CallStackPattern;


public class AllCallStackPatternTest {

	@Test
	public void testMatcher() {
		// Prepare
		// Perform
		CallStackPattern sut = AllCallStackPattern.instance();
		CallStackMatcher matcher = sut.matcher();
		// Post-check
		Assert.assertTrue(matcher.isMatchPrefix());
		Assert.assertTrue(matcher.matches());
	}

}
