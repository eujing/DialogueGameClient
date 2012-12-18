package GUI;

import Core.DialogueNode;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class DynamicTree extends JPanel {

	private DialogueNode root;
	private DefaultTreeModel treeModel;
	private JTree tree;

	public DynamicTree() {
		initialize();

		this.tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				final TreePath path = e.getNewLeadSelectionPath();
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						tree.startEditingAtPath(path);
					}
				});
			}
		});
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.treeModel = new DefaultTreeModel (null);
		this.tree = new JTree(this.treeModel);
		this.tree.setEditable(true);
		this.tree.setCellEditor(new DialogueNodeEditor(this.tree, (DefaultTreeCellRenderer) this.tree.getCellRenderer()));
		this.tree.setCellRenderer(new DialogueNodeRenderer());
		this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.tree.setShowsRootHandles(true);
		this.tree.setRootVisible(true);
		this.tree.setRowHeight(0);

		this.add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	public void clear() {
		if (this.root != null) {
			//this.root.removeAllChildren();
			this.root = null;
		}

		if (this.treeModel != null) {
			this.treeModel.setRoot (null);
			this.treeModel.reload();
		}
	}

	public void setRoot(DialogueNode root) {
		clear();
		this.root = root;
		this.treeModel = new DefaultTreeModel(this.root);
		this.tree.setModel(this.treeModel);
		this.treeModel.reload();
	}

	public void addChild(DialogueNode parent, DialogueNode child) {
		this.treeModel.insertNodeInto(child, parent, parent.childrenNodes.indexOf(child));
		this.tree.scrollPathToVisible(new TreePath(child.getPath()));
	}

	public void setRespondEnabled (boolean enabled) {
		DialogueNodeEditor editor = (DialogueNodeEditor) this.tree.getCellEditor ();
		DialogueNodeRenderer renderer = (DialogueNodeRenderer) this.tree.getCellRenderer ();
		
		editor.setRespondEnabled (enabled);
		renderer.setRespondEnabled (enabled);
	}
}
