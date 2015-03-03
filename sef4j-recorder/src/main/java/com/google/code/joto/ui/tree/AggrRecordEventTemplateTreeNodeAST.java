package com.google.code.joto.ui.tree;

import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.iterators.IteratorEnumeration;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.predicate.RecordEventSummaryPredicateUtils;
import com.google.code.joto.eventrecorder.spy.calls.MethodCallEventUtils;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.filter.RecordEventFilterFile;
import com.google.code.joto.util.CompoundEnumeration;
import com.google.code.joto.util.SortedListTreeMap;
import com.google.code.joto.util.ui.JMenuItemUtils;

/**
 *
 */
public class AggrRecordEventTemplateTreeNodeAST {

	/**
	 *
	 */
	public static abstract class AbstractAggrEventTreeNode implements TreeNode, Serializable {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;
		
		private AbstractAggrEventTreeNode parent;
		private String name;
		
		protected AbstractAggrEventTreeNode(AbstractAggrEventTreeNode parent, String name) {
			this.parent = parent;
			this.name = name;
		}

		public AbstractAggrEventTreeNode getParent() {
			return parent;
		}

		public String getName() {
			return name;
		}
		
		@Override
		public boolean getAllowsChildren() {
			return true;
		}

		@Override
		public boolean isLeaf() {
			return false;
		}

		/** used by CellRenderer */
		public String getDisplayLabel() {
			return getName();
		}

		@Override
		public String toString() {
			return name;
		}
		
		protected RootPackageAggrEventTreeNode getRootTreeNode() {
			RootPackageAggrEventTreeNode res = null;
			for(AbstractAggrEventTreeNode p = this; p != null; p = p.parent) {
				if (p instanceof RootPackageAggrEventTreeNode) {
					res = (RootPackageAggrEventTreeNode) p;
					break;
				}
			}
			return res;
		}

		public int getChildDepth(AbstractAggrEventTreeNode node) {
			int res = 0;
			for(AbstractAggrEventTreeNode p = node; p != null; p = p.parent) {
				if (p instanceof RootPackageAggrEventTreeNode) {
					break;
				}
				res++;
			}
			return res;
		}

		protected DefaultTreeModel getRootTreeModel() {
			RootPackageAggrEventTreeNode rootNode = getRootTreeNode();
			return (rootNode != null)? rootNode.treeModel : null; 
		}
		
		public JotoContext getContext() {
			RootPackageAggrEventTreeNode rootNode = getRootTreeNode();
			return (rootNode != null)? rootNode.getContext() : null; 
		}
		
		protected void fireNodeAdded(AbstractAggrEventTreeNode node, int childIndex) {
			DefaultTreeModel treeModel = getRootTreeModel();
			if (treeModel == null) return;
			int[] childIndices = new int[] { childIndex };
			treeModel.nodesWereInserted(node.getParent(), childIndices);
		}

		protected void fireNodeRemoved(AbstractAggrEventTreeNode node, int childIndex) {
			DefaultTreeModel treeModel = getRootTreeModel();
			if (treeModel == null) return;
			int[] childIndices = new int[] { childIndex };
			treeModel.nodesWereRemoved(node.getParent(), childIndices, new TreeNode[] { node });
		}

		protected void fireNodeChanged(AbstractAggrEventTreeNode node, int childIndex) {
			DefaultTreeModel treeModel = getRootTreeModel();
			if (treeModel == null) return;
			int[] childIndices = new int[] { childIndex };
			treeModel.nodesChanged(node, childIndices);
		}

//		public void fillCtxMenu(JPopupMenu ctxMenu) {
//			// do nothing, cf overriden methods
//		}

		public abstract void fillCtxMenu(JPopupMenu ctxMenu);
		
	}
	
	/**
	 *
	 */
	public static class PackageAggrEventTreeNode extends AbstractAggrEventTreeNode {
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private String fullPackageName;

		private SortedListTreeMap<String,PackageAggrEventTreeNode> childPackageTreeNode = new SortedListTreeMap<String,PackageAggrEventTreeNode>();
		private SortedListTreeMap<String,ClassAggrEventTreeNode> childClassTreeNode = new SortedListTreeMap<String,ClassAggrEventTreeNode>();

		// ------------------------------------------------------------------------
		
		public PackageAggrEventTreeNode(PackageAggrEventTreeNode parent, String packageName) {
			super(parent, packageName);
			this.fullPackageName = (parent != null && !(parent instanceof RootPackageAggrEventTreeNode))? 
					parent.getFullPackageName() + "." + packageName : packageName;			
		}

		// ------------------------------------------------------------------------
		
		public String getFullPackageName() {
			return fullPackageName;
		}

		public String getChildPackageName() {
			return super.getName();
		}

		public PackageAggrEventTreeNode getOrCreateChildPackage(String childPackageName) {
			if (childPackageName.contains(".")) {
				throw new IllegalArgumentException("bad sub package name '" + childPackageName + "', should not contains '.'");
			}
			PackageAggrEventTreeNode res = childPackageTreeNode.get(childPackageName);
			if (res == null) {
				res = new PackageAggrEventTreeNode(this, childPackageName); 
				childPackageTreeNode.put(childPackageName, res);
				int childIndex = childPackageTreeNode.indexOf(res);
				fireNodeAdded(res, childIndex);
			}
			return res;
		}

		public PackageAggrEventTreeNode getOrCreateRecursiveChildPackage(String fullPackageName) {
			PackageAggrEventTreeNode res;
			int indexDot = fullPackageName.indexOf('.');
			if (indexDot != -1) {
				String immediatePackageName = fullPackageName.substring(0, indexDot);
				String remainingPackages = fullPackageName.substring(indexDot + 1);
				PackageAggrEventTreeNode immediatePackage = getOrCreateChildPackage(immediatePackageName);
				res = immediatePackage.getOrCreateRecursiveChildPackage(remainingPackages); // recursive call
			} else {
				res = getOrCreateChildPackage(fullPackageName);
			}
			return res;
		}
		
		public ClassAggrEventTreeNode getOrCreateChildClass(String simpleClassName) {
			ClassAggrEventTreeNode res = childClassTreeNode.get(simpleClassName);
			if (res == null) {
				res = new ClassAggrEventTreeNode(this, simpleClassName); 
				childClassTreeNode.put(simpleClassName, res);
				int childIndex = childClassTreeNode.indexOf(res);
				fireNodeAdded(res, childIndex);
			}
			return res;
		}

		public ClassAggrEventTreeNode getOrCreateRecursiveChildClass(String fullClassName) {
			int indexLastDot = fullClassName.lastIndexOf('.');
			if (indexLastDot == -1) {
				return getOrCreateChildClass(fullClassName);
			}
			String simpleClassName = fullClassName.substring(indexLastDot + 1);
			String packageFullName = fullClassName.substring(0, indexLastDot);
			PackageAggrEventTreeNode pck = getOrCreateRecursiveChildPackage(packageFullName);
			ClassAggrEventTreeNode res = pck.getOrCreateChildClass(simpleClassName);
			return res;
		}
		
		// implements TreeNode
		// ------------------------------------------------------------------------
		
		@Override
		public TreeNode getChildAt(int childIndex) {
			TreeNode res;
			if (childIndex < childPackageTreeNode.size()) {
				res = childPackageTreeNode.getAt(childIndex);
			} else {
				int classIndex = childIndex - childPackageTreeNode.size();
				if (classIndex >= 0 && classIndex < childClassTreeNode.size()) {
					res = childClassTreeNode.getAt(classIndex);
				} else {
					res = null; // should not occur?!
				}
			}
			return res;
		}

		@Override
		public int getChildCount() {
			return childPackageTreeNode.size() + childClassTreeNode.size();
		}

		@Override
		public int getIndex(TreeNode node) {
			int res = -1;
			if (node instanceof PackageAggrEventTreeNode) {
				res = childPackageTreeNode.indexOf(node);
			} else if (node instanceof ClassAggrEventTreeNode) {
				res = childClassTreeNode.indexOf(node);
			}
			return res;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Enumeration<AbstractAggrEventTreeNode> children() {
			return new CompoundEnumeration(
					childPackageTreeNode.enumeration(), 
					childClassTreeNode.enumeration());
		}
		
		public void fillCtxMenu(JPopupMenu ctxMenu) {
			ctxMenu.add(JMenuItemUtils.snew("create exclude quick filter ~Package", this, "onCtxMenuItemCreateQuickFilter"));
		}

		public void onCtxMenuItemCreateQuickFilter(ActionEvent event) {
			JotoContext context = getContext();
			String fullPackageName = getFullPackageName();
			
			RecordEventFilterFile filter = new RecordEventFilterFile();
			filter.setDescription("package~ " + fullPackageName);
			filter.setActive(true);
			
			// TODO filter is not created as persistent yet... 
			// filter.setPersistentFile(persistentFile);

			Predicate eventIncludePredicate = 
					RecordEventSummaryPredicateUtils.snewDefaultClassMethodPredicate(fullPackageName + ".*", null);
			Predicate eventPredicate = PredicateUtils.notPredicate(eventIncludePredicate);
			
			filter.setEventPredicate(eventPredicate);
			filter.setEventTypePredicateDescription(MethodCallEventUtils.METHODCALL_EVENT_TYPE);
			filter.setEventClassNamePredicateDescription(fullPackageName + ".**");

			context.addMethodCallFilter(filter);
		}
		
		
		public String toString() {
			return "PackageNode[" + fullPackageName + "]";
		}
	}
	
	/**
	 *
	 */
	public static class RootPackageAggrEventTreeNode extends PackageAggrEventTreeNode {
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private JotoContext context;
		private DefaultTreeModel treeModel;
		
		/** param ... DefaultTreeModel treeModel can not be set here .... chicken-egg pb => set init() next !*/
		public RootPackageAggrEventTreeNode(JotoContext context) {
			super(null, "");
			this.context = context;
		}

		public JotoContext getContext() {
			return context;
		}
		
		public void setInit(DefaultTreeModel treeModel) {
			this.treeModel = treeModel;
		}
		
		public String toString() {
			return "(root package)";
		}
	}
	
	/**
	 * TreeNode corresponding to a java.lang.Class
	 */
	public static class ClassAggrEventTreeNode extends AbstractAggrEventTreeNode {
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		// private String className; ... implicit from parent fullPackageName + simpleClassName

		private SortedListTreeMap<String,MethodAggrEventTreeNode> childMethods = new SortedListTreeMap<String,MethodAggrEventTreeNode>();
		
		// ------------------------------------------------------------------------
		
		public ClassAggrEventTreeNode(PackageAggrEventTreeNode parentPackage, String simpleClassName) {
			super(parentPackage, simpleClassName);
		}

		// ------------------------------------------------------------------------
		
		public String getSimpleClassName() {
			return super.getName();
		}

		public PackageAggrEventTreeNode getParentPackageNode() {
			return (PackageAggrEventTreeNode) super.getParent();
		}
		
		public String getFullClassName() {
			return getParentPackageNode().getFullPackageName() + "." + getSimpleClassName();
		}

		public MethodAggrEventTreeNode getOrCreateMethod(String methodName) {
			MethodAggrEventTreeNode res = childMethods.get(methodName);
			if (res == null) {
				res = new MethodAggrEventTreeNode(this, methodName);
				childMethods.put(methodName, res);
				int childIndex = childMethods.indexOf(res);
				fireNodeAdded(res, childIndex);
			}
			return res;
		}
		
		// implements TreeNode
		// ------------------------------------------------------------------------
		
		@Override
		public TreeNode getChildAt(int childIndex) {
			return childMethods.getAt(childIndex);
		}

		@Override
		public int getChildCount() {
			return childMethods.size();
		}

		@Override
		public int getIndex(TreeNode node) {
			return childMethods.indexOf(node);
		}

		@Override
		public Enumeration<MethodAggrEventTreeNode> children() {
			return childMethods.enumeration();
		}
		
		public String toString() {
			return "ClassNode[" + getSimpleClassName() + "]";
		}
		
		public void fillCtxMenu(JPopupMenu ctxMenu) {
			ctxMenu.add(JMenuItemUtils.snew("create exclude quick filter ~Class", this, "onCtxMenuItemCreateQuickFilter"));
		}

		public void onCtxMenuItemCreateQuickFilter(ActionEvent event) {
			JotoContext context = getContext();
			ClassAggrEventTreeNode classNode = this;
			String className = classNode.getFullClassName();
			String simpleClassName = classNode.getSimpleClassName();
			
			RecordEventFilterFile filter = new RecordEventFilterFile();
			filter.setDescription("class ~ " + simpleClassName);
			filter.setActive(true);
			
			// TODO filter is not created as persistent yet... 
			// filter.setPersistentFile(persistentFile);

			Predicate eventIncludePredicate = 
					RecordEventSummaryPredicateUtils.snewDefaultClassMethodPredicate(className, null);
			Predicate eventPredicate = PredicateUtils.notPredicate(eventIncludePredicate);
			
			filter.setEventPredicate(eventPredicate);
			filter.setEventTypePredicateDescription(MethodCallEventUtils.METHODCALL_EVENT_TYPE);
			filter.setEventClassNamePredicateDescription(className);

			context.addMethodCallFilter(filter);
		}
		
	}
	
	/**
	 * TreeNode corresponding to a class java.lang.reflect.Method
	 */
	public static class MethodAggrEventTreeNode extends AbstractAggrEventTreeNode {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		// implicit from super.getName() ... private String methodSignature;
		
		private SortedListTreeMap<String,TemplateMethodCallAggrEventTreeNode> childTemplateCalls = new SortedListTreeMap<String,TemplateMethodCallAggrEventTreeNode>();
		
		private static final String OTHERS_TEMPLATES_KEY = "others";
		private int maxTemplateCount = 10;
		
		// ------------------------------------------------------------------------
		
		public MethodAggrEventTreeNode(ClassAggrEventTreeNode parent, String methodName) {
			super(parent, methodName);
		}

		// ------------------------------------------------------------------------
		
		public String getMethodName() {
			return super.getName();
		}

		public ClassAggrEventTreeNode getParentClassNode() {
			return (ClassAggrEventTreeNode) super.getParent();
		}

		public TemplateMethodCallAggrEventTreeNode getOrCreateTemplateCall(String templateCallKey) {
			boolean isOtherTemplates = false;
			if (childTemplateCalls.size() >= maxTemplateCount) {
				templateCallKey = OTHERS_TEMPLATES_KEY;
				isOtherTemplates = true;
			} 
			TemplateMethodCallAggrEventTreeNode res = childTemplateCalls.get(templateCallKey);
			if (res == null) {
				res = new TemplateMethodCallAggrEventTreeNode(this, templateCallKey);
				if (isOtherTemplates) {
					res.maxRequestResponsesCount = res.maxRequestResponsesCount * 3;
				}
				childTemplateCalls.put(templateCallKey, res);
				int childIndex = childTemplateCalls.indexOf(res);
				fireNodeAdded(res, childIndex);
			}
			return res;
		}
		
		// implements TreeNode
		// ------------------------------------------------------------------------
		
		@Override
		public TreeNode getChildAt(int childIndex) {
			return childTemplateCalls.getAt(childIndex);
		}

		@Override
		public int getChildCount() {
			return childTemplateCalls.size();
		}

		@Override
		public int getIndex(TreeNode node) {
			return childTemplateCalls.indexOf(node);
		}

		@Override
		public Enumeration<TemplateMethodCallAggrEventTreeNode> children() {
			return childTemplateCalls.enumeration();
		}
	
		public String toString() {
			return "MethodNode[" + getMethodName() + "]";
		}
		
		public void fillCtxMenu(JPopupMenu ctxMenu) {
			ctxMenu.add(JMenuItemUtils.snew("create exclude quick filter ~Method", this, "onCtxMenuItemCreateQuickFilter"));
		}

		public void onCtxMenuItemCreateQuickFilter(ActionEvent event) {
			JotoContext context = getContext();
			ClassAggrEventTreeNode classNode = getParentClassNode();
			String className = classNode.getFullClassName();
			String simpleClassName = classNode.getSimpleClassName();
			String methodName = getMethodName();
			
			RecordEventFilterFile filter = new RecordEventFilterFile();
			filter.setDescription("method ~ " + simpleClassName + "." + methodName);
			filter.setActive(true);
			
			// TODO filter is not created as persistent yet... 
			// filter.setPersistentFile(persistentFile);

			Predicate eventIncludePredicate = 
					RecordEventSummaryPredicateUtils.snewDefaultClassMethodPredicate(className, methodName);
			Predicate eventPredicate = PredicateUtils.notPredicate(eventIncludePredicate);
			
			filter.setEventPredicate(eventPredicate);
			filter.setEventTypePredicateDescription(MethodCallEventUtils.METHODCALL_EVENT_TYPE);
			filter.setEventClassNamePredicateDescription(className);
			filter.setEventMethodNamePredicateDescription(methodName);

			context.addMethodCallFilter(filter);
		}
	}
	
	/**
	 * TreeNode corresponding to a list of templatized method invocation with request-response events
	 */
	public static class TemplateMethodCallAggrEventTreeNode extends AbstractAggrEventTreeNode {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		// implicit from super.getName() ... private String methodSignature;
		
		private int maxRequestResponsesCount = 5; 
		private List<MethodCallRequestResponseEventTreeNode> purgedFifoRequestResponses = new ArrayList<MethodCallRequestResponseEventTreeNode>(); 
		
		// ------------------------------------------------------------------------
		
		public TemplateMethodCallAggrEventTreeNode(MethodAggrEventTreeNode parent, String templateKey) {
			super(parent, templateKey);
		}

		// ------------------------------------------------------------------------
		
		public void addRequestEvent(RecordEventSummary event) {
			MethodCallRequestResponseEventTreeNode e = new MethodCallRequestResponseEventTreeNode(this, event);
			if (purgedFifoRequestResponses.size() >= maxRequestResponsesCount) {
				MethodCallRequestResponseEventTreeNode purged = (MethodCallRequestResponseEventTreeNode) purgedFifoRequestResponses.remove(0);
				fireNodeRemoved(purged, 0);
			}
			purgedFifoRequestResponses.add(e);
			int childIndex = purgedFifoRequestResponses.size() -1;
			fireNodeAdded(e, childIndex);
		}

		public MethodCallRequestResponseEventTreeNode addResponseEvent(RecordEventSummary event) {
			MethodCallRequestResponseEventTreeNode res = null;
			int reqEventId = event.getCorrelatedEventId();
			if (reqEventId == -1) {
				return null; // should not occur : not linked to a reqest? => ignore this response  
			}
			for (Iterator<MethodCallRequestResponseEventTreeNode> iter = purgedFifoRequestResponses.iterator(); iter.hasNext(); ) {
				MethodCallRequestResponseEventTreeNode reqResp = iter.next();
				if (reqResp.requestEvent.getEventId() == reqEventId) {
					reqResp.responseEvent = event;
					res = reqResp;
					break;
				}
			}
			return res;
		}

		// implements TreeNode
		// ------------------------------------------------------------------------

		@Override
		public TreeNode getChildAt(int childIndex) {
			return purgedFifoRequestResponses.get(childIndex);
		}

		@Override
		public int getChildCount() {
			return purgedFifoRequestResponses.size();
		}

		@Override
		public int getIndex(TreeNode node) {
			return purgedFifoRequestResponses.indexOf(node);
		}

		@Override
		public Enumeration<?> children() {
			return new IteratorEnumeration(purgedFifoRequestResponses.iterator());
		}
		
		public void fillCtxMenu(JPopupMenu ctxMenu) {
			// ctxMenu.add(JMenuItemUtils.snew("create exclude quick filter ~TemplateMethodCall", this, "onCtxMenuItemCreateQuickFilter"));
		}
		
		public String toString() {
			return "TemplateMethodCallNode[" + purgedFifoRequestResponses.size() + "]";
		}

	}
	
	
	/**
	 * TreeNode corresponding to a method invocation with request-response events 
	 */
	public static class MethodCallRequestResponseEventTreeNode extends AbstractAggrEventTreeNode {
		
		/** internal for java.io.Serializable */
		private static final long serialVersionUID = 1L;

		private RecordEventSummary requestEvent;
		private RecordEventSummary responseEvent;
		
		public MethodCallRequestResponseEventTreeNode(AbstractAggrEventTreeNode parent, RecordEventSummary requestEvent) {
			super(parent, "req#" + requestEvent.getEventId());
			this.requestEvent = requestEvent;
		}

		public RecordEventSummary getRequestEvent() {
			return requestEvent;
		}

		public RecordEventSummary getResponseEvent() {
			return responseEvent;
		}

		public void setResponseEvent(RecordEventSummary p) {
			this.responseEvent = p;
		}
		
		// implements TreeNode
		// ------------------------------------------------------------------------

		@Override
		public boolean getAllowsChildren() {
			return false;
		}

		@Override
		public boolean isLeaf() {
			return true;
		}

		@Override
		public TreeNode getChildAt(int childIndex) {
			return null;
		}

		@Override
		public int getChildCount() {
			return 0;
		}

		@Override
		public int getIndex(TreeNode node) {
			return -1;
		}

		@Override
		public Enumeration<?> children() {
			return null;
		}
		
		public void fillCtxMenu(JPopupMenu ctxMenu) {
			// ctxMenu.add(JMenuItemUtils.snew("create exclude quick filter ~MethodCall", this, "onCtxMenuItemCreateQuickFilter"));
		}
		
		public String toString() {
			return "MethodCallRequestResponseNode[" 
					// + requestEvent 
					+ "]";
		}
		
	}
	
}
