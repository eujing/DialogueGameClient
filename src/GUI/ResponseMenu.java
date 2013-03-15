package GUI;

import Core.DialogueNode;
import Core.FileIO;
import Core.Message;
import Core.MessageHandler;
import Core.MessageTag;
import Core.ResponseHandler;
import Core.ResponseHandler.ResponseType;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ResponseMenu extends JPopupMenu {

	private Component invoker;
	private ResponseType lastResponse;
	private JButton[] buttons;
	private JList starters;
	private JTextField tfText;
	private JButton bSubmit;

	public ResponseMenu(final MessageHandler msgHandler, final Component invoker) {
		init(invoker);
		setSeedMenu ();

		this.bSubmit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = tfText.getText();
				if (text.length() > 0) {
					DialogueNode rootNode = new DialogueNode(0, "Teacher", text, ResponseType.SEED, msgHandler);
					msgHandler.submitSendingMessage(new Message(MessageTag.START_GAME, "Teacher", rootNode));
				}
			}
		});
	}

	public ResponseMenu(final DialogueNode dNode, final Component invoker) {
		init(invoker);
		setRegularMenu (dNode.type);
		
		this.bSubmit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (lastResponse != null && starters.getSelectedIndex() != -1) {
					String text = starters.getSelectedValue().toString() + " " + tfText.getText();
					DialogueNode partialNode = dNode.newChild("", text, lastResponse);
					dNode.msgHandler.submitSendingMessage(new Message(MessageTag.RESPONSE, "", partialNode));
				}
			}
		});
	}

	private void init(Component invoker) {
		this.invoker = invoker;
		this.tfText = new JTextField();
		this.bSubmit = new JButton("Submit");
	}

	private void setSeedMenu() {
		this.setLayout(new FlowLayout());
		this.tfText.setPreferredSize(new Dimension(400, 25));
		this.add(new JLabel("Seed:"));
		this.add(this.tfText);
		this.add(this.bSubmit);
	}

	private void setRegularMenu(ResponseType type) {
		this.setLayout(new GridLayout(1, 3));
		this.lastResponse = null;
		this.starters = new JList();

		this.tfText.setPreferredSize(new Dimension(200, 25));

		JPanel buttonPanel = createButtonPanel(type);
		JPanel startersPanel = createStartersPanel();
		JPanel rightPanel = createRightPanel();

		this.add(buttonPanel);
		this.add(startersPanel);
		this.add(rightPanel);
	}

	private void addWithinPanel(Container container, Component comp) {
		JPanel tmpPanel = new JPanel(new FlowLayout());
		tmpPanel.add(comp);
		container.add(tmpPanel);
	}

	private JPanel createButtonPanel(final ResponseType response) {
		ResponseHandler respHandler = new ResponseHandler();
		ResponseType[] responses = respHandler.getResponses(response);
		this.buttons = new JButton[responses.length];
		JPanel panel = new JPanel(new GridLayout(responses.length, 1));
		panel.setBorder(BorderFactory.createTitledBorder("Responses"));


		for (int i = 0; i < responses.length; i++) {
			String str = responses[i].toString();
			this.buttons[i] = new JButton(str.charAt(0) + str.substring(1).toLowerCase());
			this.buttons[i].setPreferredSize(new Dimension(100, 25));

			final DefaultListModel responseModel = new DefaultListModel();
			String dir = "/Resources/" + str.toLowerCase() + "_starters.txt";
			for (String line : FileIO.getLines(this.getClass().getResourceAsStream(dir))) {
				responseModel.addElement(line);
			}

			final JButton b = this.buttons[i];
			this.buttons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					lastResponse = ResponseType.valueOf(b.getText().toUpperCase());
					starters.setModel(responseModel);
				}
			});

			this.addWithinPanel(panel, this.buttons[i]);
		}

		return panel;
	}

	private JPanel createStartersPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Starters"));
		panel.add(new JScrollPane(this.starters), BorderLayout.CENTER);

		return panel;
	}

	private JPanel createRightPanel() {
		JPanel rightPanel = new JPanel(new GridLayout(2, 1));
		JPanel topPanel = new JPanel(new FlowLayout());
		topPanel.add(new JLabel("Text:"));
		topPanel.add(this.tfText);
		this.addWithinPanel(rightPanel, topPanel);
		this.addWithinPanel(rightPanel, this.bSubmit);

		return rightPanel;
	}

	public void showMenu() {
		this.show(invoker, 0, invoker.getHeight());
	}

	public void hideMenu() {
		setVisible(false);
	}
}
