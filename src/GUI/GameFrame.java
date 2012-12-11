package GUI;

import Core.DialogueNode;
import Networking.Client;
import Networking.IUpdatable;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GameFrame extends JFrame {
	private Client client;
	private JPanel canvas;
	
	public GameFrame() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				setTitle ("Dialogue Game");
				setPreferredSize(new Dimension(100, 100));
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				
				initComponents ();
				
				pack();

				setVisible(true);
			}
		});
		
		String name = JOptionPane.showInputDialog(this, "Enter name:");
		this.client = new Client (name, "127.0.0.1", (short) 3000, new IUpdatable () {
			@Override
			public void addDialogueNode (DialogueNode node) {
				//todo
			}
		});
	}
	
	private void initComponents () {
		this.canvas = new JPanel (null);
		
		this.add(canvas);
	}
	
	public static void main (String[] args) {
		new GameFrame ();
	}
}
