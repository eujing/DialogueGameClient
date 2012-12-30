package Mapping;

import Core.DialogueNode;
import Core.Logger;
import Game.DialogueTree;
import Game.DialogueTreeChangeListener;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.JPanel;

public class DialogueMap extends JPanel {

	private mxGraphComponent graphComponent;
	private mxGraph graph;
	private HashMap<DialogueNode, mxCell> vertexMap;

	public DialogueMap (DialogueTree tree) {
		this.graph = new mxGraph () {
			@Override
			public String convertValueToString (Object cell) {
				if (cell instanceof mxCell) {
					Object value = ((mxCell) cell).getValue ();
					if (value instanceof DialogueNode) {
						return ((DialogueNode) value).text;
					}
				}
				
				return cell.toString ();
			}
		};
		this.vertexMap = new HashMap<> ();
		this.setLayout (new BorderLayout ());
		this.graphComponent = new mxGraphComponent (this.graph);
		this.add (this.graphComponent, BorderLayout.CENTER);
		this.setVisible (true);

		tree.addChangeListener (new DialogueTreeChangeListener () {
			@Override
			public void treeChanged (DialogueNode parent, DialogueNode newNode) {
				update (parent, newNode);
				arrange ();
				autoScale ();
			}
		});
		
		this.graphComponent.addMouseListener (new MouseAdapter () {
			@Override
			public void mouseReleased (MouseEvent e) {
				Object obj = graphComponent.getCellAt (e.getX (), e.getY ());
				if (obj != null && obj instanceof mxCell) {
					mxCell cell = (mxCell) obj;
					obj = cell.getValue ();
					if (obj instanceof DialogueNode) {
						DialogueNode dNode = (DialogueNode) obj;
						Logger.log (dNode.toString ());
					}
				}
			}
		});
		
	}

	public void update (DialogueNode parent, DialogueNode newNode) {
		this.graph.getModel ().beginUpdate ();

		Object defaultParent = this.graph.getDefaultParent ();
		try {
			mxCell parentVertex = this.vertexMap.get (parent);
			mxCell childVertex = (mxCell) this.graph.insertVertex (defaultParent, null, newNode, 100, 100, 80, 30);
			this.vertexMap.put (newNode, childVertex);
			childVertex.setValue (newNode);
		

			if (parent != null) {
				this.graph.insertEdge (defaultParent, null, newNode.type.toString (), parentVertex, childVertex);
			}
		}
		finally {
			this.graph.getModel ().endUpdate ();
		}
	}
	
	public void arrange () {
		mxFastOrganicLayout layout = new mxFastOrganicLayout (this.graph);
		layout.setForceConstant (200);
		layout.setDisableEdgeStyle (false);
		
		this.graph.getModel ().beginUpdate ();
		try {
			layout.execute (this.graph.getDefaultParent ());
		}
		finally {

			this.graph.getModel ().endUpdate ();
		}
	}
	
	public void autoScale () {
		Dimension graphSize = this.graphComponent.getGraphControl ().getSize ();
		Dimension viewPortSize = this.graphComponent.getViewport ().getSize ();
		
		if (graphSize.width > 0 && graphSize.height > 0) {
			double scaleWidth = (double) viewPortSize.width / (double) graphSize.width;
			double scaleHeight = (double) viewPortSize.height / (double) graphSize.height;
			this.graphComponent.zoomTo (scaleWidth > scaleHeight ? scaleWidth : scaleHeight, true); 
		}
		
		this.repaint ();
	}
}
