package com.google.code.joto.ui.tree;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.ui.tree.AggrRecordEventTemplateTreeNodeAST.AbstractAggrEventTreeNode;

/**
 * a Tree View for aggregating similar (="templatized") RecordEvent together
 */
public class AggrRecordEventTreeView {
	
	private static Logger log = LoggerFactory.getLogger(AggrRecordEventTreeView.class);
	
	private JPanel panel;
	
	private AggrRecordEventTreeModel treeModel;
	private JTree jtree;
	
	// ------------------------------------------------------------------------

	public AggrRecordEventTreeView(AggrRecordEventTreeModel treeModel) {
		this.treeModel = treeModel;
		initComponents();
	}

	private void initComponents() {
		this.panel = new JPanel(new BorderLayout());
		
		jtree = new JTree(treeModel);
		panel.add(jtree, BorderLayout.CENTER);
		
		TreeCellRenderer aggrTreeCellRenderer = new InnerAggrTreeCellRenderer();
		jtree.setCellRenderer(aggrTreeCellRenderer);
		
		// jtree.getSelectionModel().add()
		
		jtree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					onJTreeRightClick(e);
				}
			}
		});
	}
	
	// ------------------------------------------------------------------------
	
	public JComponent getJComponent() {
		return panel;
	}

	private void onJTreeRightClick(MouseEvent e) {
		TreePath[] selectedPaths = jtree.getSelectionPaths();
		if (selectedPaths == null || selectedPaths.length == 0) {
			TreePath clickedPath = jtree.getPathForLocation(e.getX(), e.getY());
			if (clickedPath != null) {
				selectedPaths = new TreePath[] { clickedPath };
			}
		}
		// convert JTree selection to AbstractAggrEventTreeNode 
		List<AbstractAggrEventTreeNode> nodes = new ArrayList<AbstractAggrEventTreeNode>();
		if (selectedPaths != null) {
			for(TreePath path : selectedPaths) {
				AbstractAggrEventTreeNode node = (AbstractAggrEventTreeNode) path.getLastPathComponent();
				nodes.add(node);
			}
		}
		log.debug("right clicked:" + nodes);
		JPopupMenu ctxMenu = new JPopupMenu();
		
		if (nodes.size() == 1) {
			AbstractAggrEventTreeNode node = nodes.get(0);
			node.fillCtxMenu(ctxMenu);			
		} else {
			// NOT IMPLEMENTED YET
			log.error("NOT IMPLEMENTED YET ... contextual menu with multi-selection on JTree...");
		}
		// fill context menu with selection
		ctxMenu.show(jtree, e.getX(), e.getY());
		
	}

	// ------------------------------------------------------------------------
	
	private class InnerAggrTreeCellRenderer implements TreeCellRenderer {

		JLabel label = new JLabel();
		
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, 
				boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			AbstractAggrEventTreeNode node = (AbstractAggrEventTreeNode) value;
			label.setText(node.getDisplayLabel());
			return label;
		}
		
	}
	
}
