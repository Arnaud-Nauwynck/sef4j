package org.sef4j.callstack.stattree.formatters;

import java.io.PrintStream;

import org.sef4j.callstack.pattern.CallStackMatcher;
import org.sef4j.callstack.pattern.CallStackPattern;

/**
 * abstract helper class for CallTreeNode output formatter
 *
 */
public abstract class CallTreeOutputFormatter {

	private PrintStream output;
	
	private boolean useIndent;
	private int maxDepth = -1;
	private CallStackPattern callStackPattern;

		
	private int currIndentLevel;
	private CallStackMatcher currMatcher;

	// ------------------------------------------------------------------------
	
	public CallTreeOutputFormatter(PrintStream output, boolean useIndent) {
		this.output = output;
		this.useIndent = useIndent;
	}
	
	// ------------------------------------------------------------------------

	
}
