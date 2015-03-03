package com.google.code.joto.ui.tree;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.spy.calls.MethodCallEventUtils;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.AbstractAggrEventTreeNode;
import com.google.code.joto.ui.tree.aggrs.IgnoreAggrRecordEventTemplatizer;
import com.google.code.joto.ui.tree.aggrs.MethodCallEventTemplatizer;
import com.google.code.joto.util.PriorityList;

/**
 * Dispatcher for AggrRecordEventTemplatizer
 */
public class AggrRecordEventTemplatizerDispatcher implements Serializable {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(AggrRecordEventTemplatizerDispatcher.class);
	
	/** pluggable dispatcher mecanism for choosing RecordEventTemplatizer per event types */
	private Map<String,PriorityList<AggrRecordEventTemplatizer>> eventTypeToTemplatizers = 
			new HashMap<String,PriorityList<AggrRecordEventTemplatizer>>();
	
	// ------------------------------------------------------------------------

	public AggrRecordEventTemplatizerDispatcher() {
		addDefaultTemplatizers();
	}

	// ------------------------------------------------------------------------
	
	public void addDefaultTemplatizers() {
		MethodCallEventTemplatizer defaultCallTemplatizer = new MethodCallEventTemplatizer();
		addEventTypeTemplatizer(MethodCallEventUtils.METHODCALL_EVENT_TYPE, defaultCallTemplatizer, 10);
	}
	
	public void addTemplatizers(AggrRecordEventTemplatizerDispatcher src) {
		Map<String, PriorityList<AggrRecordEventTemplatizer>> srcEventTypeToTemplatizers = src.eventTypeToTemplatizers;
		for(Map.Entry<String, PriorityList<AggrRecordEventTemplatizer>> e : srcEventTypeToTemplatizers.entrySet()) {
			PriorityList<AggrRecordEventTemplatizer> srcLs = e.getValue();
			PriorityList<AggrRecordEventTemplatizer> destLs = getAggrTemplatizersForEventType(e.getKey());
			destLs.addAllWithPriority(srcLs);
		}
	}


	public void addEventTypeTemplatizer(String eventType, AggrRecordEventTemplatizer templatizer, int priority) {
		PriorityList<AggrRecordEventTemplatizer> ls = getAggrTemplatizersForEventType(eventType);
		ls.add(templatizer, priority);
	}

	private PriorityList<AggrRecordEventTemplatizer> getAggrTemplatizersForEventType(String eventType) {
		PriorityList<AggrRecordEventTemplatizer> ls = eventTypeToTemplatizers.get(eventType);
		if (ls == null) {
			ls = new PriorityList<AggrRecordEventTemplatizer>();
			eventTypeToTemplatizers.put(eventType, ls);
		}
		return ls;
	}
	
	// ------------------------------------------------------------------------

	/**
	 * main entry point for dispatching RecordEvent to any of the AggrRecordEventTemplatizer
	 * @param target
	 * @param event
	 */
	public AbstractAggrEventTreeNode dispatchAggregateTemplatizedEvent(AggrRecordEventTreeModel target, RecordEventSummary event) {
		AbstractAggrEventTreeNode res;
		String eventType = event.getEventType();
		// find AggrRecordEventTemplatizer for event
		AggrRecordEventTemplatizer foundTemplatizer = null;
		PriorityList<AggrRecordEventTemplatizer> templatizers = eventTypeToTemplatizers.get(eventType);
		if (templatizers != null) {
			for(AggrRecordEventTemplatizer e : templatizers) {
				if (e.canHandle(target, event)) {
					foundTemplatizer = e;
					break;
				}
			}
		}
		// apply templatizer
		if (foundTemplatizer == null) {
			log.warn("no templatizer for aggregating event type " + eventType + " .. default NOT IMPLEMENTED YET => ignore event");
			
			// to log once... register the "do-nothing" templatizer...
			addEventTypeTemplatizer(eventType, IgnoreAggrRecordEventTemplatizer.getInstance(), 0);
			
			res = null;
		} else {
			res = foundTemplatizer.aggregateTemplatizedEvent(target, event);
		}
		return res;
	}
	
}
