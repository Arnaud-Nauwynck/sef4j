package com.google.code.joto.ui.tree;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.AbstractAggrEventTreeNode;

/**
 * interface for supporting aggregation + templatization of RecordEvent into AggrRecordEventTreeModel tree nodes 
 */
public interface AggrRecordEventTemplatizer {

	public boolean canHandle(AggrRecordEventTreeModel target,
			RecordEventSummary event);
	
	public AbstractAggrEventTreeNode aggregateTemplatizedEvent(
			AggrRecordEventTreeModel target,
			RecordEventSummary event);
	
}
