package Mapping;

import Core.DialogueNode;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.view.mxInteractiveCanvas;
import com.mxgraph.view.mxCellState;
import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class DialogueCanvas extends mxInteractiveCanvas {

	protected JLabel vertexRenderer;
	protected mxGraphComponent graphComponent;

	public DialogueCanvas (mxGraphComponent graphComponent) {
		this.rendererPane = new CellRendererPane ();
		this.vertexRenderer = new JLabel ();
		this.graphComponent = graphComponent;

		vertexRenderer.setBorder (BorderFactory.createBevelBorder (BevelBorder.RAISED));
		vertexRenderer.setHorizontalAlignment (JLabel.CENTER);
		vertexRenderer.setBackground (graphComponent.getBackground ().darker ());
		vertexRenderer.setOpaque (true);
	}

	public void drawVertex (mxCellState state, String label) {
		DialogueNode dNode = (DialogueNode) ((mxCell) state.getCell ()).getValue ();
		//vertexRenderer.add (new JLabel (dNode.playerName + ": " + dNode.text));
		vertexRenderer.setText (dNode.playerName + ": " + dNode.text);
		rendererPane.paintComponent (g, vertexRenderer, graphComponent,
									 (int) state.getX () + translate.x,
									 (int) state.getY () + translate.y,
									 (int) state.getWidth (), (int) state.getHeight (), true);
	}
}
