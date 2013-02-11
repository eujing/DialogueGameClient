package GUI;

import Core.MessageHandler;

public class StudentPanel extends UserPanel  {

	public StudentPanel (final MessageHandler msgHandler) {
		super (msgHandler);
		initialize ();
	}
	
	private void initialize () {
		this.addWithinPanel (this, this.getSkipButton());
		this.addWithinPanel (this, this.getLoadTreeButton ());
		this.addWithinPanel (this, this.getSaveTreeButton ());
	}
}

