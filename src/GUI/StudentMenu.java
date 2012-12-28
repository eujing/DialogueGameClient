package GUI;

import Core.Message;
import Core.MessageHandler;
import Core.MessageTag;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StudentMenu extends JFrame  {
	
	public JLabel lblTurn;
	public JButton bSkip;

	public StudentMenu (final MessageHandler msgHandler, final Component invoker) {
		initialize (msgHandler, invoker);
	}
	
	private void initialize (final MessageHandler msgHandler, Component invoker) {
		this.setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable (false);
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
		this.setLocationRelativeTo (invoker);
		this.setLocation (invoker.getWidth (), 0);
		this.pack ();
	}
	
	private void addWithinPanel (Container container, Component comp) {
		JPanel tmpPanel = new JPanel (new FlowLayout ());
		tmpPanel.add (comp);
		container.add (tmpPanel);
	}
}

