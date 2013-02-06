package GUI;

import Core.ExtensionFileFilter;
import Core.Message;
import Core.MessageHandler;
import Core.MessageTag;
import Game.GameEngine;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class DefaultPanel extends JPanel {

	protected static int width = 300;
	protected static int height = 300;
	protected MessageHandler msgHandler;

	public DefaultPanel (MessageHandler msgHandler) {
		this.msgHandler = msgHandler;
	}

	protected void addWithinPanel (Container container, Component comp) {
		JPanel tmpPanel = new JPanel (new FlowLayout ());
		tmpPanel.add (comp);
		container.add (tmpPanel);
	}

	protected JButton getLoadTreeButton () {
		JButton button = new JButton ("Load Tree");

		button.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser ();
				fileChooser.setCurrentDirectory (GameEngine.currentDir);
				fileChooser.setFileFilter (new ExtensionFileFilter (new String[] {"xml"}));
				switch (fileChooser.showOpenDialog (null)) {
					case JFileChooser.APPROVE_OPTION: {
						msgHandler.submitSendingMessage (new Message (MessageTag.LOAD_TREE, "", fileChooser.getSelectedFile ()));
					}
				}
			}
		});

		return button;
	}
	
	protected JButton getSaveTreeButton () {
		JButton button = new JButton ("Save Tree");
		
		button.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				msgHandler.submitSendingMessage (new Message (MessageTag.SAVE_TREE, "", ""));
			}
		});
		
		return button;
	}
}
