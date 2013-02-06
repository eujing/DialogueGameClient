package Mapping;

import Core.DialogueNode;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

public class DialogueGraph extends mxGraph {

	@Override
	public String convertValueToString (Object cell) {
		if (cell instanceof mxCell) {
			Object value = ((mxCell) cell).getValue ();
			if (value instanceof DialogueNode) {
				return ((DialogueNode) value).playerName;
			}
			else if (value instanceof String) {
				return value.toString ();
			}
		}

		return cell.toString ();
	}
	
	@Override
	public void drawState (mxICanvas canvas, mxCellState state, boolean drawLabel) {
		String label = (drawLabel) ? state.getLabel() : "";
		
		if (getModel().isVertex (state.getCell ()) &&
			canvas instanceof mxImageCanvas &&
			((mxImageCanvas) canvas).getGraphicsCanvas() instanceof DialogueCanvas) {
			
			((DialogueCanvas) ((mxImageCanvas) canvas).getGraphicsCanvas()).drawVertex(state, label);
		}
		else if (getModel().isVertex (state.getCell ()) &&
				 canvas instanceof DialogueCanvas) {
			((DialogueCanvas) canvas).drawVertex(state, label);
		}
		else {
			super.drawState (canvas, state, drawLabel);
		}
	}
}
