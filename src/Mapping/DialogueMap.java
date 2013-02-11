package Mapping;

import Core.DialogueNode;
import Core.FileIO;
import Core.Logger;
import Game.DialogueTree;
import Game.DialogueTreeChangeListener;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;
import java.util.HashMap;
import javax.swing.JPanel;

public class DialogueMap extends JPanel {

	private mxGraphComponent graphComponent;
	private mxGraph graph;
	private mxFastOrganicLayout layout;
	private HashMap<DialogueNode, mxCell> vertexMap;

	public DialogueMap(DialogueTree tree) {
		super(new BorderLayout());
		this.graph = new DialogueGraph();
		this.graph.setCellsEditable(false);
		this.graph.setAutoSizeCells(true);

		this.vertexMap = new HashMap<>();
		this.graphComponent = new mxGraphComponent(this.graph);
		DialogueMouseListener dragListener = new DialogueMouseListener (this.graphComponent);
		this.graphComponent.getGraphControl().addMouseListener(dragListener);
		this.graphComponent.getGraphControl().addMouseMotionListener(dragListener);
		this.graphComponent.getGraphControl().addMouseWheelListener(dragListener);
		this.add(this.graphComponent, BorderLayout.CENTER);
		this.setVisible(true);

		tree.addChangeListener(new DialogueTreeChangeListener() {
			@Override
			public void treeChanged(DialogueNode parent, DialogueNode newNode) {
				update(parent, newNode);
				arrange();
				autoScale();
			}
		});
		this.layout = new mxFastOrganicLayout(this.graph);
		this.layout.setForceConstant(200);
	}

	public void update(DialogueNode parent, DialogueNode newNode) {
		this.graph.getModel().beginUpdate();

		Object defaultParent = this.graph.getDefaultParent();

		String defaultFilePath = "./Avatars/" + newNode.playerName + ".png";
		File file = new File(newNode.avatarLocation != null ? newNode.avatarLocation : defaultFilePath);

		if (!file.exists()) {
			FileIO.writeImage(newNode.avatar, defaultFilePath, "PNG");
		}
		try {
			mxCell parentVertex = this.vertexMap.get(parent);
			mxCell childVertex = (mxCell) this.graph.insertVertex(defaultParent, null, newNode, this.getWidth() / 2, this.getHeight() / 2, 180, 80);
			this.vertexMap.put(newNode, childVertex);
			childVertex.setValue(newNode);
			childVertex.setStyle("shape=label;image=file:/" + file.getAbsolutePath() + ";"
					+ "perimeter=rectanglePerimeter;fontStyle=1;"
					+ "align=center;verticalAlign=middle;"
					+ "imageAlign=left;imageWidth=64;imageHeight=64;rounded=1;"
					+ "shadow=1;glass=1;");

			if (parent == null) {
				//childVertex.setStyle("fillColor=red");
			} else {
				this.graph.insertEdge(defaultParent, null, newNode.type.getVerb(), childVertex, parentVertex);
			}
		} catch (Exception ex) {
			Logger.logException("DialogueMap::update", ex);
		} finally {
			this.graph.getModel().endUpdate();
		}
	}

	public void arrange() {
		this.graph.getModel().beginUpdate();
		try {
			this.layout.execute(this.graph.getDefaultParent());
		} finally {
			this.graph.getModel().endUpdate();
		}
	}

	public void autoScale() {
		Dimension graphSize = this.graphComponent.getGraphControl().getSize();
		Dimension viewPortSize = this.graphComponent.getViewport().getSize();

		//Zoom to fit
		if (graphSize.width > 0 && graphSize.height > 0) {
			double scaleWidth = viewPortSize.getWidth() / graphSize.getWidth();
			double scaleHeight = viewPortSize.getHeight() / graphSize.getHeight();
			this.graphComponent.zoom(scaleWidth < scaleHeight ? scaleWidth : scaleHeight);
		}

	}

	public void scrollToCenter() {
		Dimension graphSize = this.graphComponent.getGraphControl().getSize();
		Dimension viewPortSize = this.graphComponent.getViewport().getSize();

		int x = graphSize.width / 2 - viewPortSize.width / 2;
		int y = graphSize.height / 2 - viewPortSize.height / 2;
		int w = viewPortSize.width;
		int h = viewPortSize.height;
		this.graphComponent.scrollRectToVisible(new Rectangle(x, y, w, h));
	}

	public void clear() {
		graph.getModel().beginUpdate();
		try {
			graph.removeCells(graph.getChildCells(graph.getDefaultParent(), true, true));
			vertexMap.clear();
		} catch (Exception ex) {
			Logger.logException("DialogueMap::clear", ex);
		} finally {
			graph.getModel().endUpdate();
		}
	}

	public void loadTree(DialogueTree tree) {
		DialogueNode rootNode = tree.getRootDialogueNode();
		update(null, rootNode);
		recurseAddVertex(rootNode);
		arrange();
		autoScale();
		this.graphComponent.scrollCellToVisible(vertexMap.get(rootNode));
	}

	public void recurseAddVertex(DialogueNode parent) {
		for (DialogueNode childNode : parent.childrenNodes) {
			update(parent, childNode);
			recurseAddVertex(childNode);
		}
	}
}
