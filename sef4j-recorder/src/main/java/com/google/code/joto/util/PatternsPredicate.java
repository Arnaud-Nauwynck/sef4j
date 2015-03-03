package com.google.code.joto.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.Predicate;

/**
 * simple Pattern-based predicate for text includes + excludes  
 */
public class PatternsPredicate implements Predicate/*<String>*/, Serializable {
	
	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	private final List<Pattern> includePatterns;
	private final List<Pattern> excludePatterns;
	
	// ------------------------------------------------------------------------
	
	public PatternsPredicate(List<Pattern> includePatterns, List<Pattern> excludePatterns) {
		this.includePatterns = includePatterns;
		this.excludePatterns = excludePatterns;
	}

	public static PatternsPredicate snewCompilePatterns(Collection<String> includeRegexps, Collection<String> excludeRegexps) {
		return new PatternsPredicate(compilePatterns(includeRegexps), compilePatterns(excludeRegexps));
	}

	// ------------------------------------------------------------------------
	
	@Override
	public boolean evaluate(Object object) {
		if (!(object instanceof String)) return false;
		String str = (String) object;
		boolean res = evaluate(str, includePatterns, excludePatterns);
		return res;
	}

	public boolean evaluate(String str) {
		if (str == null) return false;
		boolean res = evaluate(str, includePatterns, excludePatterns);
		return res;
	}

	// static utilities
	// ------------------------------------------------------------------------
	
	public static boolean evaluate(String text, List<Pattern> includePatterns, List<Pattern> excludePatterns) {
		boolean res = true;
		if (includePatterns != null) {
			res = matchesAny(text, includePatterns);
		}
		if (res) {
			if (excludePatterns != null) {
				res = ! matchesAny(text, excludePatterns);
			}
		}
		return res;
	}
	
	public static boolean matchesAny(String text, List<Pattern> patterns) {
		boolean res = false;
		if (patterns == null || patterns.isEmpty()) return false;
		for(Pattern p : patterns) {
			if (p.matcher(text).matches()) {
				res = true;
				break;
			}
		}
		return res;
	}

	public static List<Pattern> compilePatterns(Collection<String> regexs) {
		if (regexs == null) return null;
		List<Pattern> res = new ArrayList<Pattern>(regexs.size());
		for(String regex : regexs) {
			res.add(Pattern.compile(regex));
		}
		return res;
	}
	
}