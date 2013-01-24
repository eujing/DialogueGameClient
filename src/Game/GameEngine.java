package Game;

import Core.DialogueNode;
import Core.FileReader;
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
import java.util.LinkedList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GameEngine {

	private static final String WAIT_ANIMATION = "/Resources/ActivityIndicator.gif";
	private static final String TURN_NOTIFICATION = "/Resources/ding.wav";
	private static final String IP_ADDRESS = "127.0.0.1";
	private static final short PORT = 3000;

	public enum PlayerType {

		STUDENT, TEACHER
	};
	public static PlayerType PLAYER_TYPE = PlayerType.STUDENT;
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

	public GameEngine (String playerName, ImageIcon playerAvatar) {
		this.playerName = playerName;
		this.playerAvatar = playerAvatar;
		this.nPlayers = 0;
		this.tree = new DialogueTree ();
		this.map = new DialogueMap (this.tree);
		this.waitIndicatorLayer = createWaitIndicatorLayer ();
		this.msgHandler = new MessageHandler ();
		this.registerReceivingListeners (this.msgHandler);
		this.registerSendingListeners (this.msgHandler);
		this.client = new Client (this.msgHandler, playerName, IP_ADDRESS, PORT);
		this.mostRecent = new LinkedList<> ();
		this.treeSaved = false;
		this.turnNotification = new SoundPlayer (TURN_NOTIFICATION);

		this.client.startListening ();
	}

	private WaitIndicatorLayer createWaitIndicatorLayer () {
		Image image = FileReader.getImage (this.getClass ().getResource (WAIT_ANIMATION), 0, 0);
		return new WaitIndicatorLayer (image);
	}

	public void addDialogueNode (DialogueNode node) {
		this.tree.addDialogueNode (node);

		if (this.mostRecent.size () >= this.nPlayers) {
			node.setIsMostRecent (false);
			this.mostRecent.pop ();
		}

		node.setIsMostRecent (true);
		this.mostRecent.add (node);

		this.treeSaved = false;
	}

	public void setNumberOfPlayers (int n) {
		this.nPlayers = n;
	}

	public void setRoot (DialogueNode node) {
		this.tree.setRoot (node);
		this.treeSaved = false;
	}

	public void setTurn (boolean currentTurn) {
		this.tree.getDynamicTree ().setRespondEnabled (currentTurn);
	}

	public LinkedList<DialogueNode> getMostRecentNodes () {
		return this.mostRecent;
	}

	public JPanel getControlPanel () {
		if (PLAYER_TYPE == PlayerType.TEACHER) {
			return new TeachePanel (this.msgHandler);
		}
		else if (PLAYER_TYPE == PlayerType.STUDENT) {
			return new StudentPanel (this.msgHandler);
		}

		return null;
	}

	public DynamicTree getDynamicTree () {
		return this.tree.getDynamicTree ();
	}

	public DialogueTree getDialogueTree () {
		return this.tree;
	}

	public WaitIndicatorLayer getLayerUI () {
		return this.waitIndicatorLayer;
	}

	public boolean getTreeSaved () {
		return this.treeSaved;
	}

	public void saveTree () {
		XmlWriter.WriteTree ("tree.xml", this.tree.getRootDialogueNode ());
		this.treeSaved = true;
	}

	public DialogueNode readTree (File file) {
		XmlReader reader = new XmlReader (this.msgHandler);
		return reader.ReadTreeRoot (file);
	}

	public void stopGame () {
		this.tree.getDynamicTree ().clear ();
		this.map.clear ();
		this.saveTree ();
	}

	private void registerReceivingListeners (final MessageHandler msgHandler) {
		msgHandler.registerReceiveMessageListener (MessageTag.EXIT, new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				client.disconnect ();
				turnNotification.cleanup ();
			}
		});

		msgHandler.registerReceiveMessageListener (MessageTag.REJECT, new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				Logger.log ("Registration rejected: " + msg.data.toString ());
				client.disconnect ();
			}
		});

		msgHandler.registerReceiveMessageListener (MessageTag.NUMBER_PLAYERS, new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				setNumberOfPlayers ((int) msg.data);
			}
		});

		msgHandler.registerReceiveMessageListener (MessageTag.START_GAME, new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				DialogueNode rootNode = (DialogueNode) msg.data;
				rootNode.msgHandler = msgHandler;
				setRoot (rootNode);
				addDialogueNode (rootNode);
				DialogueNode.count++;
			}
		});

		msgHandler.registerReceiveMessageListener (MessageTag.RESPONSE, new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				DialogueNode node = (DialogueNode) msg.data;
				node.msgHandler = msgHandler;
				addDialogueNode (node);
				DialogueNode.count++;
			}
		});

		msgHandler.registerReceiveMessageListener (MessageTag.CURRENT_TURN, new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				boolean isCurrentTurn = msg.data.toString ().equals (playerName);
				setTurn (isCurrentTurn);
				waitIndicatorLayer.update (!isCurrentTurn, msg.data.toString ());

				if (isCurrentTurn) {
					turnNotification.play ();
				}
			}
		});

		msgHandler.registerReceiveMessageListener (MessageTag.STOP_GAME, new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				stopGame ();
				waitIndicatorLayer.update (false, "");
			}
		});
	}

	private void registerSendingListeners (MessageHandler msgHandler) {
		MessageListener defaultSend = new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				client.sendData (msg);
			}
		};

		msgHandler.registerSendingMessageListener (MessageTag.RESPONSE, new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				DialogueNode partialNode = (DialogueNode) msg.data;
				partialNode.playerName = playerName;
				partialNode.avatar = playerAvatar;
				client.sendData (MessageTag.RESPONSE, partialNode);
			}
		});

		msgHandler.registerSendingMessageListener (MessageTag.START_GAME, new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				DialogueNode partialNode = (DialogueNode) msg.data;
				partialNode.playerName = playerName;
				partialNode.avatar = playerAvatar;
				client.sendData (MessageTag.RESPONSE, partialNode);
			}
		});

		msgHandler.registerSendingMessageListener (MessageTag.SKIP_TURN, new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				msg.from = playerName;
				msg.data = playerName;
				client.sendData (msg);
			}
		});

		msgHandler.registerSendingMessageListener (MessageTag.LOAD_TREE, new MessageListener () {
			@Override
			public void messageReceived (Message msg) {
				tree.setRoot (readTree ((File) msg.data));
				//update map
			}
		});

		msgHandler.registerSendingMessageListener (MessageTag.STOP_GAME, defaultSend);
		msgHandler.registerSendingMessageListener (MessageTag.EXIT, defaultSend);

	}
}
