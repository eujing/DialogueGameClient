package Game;

import Core.DialogueNode;
import GUI.DynamicTree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class DialogueTree {

	private DynamicTree dTree;
	private DialogueNode root;
	private ArrayList<DialogueTreeChangeListener> listeners;

	public DialogueTree () {
		this.listeners = new ArrayList<> ();
		this.dTree = new DynamicTree ();
		this.addChangeListener (new DialogueTreeChangeListener () {
			@Override
			public void treeChanged (DialogueNode parent, DialogueNode newNode) {
				if (parent != null) {
					dTree.addChild (parent, newNode);
				}
			}
		});
	}

	public void setRoot (DialogueNode root) {
		this.root = root;
		this.dTree.setRoot (root);
	}

	//Breadth first search based on node id
	private DialogueNode getNode (int id) {
		LinkedList<DialogueNode> searchQueue = new LinkedList<> ();
		if (id == 0) {
			return null;
		}

		searchQueue.clear ();
		searchQueue.add (this.root);

		DialogueNode current;
		while (!searchQueue.isEmpty ()) {
			current = searchQueue.pop ();

			if (current.id == id) {
				return current;
			}
			else if (!current.childrenNodes.isEmpty ()) {
				for (DialogueNode node : current.childrenNodes) {
					searchQueue.add (node);
				}
			}
		}

		return null;
	}

	private void notifyListeners (DialogueNode parent, DialogueNode newNode) {
		for (DialogueTreeChangeListener listener : listeners) {
			listener.treeChanged (parent, newNode);
		}
	}

	public final void addChangeListener (DialogueTreeChangeListener listener) {
		this.listeners.add (listener);
	}

	public DialogueNode addDialogueNode (DialogueNode node) {
		DialogueNode parent = this.getNode (node.parentId);

		if (parent == null) {
			this.setRoot (node);
		}
		else {
			Collections.sort (parent.childrenNodes);
			parent.childrenNodes.add (node);
		}

		this.notifyListeners (parent, node);

		return parent;
	}

	public DialogueNode getRootDialogueNode () {
		return this.root;
	}

	public DynamicTree getDynamicTree () {
		return this.dTree;
	}
}
