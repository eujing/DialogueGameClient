package Mapping;

import Core.DialogueNode;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxCellState;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class DialogueMouseListener implements MouseListener, MouseMotionListener, MouseWheelListener {

	private mxGraphComponent graphComponent;
	private Rectangle rect;
	private Point pressed;
	private Point upTo;

	public DialogueMouseListener(mxGraphComponent graphComponent) {
		this.graphComponent = graphComponent;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.pressed = e.getPoint();
		this.rect = this.graphComponent.getViewport().getVisibleRect();
		mxCell cell = (mxCell) this.graphComponent.getCellAt(this.pressed.x, this.pressed.y);
		if (cell != null) {
			Object value = cell.getValue();
			if (value instanceof DialogueNode) {
				DialogueNode dNode = (DialogueNode) value;
				mxCellState state = this.graphComponent.getGraph ().getView().getState(cell);
				DialoguePopupMenu info = new DialoguePopupMenu(dNode);
				info.show(graphComponent,
						(int) (state.getX() - graphComponent.getHorizontalScrollBar().getValue() + state.getWidth()),
						(int) (state.getY() - graphComponent.getVerticalScrollBar().getValue()));
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.upTo = e.getPoint();
		this.rect.x += pressed.x - upTo.x;
		this.rect.y += pressed.y - upTo.y;
		this.graphComponent.getViewport().scrollRectToVisible(this.rect);
	}

	@Override
	public void mouseWheelMoved (MouseWheelEvent e) {
		if (e.getWheelRotation() < 0) {
			this.graphComponent.zoomIn();
		}
		else if (e.getWheelRotation() > 0) {
			this.graphComponent.zoomOut();
		}
	}
	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
