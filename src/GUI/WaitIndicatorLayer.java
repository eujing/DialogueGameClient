package GUI;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.plaf.LayerUI;

public class WaitIndicatorLayer extends LayerUI <JPanel> implements ImageObserver {
	
	private Image waitAnimation;
	private boolean waiting;
	private String currentTurn;
	
	public WaitIndicatorLayer (Image waitAnimation) {
		this.waitAnimation = waitAnimation;
	}
	
	public void update (boolean waiting, String currentTurn) {
		this.waiting = waiting;
		this.currentTurn = currentTurn;
	}
	
	private void drawCenteredString (Graphics g, String str, int x, int y) {
		int strWidth = g.getFontMetrics ().stringWidth (str);
		int strHeight = g.getFontMetrics ().getHeight ();
		g.drawString (str, x - strWidth / 2, y + strHeight);
	}
	
	@Override
	public void paint (Graphics g, JComponent c) {
		super.paint (g, c);
		
		if (!this.waiting || this.waitAnimation == null) {
			return;
		}
		
		Graphics2D g2 = (Graphics2D) g;
		int imgX = (c.getWidth () - waitAnimation.getWidth (this)) / 2;
		int imgY = (c.getHeight () - waitAnimation.getHeight (this)) / 2;
		g2.drawImage (this.waitAnimation,
					  imgX, 
					  imgY,
					  waitAnimation.getWidth (this),
					  waitAnimation.getHeight (this),
					  this);
		drawCenteredString (g, currentTurn + "'s turn...", c.getWidth () / 2, imgY + waitAnimation.getHeight (this));
	}
	
	@Override
	public boolean imageUpdate (Image img, int flags, int x, int y, int w, int h) {
		firePropertyChange ("imageUpdate", 0, 1);
		return true;
	}
	
	@Override
	public void applyPropertyChange (PropertyChangeEvent pce, JLayer layer) {
		if (pce.getPropertyName ().equals ("imageUpdate")) {
			layer.repaint ();
		}
	}
}

