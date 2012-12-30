package GUI;

import Core.Logger;
import Game.GameEngine;
import Game.GameEngine.PlayerType;
import Mapping.DialogueMap;
import com.mxgraph.view.mxGraph;
import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
public class GameFrame extends JFrame {

	private static final String THEME = "Resources/Light.theme";
	private static final int FRAME_WIDTH = 640;
	private static final int FRAME_HEIGHT = 720;
	private static final String[] fileExtensions = {"jpg", "png", "gif"};
	private GameEngine gEngine;
	private JTabbedPane tabbedPane;

	public GameFrame () {
		this.gEngine = new GameEngine (this.getPlayerName (), this.getPlayerAvatar ());
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
	
	private ImageIcon getPlayerAvatar () {
		JFileChooser fileChooser = new JFileChooser ();
		fileChooser.setFileFilter (new FileFilter () {		
			@Override
			public String getDescription () {
				String buffer = "";
				for (int i = 0; i < fileExtensions.length; i++) {
					if (i + 1 < fileExtensions.length) {
						buffer += fileExtensions[i] + ", ";
					}
					else {
						buffer += "and " + fileExtensions[i];
					}
				}
				
				return buffer;
			}
			
			@Override
			public boolean accept (File file) {
				for (String extension : fileExtensions) {
					if (file.getName ().toLowerCase ().endsWith (extension)) {
						return true;
					}
				}
				
				return false;
			}
		});
		int value = fileChooser.showOpenDialog (this);
		
		switch (value) {
			case JFileChooser.APPROVE_OPTION: {
				return new ImageIcon (fileChooser.getSelectedFile ().getAbsolutePath ());
			}
			default: {
				return null;
			}
		}
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
		
		this.tabbedPane = new JTabbedPane ();
		
		//Set frame properties
		setTitle ("Dialogue Game - " + GameEngine.PLAYER_TYPE.toString ());
		setPreferredSize (new Dimension (FRAME_WIDTH, FRAME_HEIGHT));
		setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		//Add panels
		JPanel panel = new JPanel (new BorderLayout ());
		panel.add (new JScrollPane (this.gEngine.getDynamicTree ()), BorderLayout.CENTER);
		
		
		this.tabbedPane.add ("Dialogue", new JLayer <> (panel, this.gEngine.getLayerUI ()));
		this.tabbedPane.add ("Map", new JScrollPane (new DialogueMap (this.gEngine.getDialogueTree ())));
		
		this.add (this.tabbedPane);
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

		gEngine.getControlPanel (this).setVisible (true);
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
