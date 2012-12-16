package Game;

import Core.DialogueNode;
import Core.ResponseHandler;
import Core.XmlReader;
import Core.XmlWriter;
import GUI.DynamicTree;
import java.util.Collections;
import java.util.LinkedList;

public class GameEngine {
	private DynamicTree tree;
	private String currentTurn;
	private DialogueNode root;
	private LinkedList<DialogueNode> searchQueue;

	public GameEngine () {
		this.tree = new DynamicTree ();
		this.currentTurn = null;
		this.root = null;
		this.searchQueue = new LinkedList<> ();
		createTestTree ();
	}
	
	private void createTestTree () {
		DialogueNode pRoot = new DialogueNode (0, "Teacher", "SEED", ResponseHandler.Response.QUESTION); //1
		DialogueNode p1_1 = new DialogueNode (1, "P1", "CHALLENGE_1", ResponseHandler.Response.CHALLENGE);//2
		DialogueNode p2_1 = new DialogueNode (1, "P2", "CHALLENGE_1", ResponseHandler.Response.CHALLENGE);//3
		DialogueNode p1_2 = new DialogueNode (3, "P1", "CHALLENGE_2", ResponseHandler.Response.CHALLENGE);//4
		DialogueNode p2_2 = new DialogueNode (1, "P2", "INFOMATION_1", ResponseHandler.Response.INFORMATION);//5

		this.setRoot (pRoot);

		this.addDialogueNode (pRoot);
		this.addDialogueNode (p1_1);
		this.addDialogueNode (p2_1);
		this.addDialogueNode (p1_2);
		this.addDialogueNode (p2_2);
	}

	public void setRoot (DialogueNode node) {
		this.root = node;
		this.tree.setRoot (node);
	}

	//Breadth first search
	public DialogueNode getNode (int id) {
		if (id == 0) {
			return null;
		}

		this.searchQueue.clear ();
		this.searchQueue.add (this.root);

		DialogueNode current;
		while (!this.searchQueue.isEmpty ()) {
			current = this.searchQueue.pop ();

			if (current.id == id) {
				return current;
			}
			else if (!current.childrenNodes.isEmpty ()) {
				for (DialogueNode node : current.childrenNodes) {
					this.searchQueue.add (node);
				}
			}
		}

		return null;
	}
	
	public void addDialogueNode (DialogueNode node) {
		DialogueNode parent = this.getNode (node.parentId);

		if (parent == null) {
			this.tree.setRoot (node);
			this.setRoot (node);
		}
		else {
			Collections.sort (parent.childrenNodes);
			parent.childrenNodes.add (node);
			this.tree.addChild (parent, node);
		}
	}

	public DynamicTree getTree () {
		return this.tree;
	}

	public void saveTree () {
		XmlWriter.WriteTree ("tree.xml", this.root);
	}

	public DialogueNode readTree () {
		return XmlReader.ReadTree ("tree.xml");
	}
}
