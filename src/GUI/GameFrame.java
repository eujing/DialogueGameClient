package GUI;

import Core.ExtensionFileFilter;
import Core.FileIO;
import Core.Logger;
import Game.GameEngine;
import Game.GameEngine.PlayerType;
import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

public class GameFrame extends JFrame {
	
	private static final String THEME = "Resources/Light.theme";
	private static final int FRAME_WIDTH = 640;
	private static final int FRAME_HEIGHT = 720;
	private static final String[] fileExtensions = {"jpg", "png", "gif"};
	private GameEngine gEngine;
	private JTabbedPane tabbedPane;
	
	public GameFrame() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initialize();
			}
		});
	}
	
	private String getPlayerName() {
		String name = "";
		if (GameEngine.PLAYER_TYPE == PlayerType.STUDENT) {
			name = JOptionPane.showInputDialog(null, "Enter name:");
		} else if (GameEngine.PLAYER_TYPE == PlayerType.TEACHER) {
			name = "Teacher";
		}
		
		return name;
	}
	
	private ImageIcon getPlayerAvatar() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select an avatar");
		fileChooser.setFileFilter(new ExtensionFileFilter(fileExtensions));
		int value = fileChooser.showOpenDialog(this);
		
		switch (value) {
			case JFileChooser.APPROVE_OPTION: {
				try {
					return new ImageIcon(FileIO.getImage(fileChooser.getSelectedFile().toURI().toURL(), 64, 64));
				} catch (Exception ex) {
					Logger.logException("GameFrame::getplayerAvatar", ex);
				}
			}
			default: {
				return new ImageIcon(FileIO.getImage (this.getClass().getResource(GameEngine.NO_AVATAR), 64, 64));
			}
		}
	}
	
	private void setLookAndFeel() {
		try {
			NimRODTheme nt = new NimRODTheme(THEME);
			NimRODLookAndFeel nf = new NimRODLookAndFeel();
			nf.setCurrentTheme(nt);
			UIManager.setLookAndFeel(nf);
		} catch (UnsupportedLookAndFeelException ex) {
			Logger.logException("GameFrame::setLookAndFeel", ex);
		}
	}
	
	private void initialize() {
		this.setLookAndFeel();
		this.setLayout(new BorderLayout());
		
		String name = this.getPlayerName();
		this.gEngine = new GameEngine(name, this.getPlayerAvatar());
		
		this.tabbedPane = new JTabbedPane();

		//Set frame properties
		String title = "Dialogue Game - " + GameEngine.PLAYER_TYPE.toString();
		if (GameEngine.PLAYER_TYPE == GameEngine.PlayerType.STUDENT) {
			title += ": " + name;
		}
		setTitle(title);
		setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add panels
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(this.gEngine.getDynamicTree()), BorderLayout.CENTER);

		//Add Dialogue and Map tabs
		this.tabbedPane.add("Dialogue", new JLayer<>(panel, this.gEngine.getLayerUI()));
		this.tabbedPane.add("Map", this.gEngine.getDialogueMap());


		//Add tabbed pane to center
		this.getContentPane().add(this.tabbedPane, BorderLayout.CENTER);

		//Add control panel to bottom
		this.getContentPane().add(gEngine.getControlPanel(), BorderLayout.PAGE_END);

		//On closing
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (!gEngine.getTreeSaved()) {
					gEngine.showMapSaveDialogue();
				}
			}
		});
		
		pack();
		setVisible(true);
		this.setLocationRelativeTo(null);
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
