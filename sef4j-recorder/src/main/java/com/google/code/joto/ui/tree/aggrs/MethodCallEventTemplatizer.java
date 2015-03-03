package com.google.code.joto.ui.tree.aggrs;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.spy.calls.MethodCallEventUtils;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.AbstractAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.ClassAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.MethodAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.RootPackageAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.TemplateMethodCallAggrEventTreeNode;
import com.google.code.joto.ui.tree.AggrRecordEventTemplatizer;
import com.google.code.joto.ui.tree.AggrRecordEventTreeModel;

/**
 *
 */
public class MethodCallEventTemplatizer implements AggrRecordEventTemplatizer {

	// ------------------------------------------------------------------------

	public MethodCallEventTemplatizer() {
	}

	// ------------------------------------------------------------------------

	
	@Override
	public boolean canHandle(AggrRecordEventTreeModel target, RecordEventSummary event) {
		return MethodCallEventUtils.METHODCALL_EVENT_TYPE.equals(event.getEventType());
	}

	@Override
	public AbstractAggrEventTreeNode aggregateTemplatizedEvent(AggrRecordEventTreeModel target, RecordEventSummary event) {
		String eventSubType = event.getEventSubType();
		String className = event.getEventClassName();
		String methodName = event.getEventMethodName();
		if (className == null) {
			className = "Unknown"; // should not occur
		}
		RootPackageAggrEventTreeNode rootPackageNode = target.getRootPackageNode();
		ClassAggrEventTreeNode classNode = rootPackageNode.getOrCreateRecursiveChildClass(className);	
		MethodAggrEventTreeNode methodNode = classNode.getOrCreateMethod(methodName);
		
		String templateCallKey = "dummy-params-templatekey"; // ... todo templatize params...
		
		TemplateMethodCallAggrEventTreeNode methodCallNode = methodNode.getOrCreateTemplateCall(templateCallKey);

		if (MethodCallEventUtils.REQUEST_EVENT_SUBTYPE.equals(eventSubType)) {
			methodCallNode.addRequestEvent(event);	
		} else if (MethodCallEventUtils.RESPONSE_EVENT_SUBTYPE.equals(eventSubType)) {
			methodCallNode.addResponseEvent(event);	
		} else {
			// unrecognized request/response event subType => ignore!
		}
		return methodCallNode;
	}
	
}
