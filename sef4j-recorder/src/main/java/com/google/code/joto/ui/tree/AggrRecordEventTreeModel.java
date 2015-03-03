package com.google.code.joto.ui.tree;

import javax.swing.tree.DefaultTreeModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.DefaultRecordEventChangeVisitor;
import com.google.code.joto.eventrecorder.DefaultVisitorRecordEventListener;
import com.google.code.joto.eventrecorder.RecordEventChangeVisitor;
import com.google.code.joto.eventrecorder.RecordEventStoreChange.AddRecordEventStoreEvent;
import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.table.RecordEventSwingRedispatcher;
import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.RootPackageAggrEventTreeNode;

/**
 * a swing TreeModel adapter for aggregating RecordEventTree per template category
 *
 */
public class AggrRecordEventTreeModel extends DefaultTreeModel {

	/** internal for java.io.Serializable */
	private static final long serialVersionUID = 1L;

	
	private static Logger log = LoggerFactory.getLogger(AggrRecordEventTreeModel.class);
	
	private JotoContext context;
	
	// ... implicit from super: private DefaultRecordEventTemplateTreeNode rootNode;
	private RootPackageAggrEventTreeNode rootPackageNode;
	
	private RecordEventChangeVisitor innerRecordEventChangeListener = new InnerRecordEventChangeListener();

	/** dispatcher for choosing RecordEventTemplatizer per event types, and build the aggregated TreeNode */
	private AggrRecordEventTemplatizerDispatcher eventTemplatizerDispatcher = new AggrRecordEventTemplatizerDispatcher();
	
	// ------------------------------------------------------------------------
	
	public AggrRecordEventTreeModel(JotoContext context) {
		super(new RootPackageAggrEventTreeNode(context));
		this.rootPackageNode = (RootPackageAggrEventTreeNode) super.getRoot();
		rootPackageNode.setInit(this);
		this.context = context;

		// copy Templatizers settings from context
		eventTemplatizerDispatcher.addTemplatizers(context.getConfig().getEventTemplatizerDispatcher());

		// subscribe + replay history from first to current event
		context.getEventStore().getEventsAndAddEventListener(0, 
				new RecordEventSwingRedispatcher(new DefaultVisitorRecordEventListener(innerRecordEventChangeListener)));

	}

	// ------------------------------------------------------------------------

	public JotoContext getContext() {
		return context;
	}

	public RootPackageAggrEventTreeNode getRootPackageNode() {
		return rootPackageNode;
	}
	
	// ------------------------------------------------------------------------

	private class InnerRecordEventChangeListener extends DefaultRecordEventChangeVisitor {

		@Override
		public void caseAddEvent(AddRecordEventStoreEvent addEvent) {
			RecordEventSummary event = addEvent.getEventSummary();
			try {
				eventTemplatizerDispatcher.dispatchAggregateTemplatizedEvent(AggrRecordEventTreeModel.this, event);
			} catch(Exception ex) {
				log.error("Failed to templatize event " + event + "... ignore it, no rethrow!", ex);
			}
		}
	};
}
