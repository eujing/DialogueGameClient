package GUI;

import Core.Logger;
import Game.GameEngine;
import Game.GameEngine.PlayerType;
import Networking.Client;
import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class GameFrame extends JFrame {

	private static final String THEME = "Resources/Light.theme";
	private static final int FRAME_WIDTH = 640;
	private static final int FRAME_HEIGHT = 720;
	private GameEngine gEngine;

	public GameFrame () {
		this.gEngine = new GameEngine (this.getPlayerName ());
		SwingUtilities.invokeLater (new Runnable () {
			@Override
			public void run () {
				initialize ();
			}
		});
	}
	
	private String getPlayerName () {
		String name = "";
		if (GameEngine.PLAYER_TYPE == PlayerType.STUDENT) {
			name = JOptionPane.showInputDialog (null, "Enter name:");
		}
		else if (GameEngine.PLAYER_TYPE == PlayerType.TEACHER) {
			name = "Teacher";
		}
		
		return name;
	}
	
	private void setLookAndFeel () {
		try {
			NimRODTheme nt = new NimRODTheme (THEME);
			NimRODLookAndFeel nf = new NimRODLookAndFeel ();
			nf.setCurrentTheme (nt);
			UIManager.setLookAndFeel (nf);
		}
		catch (UnsupportedLookAndFeelException ex) {
			Logger.logDebug (ex.getMessage () + " " + ex.getCause ());
		}
	}

	private void initialize () {
		this.setLookAndFeel ();
		
		//Set frame properties
		setTitle ("Dialogue Game - " + GameEngine.PLAYER_TYPE.toString ());
		setPreferredSize (new Dimension (FRAME_WIDTH, FRAME_HEIGHT));
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		//Add panels
		JPanel panel = new JPanel (new BorderLayout ());
		panel.add (new JScrollPane (this.gEngine.getTree ()), BorderLayout.CENTER);
		
		
		this.add (new JLayer <> (panel, this.gEngine.getLayerUI ()));
		
		//On closing
		this.addWindowListener (new WindowAdapter () {
			@Override
			public void windowClosing (WindowEvent e) {
				if (!gEngine.getTreeSaved ()) {
					gEngine.saveTree ();
				}
			}
		});

		pack ();
		setVisible (true);

		gEngine.getTeacherMenu (this).setVisible (true);
	}

	public static void main (String[] args) {
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
		new GameFrame ();
	}
}
