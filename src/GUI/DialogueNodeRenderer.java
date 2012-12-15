package GUI;

import Core.DialogueNode;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class DialogueNodeRenderer implements TreeCellRenderer {

	private DefaultTreeCellRenderer defaultRenderer;
	
	public DialogueNodeRenderer () {
		this.defaultRenderer = new DefaultTreeCellRenderer ();
	}

	@Override
	public Component getTreeCellRendererComponent (JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component comp = null;

		if (value != null && value instanceof DefaultMutableTreeNode) {
			if (value instanceof DialogueNode) {
				DialogueNode dNode = (DialogueNode) value;
				comp = dNode.gPanel;
			}
		}
		
		if (comp == null) {
			comp = defaultRenderer.getTreeCellRendererComponent (tree, value, leaf, expanded, leaf, row, hasFocus);
			//System.out.println ("oh no");
		}

		return comp;
	}
}
