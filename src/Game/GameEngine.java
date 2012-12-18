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
	private DialogueNode root;
	private LinkedList<DialogueNode> searchQueue;
	private boolean treeSaved;

	public GameEngine (MessageHandler msgHandler) {
		this.tree = new DynamicTree ();
		this.msgHandler = msgHandler;
		this.root = null;
		this.searchQueue = new LinkedList<> ();
		this.treeSaved = false;
		//createTestTree ();
	}
	
	private void createTestTree () {
		DialogueNode pRoot = new DialogueNode (0, "Teacher", "SEED", ResponseType.QUESTION, this.msgHandler); //1
		DialogueNode p1_1 = new DialogueNode (1, "P1", "CHALLENGE_1", ResponseType.CHALLENGE, this.msgHandler);//2
		DialogueNode p2_1 = new DialogueNode (1, "P2", "CHALLENGE_1", ResponseType.CHALLENGE, this.msgHandler);//3
		DialogueNode p1_2 = new DialogueNode (3, "P1", "CHALLENGE_2", ResponseType.CHALLENGE, this.msgHandler);//4
		DialogueNode p2_2 = new DialogueNode (1, "P2", "INFOMATION_1", ResponseType.INFORMATION, this.msgHandler);//5

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
		this.treeSaved = false;
	}
	
	public void setTurn (boolean currentTurn) {
		this.tree.setRespondEnabled (currentTurn);
	}
	
	public void stopGame () {
		this.tree.clear ();
		this.saveTree ();
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
		
		this.treeSaved = false;
	}

	public DynamicTree getTree () {
		return this.tree;
	}
	
	public boolean getTreeSaved () {
		return this.treeSaved;
	}

	public void saveTree () {
		XmlWriter.WriteTree ("tree.xml", this.root);
		this.treeSaved = true;
	}

	public DialogueNode readTree () {
		XmlReader reader = new XmlReader (this.msgHandler);
		return reader.ReadTree ("tree.xml");
	}
}
