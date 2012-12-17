package GUI;

import Networking.Client;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GameFrame extends JFrame {
	private Client client;

	public GameFrame () {
		String name = JOptionPane.showInputDialog (null, "Enter name:");
		this.client = new Client (name, "127.0.0.1", (short) 3000);
		
		EventQueue.invokeLater (new Runnable () {
			@Override
			public void run () {
				setLayout (new BorderLayout ());
				setTitle ("Dialogue Game");
				setPreferredSize (new Dimension (1280, 720));
				setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

				initComponents ();

				pack ();
				setVisible (true);
			}
		});
		this.client.startListening ();
	}

	

	private void initComponents () {
		this.add (this.client.gEngine.getTree (), BorderLayout.CENTER);
	}

	public static void main (String[] args) {
		new GameFrame ();
	}
}
