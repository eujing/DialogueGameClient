package GUI;

import Core.DialogueNode;
import Core.ResponseHandler.Response;
import Networking.Client;
import Networking.IUpdatable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GameFrame extends JFrame implements IUpdatable {
	private DynamicTree tree;
	private Client client;

	public GameFrame () {
		String name = JOptionPane.showInputDialog (null, "Enter name:");
		this.client = new Client (name, "127.0.0.1", (short) 3000, this);
		
		EventQueue.invokeLater (new Runnable () {
			@Override
			public void run () {
				setLayout (new BorderLayout ());
				setTitle ("Dialogue Game");
				setPreferredSize (new Dimension (1280, 720));
				setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

				initComponents ();
				createTestTree ();

				pack ();
				setVisible (true);
			}
		});
	}

	@Override
	public void addDialogueNode (DialogueNode node) {
		DialogueNode parent = client.gEngine.getNode (node.parentId);
		if (parent == null) {
			this.tree.setRoot (node);
		}
		else {
			this.tree.addChild (parent, node);
		}
	}

	private void initComponents () {
		this.tree = new DynamicTree ();
		
		this.add (this.tree, BorderLayout.CENTER);
	}

	private void createTestTree () {
		DialogueNode root = new DialogueNode (0, "Teacher", "SEED", Response.QUESTION); //1
		DialogueNode p1_1 = new DialogueNode (1, "P1", "CHALLENGE_1", Response.CHALLENGE);//2
		DialogueNode p2_1 = new DialogueNode (1, "P2", "CHALLENGE_1", Response.CHALLENGE);//3
		DialogueNode p1_2 = new DialogueNode (3, "P1", "CHALLENGE_2", Response.CHALLENGE);//4
		DialogueNode p2_2 = new DialogueNode (1, "P2", "INFOMATION_1", Response.INFORMATION);//5
		
		this.client.gEngine.setRoot (root);
		
		this.addDialogueNode (root);
		this.addDialogueNode (p1_1);
		this.addDialogueNode (p2_1);
		this.addDialogueNode (p1_2);
		this.addDialogueNode (p2_2);
	}

	public static void main (String[] args) {
		new GameFrame ();
	}
}
