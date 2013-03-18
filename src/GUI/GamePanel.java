package GUI;

import Core.DialogueNode;
import Core.FileIO;
import Game.GameEngine;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;

public class GamePanel extends RoundedPanel {
	private ResponseMenu responseMenu;
	
	public GamePanel(final DialogueNode dNode) {
		initComponents();

		this.responseMenu = new ResponseMenu(dNode, bRespond);
		lblName.setText(dNode.playerName);
		if (dNode.avatar == null) {
			ImageIcon img = new ImageIcon (FileIO.getImage (this.getClass ().getResource (GameEngine.NO_AVATAR), 64, 64));
			avatar.setIcon (img);
		}
		else {
			avatar.setIcon (dNode.avatar);
		}
		
		taText.setEditable (false);
		taText.setLineWrap(true);
		taText.setText(dNode.text);
		lblType.setText(capitaliseFirst (dNode.type.toString()));

		this.bRespond.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				responseMenu.showMenu ();
			}
		});
	}
	
	private String capitaliseFirst (String str) {
		String lowerString = str.toLowerCase();
		char c = lowerString.charAt(0);
		return lowerString.replace(c, (char) (c + ('A' - 'a')));
	}
	
	public void setRespondEnabled (boolean enabled) {
		this.bRespond.setEnabled (enabled);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblName = new javax.swing.JLabel();
        lblType = new javax.swing.JLabel();
        bRespond = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        taText = new javax.swing.JTextArea();
        avatar = new javax.swing.JLabel();

        lblName.setText("<name>");

        lblType.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblType.setText("<type>");

        bRespond.setText("Respond");

        taText.setColumns(20);
        taText.setRows(5);
        jScrollPane1.setViewportView(taText);

        avatar.setText("<avatar>");
        avatar.setMinimumSize(new java.awt.Dimension(64, 64));
        avatar.setPreferredSize(new java.awt.Dimension(64, 64));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(avatar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addComponent(bRespond)
                        .addGap(22, 22, 22))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(avatar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblName))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(lblType, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(bRespond)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel avatar;
    private javax.swing.JButton bRespond;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblType;
    private javax.swing.JTextArea taText;
    // End of variables declaration//GEN-END:variables
}
