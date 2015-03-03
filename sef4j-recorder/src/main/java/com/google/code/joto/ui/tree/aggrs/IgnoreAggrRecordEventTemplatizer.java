package com.google.code.joto.ui.tree.aggrs;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.AbstractAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplatizer;
import com.google.code.joto.ui.tree.AggrRecordEventTreeModel;

public class IgnoreAggrRecordEventTemplatizer implements AggrRecordEventTemplatizer {

	private static final IgnoreAggrRecordEventTemplatizer INSTANCE = new IgnoreAggrRecordEventTemplatizer();
	public static IgnoreAggrRecordEventTemplatizer getInstance() {
		return INSTANCE;
	}
	
	@Override
	public boolean canHandle(AggrRecordEventTreeModel target, RecordEventSummary event) {
		return true;
	}

	@Override
	public AbstractAggrEventTreeNode aggregateTemplatizedEvent(AggrRecordEventTreeModel target, RecordEventSummary event) {
		return null;
	}
	
}
