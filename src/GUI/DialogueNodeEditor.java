package GUI;

import Core.DialogueNode;
import Core.GamePanel;
import java.awt.Component;
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
			return new GamePanel ((DialogueNode) value);
		}
		
		return super.getTreeCellEditorComponent (tree, value, isSelected, expanded, leaf, row);
	}
}
