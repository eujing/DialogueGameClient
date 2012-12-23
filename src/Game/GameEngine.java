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
import GUI.TeacherMenu;
import GUI.WaitIndicatorLayer;
import Networking.Client;
import java.awt.Component;
import java.awt.Image;
import java.util.Collections;
import java.util.LinkedList;
import javax.swing.ImageIcon;

public class GameEngine {
	private static final String WAIT_ANIMATION = "/Resources/ActivityIndicator.gif";
	private static final String TURN_NOTIFICATION = "/Resources/ding.wav";
	private static final String IP_ADDRESS = "127.0.0.1";
	private static final short PORT = 3000;

	public enum PlayerType {STUDENT, TEACHER};
	public static PlayerType PLAYER_TYPE = PlayerType.STUDENT;
	private String playerName;
	private Client client;
	private int nPlayers;
	private DynamicTree tree;
	private WaitIndicatorLayer waitIndicatorLayer;
	private MessageHandler msgHandler;
	private DialogueNode root;
	private LinkedList<DialogueNode> mostRecent;
	private LinkedList<DialogueNode> searchQueue;
	private boolean treeSaved;
	private SoundPlayer turnNotification;

	public GameEngine (String playerName) {
		this.playerName = playerName;
		this.nPlayers = 0;
		this.tree = new DynamicTree (this);
		this.waitIndicatorLayer = createWaitIndicatorLayer ();
		this.msgHandler = new MessageHandler ();
		this.registerReceivingListeners (this.msgHandler);
		this.registerSendingListeners (this.msgHandler);
		this.client = new Client (this.msgHandler, playerName, IP_ADDRESS, PORT);
		this.root = null;
		this.mostRecent = new LinkedList<> ();
		this.searchQueue = new LinkedList<> ();
		this.treeSaved = false;
		this.turnNotification = new SoundPlayer (TURN_NOTIFICATION);
		
		this.client.startListening ();
	}
	
	private WaitIndicatorLayer createWaitIndicatorLayer () {
		Image image = FileReader.getImage (this.getClass ().getResource (WAIT_ANIMATION), 0, 0);
		return new WaitIndicatorLayer (image);
	}
	
	public void addDialogueNode (DialogueNode node) {
		DialogueNode parent = this.getNode (node.parentId);

		if (parent == null) {
			this.tree.setRoot (node);
			this.setRoot (node);
		}
		else {
			Collections.sort (parent.childrenNodes);
			parent.childrenNodes.add (node);
			this.tree.addChild (parent, node);
		}

		if (this.mostRecent.size () >= this.nPlayers) {
			this.mostRecent.pop ();
		}
		this.mostRecent.add (node);

		this.treeSaved = false;
	}

	public void setNumberOfPlayers (int n) {
		this.nPlayers = n;
	}

	public void setRoot (DialogueNode node) {
		this.root = node;
		this.tree.setRoot (node);
		this.treeSaved = false;
	}

	public void setTurn (boolean currentTurn) {
		this.tree.setRespondEnabled (currentTurn);
	}
	
	//Breadth first search based on node id
	public DialogueNode getNode (int id) {
		if (id == 0) {
			return null;
		}

		this.searchQueue.clear ();
		this.searchQueue.add (this.root);

		DialogueNode current;
		while (!this.searchQueue.isEmpty ()) {
			current = this.searchQueue.pop ();

			if (current.id == id) {
				return current;
			}
			else if (!current.childrenNodes.isEmpty ()) {
				for (DialogueNode node : current.childrenNodes) {
					this.searchQueue.add (node);
				}
			}
		}

		return null;
	}

	public LinkedList<DialogueNode> getMostRecentNodes () {
		return this.mostRecent;
	}
	
	public TeacherMenu getTeacherMenu (Component invoker) {
		return new TeacherMenu (this.msgHandler, invoker);
	}

	public DynamicTree getTree () {
		return this.tree;
	}
	
	public WaitIndicatorLayer getLayerUI () {
		return this.waitIndicatorLayer;
	}

	public boolean getTreeSaved () {
		return this.treeSaved;
	}

	public void saveTree () {
		XmlWriter.WriteTree ("tree.xml", this.root);
		this.treeSaved = true;
	}

	public DialogueNode readTree () {
		XmlReader reader = new XmlReader (this.msgHandler);
		return reader.ReadTree ("tree.xml");
	}
	
	public void stopGame () {
		this.tree.clear ();
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
				client.sendData (MessageTag.RESPONSE, partialNode);
			}
		});

		msgHandler.registerSendingMessageListener (MessageTag.START_GAME, defaultSend);
		msgHandler.registerSendingMessageListener (MessageTag.STOP_GAME, defaultSend);
		msgHandler.registerSendingMessageListener (MessageTag.EXIT, defaultSend);
		msgHandler.registerSendingMessageListener (MessageTag.SKIP_TURN, defaultSend);
	}
}
