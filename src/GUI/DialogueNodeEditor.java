package GUI;

import Core.DialogueNode;
import Core.GamePanel;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DialogueNodeEditor extends DefaultTreeCellEditor {
	
	public DialogueNodeEditor (JTree tree, DefaultTreeCellRenderer renderer) {
		super (tree, renderer);
	}

	@Override
	public Component getTreeCellEditorComponent (JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		if (value instanceof DialogueNode) {
			GamePanel gPanel = new GamePanel ((DialogueNode) value);
			gPanel.setBorder (BorderFactory.createLineBorder (Color.black, 1, true));
			return gPanel;
		}
		
		return super.getTreeCellEditorComponent (tree, value, isSelected, expanded, leaf, row);
	}
}
