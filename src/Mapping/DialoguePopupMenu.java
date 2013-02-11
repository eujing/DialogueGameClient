package Mapping;

import Core.DialogueNode;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

public class DialoguePopupMenu extends JPopupMenu {

	public DialoguePopupMenu (DialogueNode dNode) {
		super (dNode.playerName);
		this.setLayout(new GridLayout (2, 1));
		this.add(new JLabel (dNode.playerName));
		JTextArea text = new JTextArea (dNode.text);
		text.setEditable(false);
		text.setLineWrap(true);
		this.add (text);
	}
}
