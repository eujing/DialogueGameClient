package GUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DefaultMenu extends JFrame  {
	
	protected static int width = 300;
	protected static int height = 300;
	
	public DefaultMenu (final Component invoker) {
		this.setPreferredSize (new Dimension (DefaultMenu.width, DefaultMenu.height));
		this.setDefaultCloseOperation (JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable (false);
		this.setLocationRelativeTo (invoker);
		this.setLocation (invoker.getWidth (), 0);
	}
	
	protected void addWithinPanel (Container container, Component comp) {
		JPanel tmpPanel = new JPanel (new FlowLayout ());
		tmpPanel.add (comp);
		container.add (tmpPanel);
	}
}

