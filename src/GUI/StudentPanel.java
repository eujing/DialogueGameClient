package GUI;

import Core.Message;
import Core.MessageHandler;
import Core.MessageTag;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class StudentPanel extends DefaultPanel  {

	public JButton bSkip;

	public StudentPanel (final MessageHandler msgHandler) {
		super (msgHandler);
		initialize ();
	}
	
	private void initialize () {
		this.bSkip = new JButton ("Skip turn");
		this.bSkip.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				msgHandler.submitSendingMessage (new Message (MessageTag.SKIP_TURN, "", ""));
			}
		});
		
		this.addWithinPanel (this, this.bSkip);
		this.addWithinPanel (this, this.getLoadTreeButton ());
		this.addWithinPanel (this, this.getSaveTreeButton ());
	}
}

