package GUI;

import Core.Logger;
import Game.GameEngine;
import Game.GameEngine.PlayerType;
import Networking.Client;
import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GameFrame extends JFrame {

	private Client client;

	public GameFrame() {
		String name = "";
		if (GameEngine.PLAYER_TYPE == PlayerType.STUDENT) {
			name = JOptionPane.showInputDialog(null, "Enter name:");
		} else if (GameEngine.PLAYER_TYPE == PlayerType.TEACHER) {
			name = "Teacher";
		}

		this.client = new Client(name, "127.0.0.1", (short) 3000);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initialize ();
			}
		});
		this.client.startListening();
	}

	private void initialize () {
		try {
			NimRODTheme nt = new NimRODTheme("Resources/Light.theme");
			NimRODLookAndFeel nf = new NimRODLookAndFeel();
			nf.setCurrentTheme(nt);
			UIManager.setLookAndFeel(nf);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.logDebug(ex.getMessage() + " " + ex.getCause());
		}
		setLayout(new BorderLayout());
		setTitle("Dialogue Game - " + GameEngine.PLAYER_TYPE.toString());
		setPreferredSize(new Dimension(1000, 720));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.add(new JScrollPane(this.client.gEngine.getTree()), BorderLayout.CENTER);

		pack();
		setVisible(true);

		showController();
		
	}

	private void showController() {
		if (GameEngine.PLAYER_TYPE == PlayerType.TEACHER) {
			new TeacherMenu(client.getMessageHandler(), this);
		}
	}

	public static void main(String[] args) {
		if (args.length == 1) {
			switch (args[0]) {
				case "-student":
					GameEngine.PLAYER_TYPE = PlayerType.STUDENT;
					break;
				case "-teacher":
					GameEngine.PLAYER_TYPE = PlayerType.TEACHER;
					break;
			}
		}
		new GameFrame();
	}
}
