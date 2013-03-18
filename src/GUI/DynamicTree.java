package GUI;

import Core.DialogueNode;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
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

		this.tree.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				final TreePath path = tree.getPathForLocation(e.getX(), e.getY());
				if (path != null) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							tree.startEditingAtPath(path);
						}
					});
				}
			}
		});
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.treeModel = new DefaultTreeModel(null);
		this.tree = new JTree(this.treeModel);
		this.tree.setEditable(true);
		this.tree.setCellEditor(new DialogueNodeEditor(this.tree, (DefaultTreeCellRenderer) this.tree.getCellRenderer()));
		this.tree.setCellRenderer(new DialogueNodeRenderer());
		this.tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.tree.setShowsRootHandles(true);
		this.tree.setRootVisible(true);

		this.add(new JScrollPane(tree), BorderLayout.CENTER);
	}

	public void clear() {
		if (this.root != null) {
			this.root = null;
		}

		if (this.treeModel != null) {
			this.treeModel.setRoot(null);
			this.treeModel.reload();
		}
	}

	public void setRoot(DialogueNode root) {
		clear();
		this.root = root;
		this.treeModel = new DefaultTreeModel(this.root);
		this.tree.setModel(this.treeModel);
		this.recurseAddChild(null, root);
		this.treeModel.reload();
	}

	public void recurseAddChild(DialogueNode parent, DialogueNode child) {
		if (parent != null) {
			this.addChild(parent, child);
		}
		if (child.childrenNodes.size() > 0) {
			for (DialogueNode childChild : child.childrenNodes) {
				this.recurseAddChild(child, childChild);
			}
		}
	}

	public void addChild(DialogueNode parent, DialogueNode child) {
		this.treeModel.insertNodeInto(child, parent, parent.childrenNodes.indexOf(child));
		this.tree.scrollPathToVisible(new TreePath(child.getPath()));
	}

	public void setRespondEnabled(boolean enabled) {
		DialogueNodeEditor editor = (DialogueNodeEditor) this.tree.getCellEditor();
		DialogueNodeRenderer renderer = (DialogueNodeRenderer) this.tree.getCellRenderer();

		editor.setRespondEnabled(enabled);
		renderer.setRespondEnabled(enabled);
	}
}
