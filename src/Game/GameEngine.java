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
	}
	
	public void setRoot (DialogueNode node) {
		this.root = node;
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
