package GUI;

import Core.DialogueNode;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DialogueNodeEditor extends DefaultTreeCellEditor {

	private boolean enabled;

	public DialogueNodeEditor (JTree tree, DefaultTreeCellRenderer renderer) {
		super (tree, renderer);
		this.enabled = false;
	}

	public void setRespondEnabled (boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public Component getTreeCellEditorComponent (JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		if (value instanceof DialogueNode) {
			DialogueNode node = (DialogueNode) value;
			GamePanel gPanel = new GamePanel (node);
			gPanel.setBackground(Color.yellow);
			gPanel.setRespondEnabled (this.enabled);
			
			if (node.isMostRecent) {
				gPanel.setBackground (Color.pink);
			}
			
			return gPanel;
		}

		return super.getTreeCellEditorComponent (tree, value, isSelected, expanded, leaf, row);
	}
}
