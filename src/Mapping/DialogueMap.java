package Mapping;

import Core.DialogueNode;
import Core.Logger;
import Game.DialogueTree;
import Game.DialogueTreeChangeListener;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import javax.swing.JPanel;

public class DialogueMap extends JPanel {

	private mxGraphComponent graphComponent;
	private mxGraph graph;
	private mxFastOrganicLayout layout;
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
					else if (value instanceof String) {
						return value.toString ();
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

		this.graph.getSelectionModel ().addListener (mxEvent.CHANGE, new mxIEventListener () {
			@Override
			public void invoke (Object sender, mxEventObject e) {
				if (sender instanceof mxGraphSelectionModel) {
					for (Object obj : ((mxGraphSelectionModel) sender).getCells ()) {
						mxCell cell = (mxCell) obj;
						Object value = cell.getValue ();
						if (value instanceof DialogueNode) {
							DialogueNode dNode = (DialogueNode) value;
							DialogueNodeMenu menu = new DialogueNodeMenu (dNode, cell.getGeometry (), graphComponent);
						}
					}
				}
			}
		});

		this.layout = new mxFastOrganicLayout (this.graph);
		this.layout.setForceConstant (200);
		this.layout.setDisableEdgeStyle (false);

	}

	public void update (DialogueNode parent, DialogueNode newNode) {
		this.graph.getModel ().beginUpdate ();

		Object defaultParent = this.graph.getDefaultParent ();
		try {
			mxCell parentVertex = this.vertexMap.get (parent);
			mxCell childVertex = (mxCell) this.graph.insertVertex (defaultParent, null, newNode, 0, 0, 80, 30);
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
		this.graph.getModel ().beginUpdate ();
		try {
			this.layout.execute (this.graph.getDefaultParent ());
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

	public void clear () {
		mxGraphModel model = (mxGraphModel) this.graph.getModel ();

		model.beginUpdate ();

		Logger.logDebug ("n cells: " + graph.getChildCells (this.graph.getDefaultParent ()).length);
		try {
			//this.graph.removeCells (this.graph.getChildCells (this.graph.getDefaultParent (), true, true));
			for (Object v : this.graph.getChildCells (this.graph.getDefaultParent ())) {
				model.remove (v);
			}
			for (Object e : this.graph.getChildEdges (this.graph.getDefaultParent ())) {
				model.remove (e);
			}
			
			this.vertexMap.clear ();
			
			this.graphComponent.refresh ();
			this.remove (this.graphComponent);
			this.revalidate ();
			this.repaint ();
			//this.graphComponent = new mxGraphComponent (this.graph);
			//this.add (this.graphComponent);
		}
		catch (Exception ex) {
			Logger.logException ("DialogueMap::clear", ex);
		}
		finally {
			model.endUpdate ();
			Logger.logDebug ("n cells: " + graph.getChildCells (this.graph.getDefaultParent ()).length);	
		}
	}
}
