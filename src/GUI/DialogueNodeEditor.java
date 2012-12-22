package GUI;

import Core.DialogueNode;
import Core.GamePanel;
import Game.GameEngine;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DialogueNodeEditor extends DefaultTreeCellEditor {

	private GameEngine gEngine;
	private boolean enabled;

	public DialogueNodeEditor (GameEngine gEngine, JTree tree, DefaultTreeCellRenderer renderer) {
		super (tree, renderer);
		this.gEngine = gEngine;
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
			gPanel.setBorder (BorderFactory.createLineBorder (Color.black, 1, true));
			gPanel.setRespondEnabled (this.enabled);
			if (this.gEngine != null && this.gEngine.getMostRecentNodes ().contains (node)) {
				gPanel.setBackground (Color.red);
			}
			return gPanel;
		}

		return super.getTreeCellEditorComponent (tree, value, isSelected, expanded, leaf, row);
	}
}
