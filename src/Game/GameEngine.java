package Game;

import Core.DialogueNode;
import Core.MessageHandler;
import Core.ResponseHandler.ResponseType;
import Core.XmlReader;
import Core.XmlWriter;
import GUI.DynamicTree;
import java.util.Collections;
import java.util.LinkedList;

public class GameEngine {
	public enum PlayerType {STUDENT, TEACHER};
	public static PlayerType PLAYER_TYPE = PlayerType.STUDENT;
	private DynamicTree tree;
	private MessageHandler msgHandler;
	private String currentTurn;
	private DialogueNode root;
	private LinkedList<DialogueNode> searchQueue;

	public GameEngine (MessageHandler msgHandler) {
		this.tree = new DynamicTree ();
		this.msgHandler = msgHandler;
		this.currentTurn = null;
		this.root = null;
		this.searchQueue = new LinkedList<> ();
		//createTestTree ();
	}
	
	private void createTestTree () {
		DialogueNode pRoot = new DialogueNode (0, "Teacher", "SEED", ResponseType.QUESTION, this.msgHandler); //1
		//DialogueNode p1_1 = new DialogueNode (1, "P1", "CHALLENGE_1", ResponseHandler.Response.CHALLENGE, this.msgHandler);//2
		//DialogueNode p2_1 = new DialogueNode (1, "P2", "CHALLENGE_1", ResponseHandler.Response.CHALLENGE, this.msgHandler);//3
		//DialogueNode p1_2 = new DialogueNode (3, "P1", "CHALLENGE_2", ResponseHandler.Response.CHALLENGE, this.msgHandler);//4
		//DialogueNode p2_2 = new DialogueNode (1, "P2", "INFOMATION_1", ResponseHandler.Response.INFORMATION, this.msgHandler);//5

		this.setRoot (pRoot);

		this.addDialogueNode (pRoot);
		//this.addDialogueNode (p1_1);
		//this.addDialogueNode (p2_1);
		//this.addDialogueNode (p1_2);
		//this.addDialogueNode (p2_2);
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
		XmlReader reader = new XmlReader (this.msgHandler);
		return reader.ReadTree ("tree.xml");
	}
}
