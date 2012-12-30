package GUI;

import Core.Message;
import Core.MessageHandler;
import Core.MessageTag;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;

public class StudentMenu extends DefaultMenu  {
	
	public JLabel lblTurn;
	public JButton bSkip;

	public StudentMenu (final MessageHandler msgHandler, final Component invoker) {
		super (invoker);
		initialize (msgHandler, invoker);
	}
	
	private void initialize (final MessageHandler msgHandler, Component invoker) {
		this.setTitle ("Student's Control Panel");
		//this.setLayout (new GridLayout (2, 1));
		this.setLayout (new FlowLayout ());
		
		this.lblTurn = new JLabel ();
		this.bSkip = new JButton ("Skip turn");
		this.bSkip.addActionListener (new ActionListener () {
			@Override
			public void actionPerformed (ActionEvent e) {
				msgHandler.submitSendingMessage (new Message (MessageTag.SKIP_TURN, "", ""));
			}
		});
		
		//this.addWithinPanel (this, this.lblTurn);
		this.addWithinPanel (this, this.bSkip);
		
		this.pack ();
	}
}

