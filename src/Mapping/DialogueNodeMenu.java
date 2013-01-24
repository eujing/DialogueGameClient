package Mapping;

import Core.DialogueNode;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

public class DialogueNodeMenu extends JPopupMenu {

	public DialogueNodeMenu (DialogueNode node, mxGeometry geom, mxGraphComponent graphComponent) {
		this.setLayout (new GridLayout (5, 1));
		this.addLabel ("id = " + node.id);
		this.addLabel ("parentId = " + node.parentId);
		this.addLabel ("playerName = " + node.playerName);
		this.addLabel ("text = " + node.text);
		this.addLabel ("type = " + node.type);
		this.pack ();
		this.show (graphComponent, (int) geom.getX (), (int) (geom.getY () + geom.getHeight ()));
	}
	
	private void addLabel (String str) {
		this.add  (new JLabel (str));
	}
}

