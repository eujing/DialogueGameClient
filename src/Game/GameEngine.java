package Game;

import Core.DialogueNode;
import Core.XmlReader;
import Core.XmlWriter;
import java.util.LinkedList;

public class GameEngine {
	private String currentTurn;
	private DialogueNode root;
	private LinkedList <DialogueNode> searchQueue;

	public GameEngine () {
		this.currentTurn = null;
		this.root = null;
		this.searchQueue = new LinkedList <> ();
		
		//createTestTree ();
		//saveTree ();
		
		//this.root = readTree ();
		//saveTree ();
	}
	
	private void createTestTree () {
	}
	
	//Breadth first search
	public DialogueNode getNode (int id) {
		this.searchQueue.clear ();
		this.searchQueue.add (this.root);
		
		DialogueNode current;
		while (!this.searchQueue.isEmpty ()) {
			current = this.searchQueue.pop ();
			
			if (current.id == id) {
				return current;
			}
			else if (!current.children.isEmpty ()) {
				for (DialogueNode node : current.children) {
					this.searchQueue.add (node);
				}
			}
		}
		
		return null;
	}
	
	
	public DialogueNode getTree () {
		return this.root;
	}
	
	public void saveTree () {
		XmlWriter.WriteTree ("tree.xml", this.root);
	}
	
	public DialogueNode readTree () {
		return XmlReader.ReadTree("tree.xml");
	}
}
