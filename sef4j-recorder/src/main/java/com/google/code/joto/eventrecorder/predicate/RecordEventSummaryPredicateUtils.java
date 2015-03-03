package com.google.code.joto.eventrecorder.predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.functors.FalsePredicate;
import org.apache.commons.collections.functors.NotPredicate;
import org.apache.commons.collections.functors.OrPredicate;
import org.apache.commons.collections.functors.TruePredicate;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.util.PatternsPredicate;
import com.thoughtworks.xstream.XStream;

/**
 * Utility Predicate classes for RecordEventSummary 
 */
public class RecordEventSummaryPredicateUtils {

	public static XStream createDefaultPredicateXStream() {
		XStream res = new XStream();
		registerDefaultXStreamAlias(res);
		return res;
	}
	
	public static void registerDefaultXStreamAlias(XStream res) {
		res.alias("And", AndPredicate.class);
		res.alias("Or", OrPredicate.class);
		res.alias("Not", NotPredicate.class);

		res.alias("Equal", EqualPredicate.class);
		res.alias("True", TruePredicate.class);
		res.alias("False", FalsePredicate.class);

		res.alias("DefaultEventPredicate", DefaultEventTypeRecordEventSummaryPredicate.class);
		
		res.alias("ClassMethodEquals", ClassMethodPatternRecordEventSummaryPredicate.class);
		res.alias("TypeSubTypeEquals", TypeSubTypePatternRecordEventSummaryPredicate.class);
		
	}
	
	public static Predicate snewDefaultClassMethodPredicate(String className, String methodName) {
		List<String> classNameIncludes = null;
		if (className != null) {
			classNameIncludes = new ArrayList<String>();
			classNameIncludes.add(className);
		}
		List<String> methodNameIncludes = null;
		if (methodName != null) {
			methodNameIncludes = new ArrayList<String>();
			methodNameIncludes.add(methodName);
		}
		
		ClassMethodPatternRecordEventSummaryPredicate res = 
				new ClassMethodPatternRecordEventSummaryPredicate(classNameIncludes,
						null, methodNameIncludes, null);
		return res;
	}

	// ------------------------------------------------------------------------
	
	public static abstract class AbstractRecordEventSummaryPredicate implements RecordEventSummaryPredicate, Serializable {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		@Override
		public boolean evaluate(Object obj) {
			if (obj == null || !(obj instanceof RecordEventSummary)) return false;
			else return evaluate((RecordEventSummary) obj);
		}
		
	}

	/**
	 * Predicate with basic conditions on <code>eventType</code> and <code>eventSubType</code>   
	 */
	public static class TypeSubTypePatternRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final String eventTypeValue;
		private Collection<String> eventSubTypeIncludes;
		private Collection<String> eventSubTypeExcludes;

		/** transient, computed from eventSubTypeIncludes,eventSubTypeExcludes */
		transient private PatternsPredicate _cachedEventSubTypePatterns;
		
		public TypeSubTypePatternRecordEventSummaryPredicate(
				String eventTypeValue, 
				Collection<String> eventSubTypeIncludes, Collection<String> eventSubTypeExcludes) {
			super();
			this.eventTypeValue = eventTypeValue;
			this.eventSubTypeIncludes = eventSubTypeIncludes;
			this.eventSubTypeExcludes = eventSubTypeExcludes;
			this._cachedEventSubTypePatterns = PatternsPredicate.snewCompilePatterns(eventSubTypeIncludes, eventSubTypeExcludes);
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
			if (eventTypeValue != null 
					&& !eventTypeValue.equals(evt.getEventType())) {
				return false;
			}
			if (_cachedEventSubTypePatterns == null) {
				this._cachedEventSubTypePatterns = PatternsPredicate.snewCompilePatterns(eventSubTypeIncludes, eventSubTypeExcludes);
			}
			if (_cachedEventSubTypePatterns != null 
					&& !_cachedEventSubTypePatterns.evaluate(evt.getEventSubType())) {
				return false;
			}
			return true;
		}
		
	}
	
	

	/**
	 * Predicate with basic conditions on <code>ClassName</code>, <code>MethodName</code>
	 */
	public static class ClassMethodPatternRecordEventSummaryPredicate extends AbstractRecordEventSummaryPredicate {

		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private final List<String> classNameIncludes;
		private final List<String> classNameExcludes;

		private final List<String> methodNameIncludes;
		private final List<String> methodNameExcludes;		
		
		/** transient, computed from classNameIncludes,classNameExcludes */
		transient private PatternsPredicate _cachedClassNamePatterns;
		/** transient, computed from methodNameIncludes,methodNameExcludes */
		transient private PatternsPredicate _cachedMethodNamePatterns;
		
		public ClassMethodPatternRecordEventSummaryPredicate(
				List<String> classNameIncludes,
				List<String> classNameExcludes,
				List<String> methodNameIncludes,
				List<String> methodNameExcludes) {
			super();
			this.classNameIncludes = classNameIncludes;
			this.classNameExcludes = classNameExcludes;
			this._cachedClassNamePatterns = PatternsPredicate.snewCompilePatterns(classNameIncludes, classNameExcludes);
			this.methodNameIncludes = methodNameIncludes;
			this.methodNameExcludes = methodNameExcludes;
			this._cachedMethodNamePatterns = PatternsPredicate.snewCompilePatterns(methodNameIncludes, methodNameExcludes);
		}

		@Override
		public boolean evaluate(RecordEventSummary evt) {
			if (_cachedClassNamePatterns == null) {
				this._cachedClassNamePatterns = PatternsPredicate.snewCompilePatterns(classNameIncludes, classNameExcludes);
			}
			if (_cachedClassNamePatterns != null 
					&& !_cachedClassNamePatterns.evaluate(evt.getEventClassName())) {
				return false;
			}
			if (_cachedMethodNamePatterns == null) {
				this._cachedMethodNamePatterns = PatternsPredicate.snewCompilePatterns(methodNameIncludes, methodNameExcludes);
			}
			if (_cachedMethodNamePatterns != null 
					&& !_cachedMethodNamePatterns.evaluate(evt.getEventMethodName())) {
				return false;
			}
			return true;
		}
		
	}

}
