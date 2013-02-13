package Mapping;

import Core.DialogueNode;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class DialoguePopupMenu extends JPopupMenu {

	public DialoguePopupMenu (DialogueNode dNode) {
		//this.setLayout (new);
		//this.setLayout(new GridLayout (2, 1));
		JPanel panel = new JPanel ();
		panel.setBorder (BorderFactory.createTitledBorder(dNode.playerName + " says..."));
		JTextArea text = new JTextArea (dNode.text);
		text.setPreferredSize(new Dimension(200, 75));
		text.setEditable(false);
		text.setLineWrap(true);
		panel.add (new JScrollPane (text), BorderLayout.CENTER);
		this.add (panel);
	}
}
