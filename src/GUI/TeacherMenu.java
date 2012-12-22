package GUI;

import Core.Message;
import Core.MessageHandler;
import Core.MessageTag;
import Core.ResponseMenu;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TeacherMenu extends JFrame {

	private ResponseMenu responseMenu;
	private JButton bStart;
	private JButton bStop;

	public TeacherMenu (final MessageHandler msgHandler, final Component invoker) {
		initialize (msgHandler, invoker);
	}

	private void initialize (final MessageHandler msgHandler, Component invoker) {
		//this.setUndecorated(true);
		this.setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable (false);
		this.setTitle ("Teacher's Control Panel");
		this.setLayout (new GridLayout (2, 1));
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
