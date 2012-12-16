package GUI;

import Core.DialogueNode;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
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
				dNode.gPanel.setBorder (BorderFactory.createLineBorder (Color.black));
				comp = dNode.gPanel;
			}
		}
		
		if (comp == null) {
			comp = defaultRenderer.getTreeCellRendererComponent (tree, value, leaf, expanded, leaf, row, hasFocus);
		}

		return comp;
	}
}
