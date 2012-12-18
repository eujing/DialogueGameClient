package GUI;

import Core.DialogueNode;
import Core.GamePanel;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

public class DialogueNodeRenderer implements TreeCellRenderer {

	private DefaultTreeCellRenderer defaultRenderer;
	private boolean enabled;
	
	public DialogueNodeRenderer () {
		this.defaultRenderer = new DefaultTreeCellRenderer ();
		this.enabled = false;
	}
	
	public void setRespondEnabled (boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public Component getTreeCellRendererComponent (JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component comp = null;

		if (value != null && value instanceof DefaultMutableTreeNode) {
			if (value instanceof DialogueNode) {
				GamePanel gPanel = new GamePanel ((DialogueNode) value);
				gPanel.setRespondEnabled (enabled);
				comp = gPanel;
			}
		}
		
		if (comp == null) {
			comp = defaultRenderer.getTreeCellRendererComponent (tree, value, leaf, expanded, leaf, row, hasFocus);
		}

		return comp;
	}
}
