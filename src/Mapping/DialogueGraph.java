package Mapping;

import Core.DialogueNode;
import com.mxgraph.canvas.mxICanvas;
import com.mxgraph.canvas.mxImageCanvas;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxUtils;
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
}
