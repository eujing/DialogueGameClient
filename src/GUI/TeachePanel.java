package GUI;

import Core.Message;
import Core.MessageHandler;
import Core.MessageTag;
import Core.ResponseMenu;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class TeachePanel extends DefaultPanel {

	private ResponseMenu responseMenu;
	private JButton bStart;
	private JButton bStop;

	public TeachePanel (final MessageHandler msgHandler) {
		super (msgHandler);
		initialize ();
	}

	private void initialize () {
		this.setLayout (new GridLayout (1, 2));
		this.bStart = new JButton ("Start New Game");
		this.bStop = new JButton ("Stop");
		
		this.responseMenu = new ResponseMenu (msgHandler, bStart);

		this.bStart.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				responseMenu.showMenu ();
			}
		});

		this.bStop.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				msgHandler.submitSendingMessage (new Message (MessageTag.STOP_GAME, "Teacher", ""));
			}
		});

		this.addWithinPanel (this, this.bStart);
		this.addWithinPanel (this, this.bStop);
		this.addWithinPanel (this, getLoadTreeButton ());
	}
}
