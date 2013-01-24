package Game;

import Core.DialogueNode;

public interface DialogueTreeChangeListener {
	public void treeChanged (DialogueNode parent, DialogueNode newNode);
}

