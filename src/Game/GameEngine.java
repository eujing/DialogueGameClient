package Game;

import Core.DialogueNode;
import Core.FileIO;
import Core.Logger;
import Core.Message;
import Core.MessageHandler;
import Core.MessageListener;
import Core.MessageTag;
import Core.XmlReader;
import Core.XmlWriter;
import GUI.DynamicTree;
import GUI.StudentPanel;
import GUI.TeachePanel;
import GUI.WaitIndicatorLayer;
import Mapping.DialogueMap;
import Networking.Client;
import java.awt.Image;
import java.io.File;
import java.util.Calendar;
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class GameEngine {

	public static final String NO_AVATAR = "/Resources/noAvatar.jpg";
	private static final String WAIT_ANIMATION = "/Resources/ActivityIndicator.gif";
	private static final String TURN_NOTIFICATION = "/Resources/ding.wav";

	public enum PlayerType {

		STUDENT, TEACHER
	};
	public static PlayerType PLAYER_TYPE = PlayerType.STUDENT;
	public static final File currentDir = new File(System.getProperty("user.dir"));
	private String playerName;
	private ImageIcon playerAvatar;
	private Client client;
	private int nPlayers;
	private WaitIndicatorLayer waitIndicatorLayer;
	private MessageHandler msgHandler;
	private DialogueTree tree;
	private DialogueMap map;
	private LinkedList<DialogueNode> mostRecent;
	private boolean treeSaved;
	private SoundPlayer turnNotification;

	public GameEngine(String playerName, ImageIcon playerAvatar, String ipAddress, short port) {
		this.playerName = playerName;
		this.playerAvatar = playerAvatar;
		this.nPlayers = 0;
		this.tree = new DialogueTree();
		this.map = new DialogueMap(this.tree);
		this.waitIndicatorLayer = createWaitIndicatorLayer();
		this.msgHandler = new MessageHandler();
		this.registerReceivingListeners(this.msgHandler);
		this.registerSendingListeners(this.msgHandler);
		this.client = new Client(this.msgHandler, playerName, ipAddress, port);
		this.mostRecent = new LinkedList<>();
		this.treeSaved = true;
		this.turnNotification = new SoundPlayer(TURN_NOTIFICATION);

		this.client.startListening();
	}

	private WaitIndicatorLayer createWaitIndicatorLayer() {
		Image image = FileIO.getImage(this.getClass().getResource(WAIT_ANIMATION), 0, 0);
		return new WaitIndicatorLayer(image);
	}

	public void addDialogueNode(DialogueNode node) {
		this.tree.addDialogueNode(node);
		DialogueNode.count++;

		if (this.mostRecent.size() >= this.nPlayers) {
			this.mostRecent.pop().setIsMostRecent(false);
		}

		node.setIsMostRecent(true);
		this.mostRecent.add(node);

		this.treeSaved = false;
	}

	public void setNumberOfPlayers(int n) {
		this.nPlayers = n;
	}

	public void setRoot(DialogueNode node) {
		this.tree.setRoot(node);
		this.treeSaved = false;
	}

	public void setTurn(boolean currentTurn) {
		this.tree.getDynamicTree().setRespondEnabled(currentTurn);
	}

	public LinkedList<DialogueNode> getMostRecentNodes() {
		return this.mostRecent;
	}

	public JPanel getControlPanel() {
		if (PLAYER_TYPE == PlayerType.TEACHER) {
			return new TeachePanel(this.msgHandler);
		} else if (PLAYER_TYPE == PlayerType.STUDENT) {
			return new StudentPanel(this.msgHandler);
		}

		return null;
	}

	public DynamicTree getDynamicTree() {
		return this.tree.getDynamicTree();
	}

	public DialogueTree getDialogueTree() {
		return this.tree;
	}

	public DialogueMap getDialogueMap() {
		return this.map;
	}

	public WaitIndicatorLayer getLayerUI() {
		return this.waitIndicatorLayer;
	}

	public boolean getTreeSaved() {
		return this.treeSaved;
	}
	
	public void showMapSaveDialogue() {
		if (JOptionPane.showConfirmDialog(null, (Object) "Save currrent map?", "Save", JOptionPane.YES_NO_OPTION) == JFileChooser.APPROVE_OPTION) {
			this.saveTree();
		}
	}

	public void saveTree() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(currentDir);
		fileChooser.setSelectedFile(new File("DialogueMap - " + Calendar.getInstance().getTimeInMillis() + ".xml"));
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			if (!fileChooser.getSelectedFile().getPath().endsWith(".xml")) {
				f.renameTo(new File(f.getAbsoluteFile() + ".xml"));
			}
			XmlWriter.WriteTree(fileChooser.getSelectedFile(), this.tree.getRootDialogueNode());
			this.treeSaved = true;
		}
	}

	public DialogueNode readTree(File file) {
		XmlReader reader = new XmlReader(this.msgHandler);
		return reader.ReadTreeRoot(file);
	}
	
	private void clear () {
		this.tree.getDynamicTree().clear();
		this.map.clear();
	}

	public void stopGame() {
		DialogueNode.count = 1;
		this.clear ();
		this.treeSaved = true;
	}

	private void registerReceivingListeners(final MessageHandler msgHandler) {
		msgHandler.registerReceiveMessageListener(MessageTag.EXIT, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				client.disconnect();
				turnNotification.cleanup();
			}
		});

		msgHandler.registerReceiveMessageListener(MessageTag.REJECT, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				Logger.log("Registration rejected: " + msg.data.toString());
				client.disconnect();
			}
		});

		msgHandler.registerReceiveMessageListener(MessageTag.NUMBER_PLAYERS, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				setNumberOfPlayers((int) msg.data);
			}
		});

		msgHandler.registerReceiveMessageListener(MessageTag.START_GAME, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				DialogueNode rootNode = (DialogueNode) msg.data;
				rootNode.msgHandler = msgHandler;

				if (!getTreeSaved()) {
					showMapSaveDialogue ();
				}

				stopGame ();
				setRoot(rootNode);
				addDialogueNode(rootNode);	
			}
		});

		msgHandler.registerReceiveMessageListener(MessageTag.RESPONSE, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				DialogueNode node = (DialogueNode) msg.data;
				node.msgHandler = msgHandler;
				addDialogueNode(node);
			}
		});

		msgHandler.registerReceiveMessageListener(MessageTag.CURRENT_TURN, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				boolean isCurrentTurn = msg.data.toString().equals(playerName);
				setTurn(isCurrentTurn);
				waitIndicatorLayer.update(!isCurrentTurn, msg.data.toString());

				if (isCurrentTurn) {
					turnNotification.play();
				}
			}
		});

		msgHandler.registerReceiveMessageListener(MessageTag.STOP_GAME, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				if (!getTreeSaved()) {
					showMapSaveDialogue ();
				}
				stopGame();
				waitIndicatorLayer.update(false, "");
			}
		});
	}

	private void registerSendingListeners(MessageHandler msgHandler) {
		MessageListener defaultSend = new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				client.sendData(msg);
			}
		};

		msgHandler.registerSendingMessageListener(MessageTag.RESPONSE, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				DialogueNode partialNode = (DialogueNode) msg.data;
				partialNode.playerName = playerName;
				partialNode.avatar = playerAvatar;
				client.sendData(MessageTag.RESPONSE, partialNode);
			}
		});

		msgHandler.registerSendingMessageListener(MessageTag.START_GAME, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				DialogueNode partialNode = (DialogueNode) msg.data;
				partialNode.playerName = playerName;
				partialNode.avatar = playerAvatar;
				client.sendData(MessageTag.START_GAME, partialNode);
			}
		});

		msgHandler.registerSendingMessageListener(MessageTag.SKIP_TURN, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				msg.from = playerName;
				msg.data = playerName;
				client.sendData(msg);
			}
		});

		msgHandler.registerSendingMessageListener(MessageTag.LOAD_TREE, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				if (!getTreeSaved()) {
					showMapSaveDialogue ();
				}
				stopGame ();
				DialogueNode rootNode = readTree((File) msg.data);
				tree.setRoot(rootNode);
				map.loadTree(tree);
			}
		});

		msgHandler.registerSendingMessageListener(MessageTag.SAVE_TREE, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				saveTree();
			}
		});

		msgHandler.registerSendingMessageListener(MessageTag.STOP_GAME, defaultSend);
		msgHandler.registerSendingMessageListener(MessageTag.EXIT, defaultSend);

	}
}
