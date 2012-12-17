package Networking;

import Core.DialogueNode;
import Core.Logger;
import Core.Message;
import Core.MessageHandler;
import Core.MessageListener;
import Core.MessageTag;
import Game.GameEngine;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

	private static final boolean DEBUG = true;
	public String name;
	private Socket clientSocket;
	private Thread listenThread;
	private ObjectInputStream inFromServer;
	private ObjectOutputStream outToServer;
	private MessageHandler msgHandler;
	public GameEngine gEngine;

	public Client(final String name, String ipAddress, short port) {
		try {
			this.name = name;
			Logger.log("Connecting to server...");
			this.clientSocket = new Socket(ipAddress, port);
			this.outToServer = new ObjectOutputStream(this.clientSocket.getOutputStream());
			this.outToServer.flush();
			this.inFromServer = new ObjectInputStream(this.clientSocket.getInputStream());
			this.msgHandler = new MessageHandler();
			this.gEngine = new GameEngine(this.msgHandler);

			//Send info here
			Logger.logDebug("Sending info to server");
			sendData(MessageTag.INFO, name);
			Logger.logDebug("Info sent!");

			registerReceivingListeners(this.msgHandler);
			registerSendingListeners(this.msgHandler);

			this.listenThread = createListenThread();

		} catch (Exception ex) {
			Logger.logDebug("Client: " + ex.getMessage());
			System.exit(0);
		}
	}

	private void registerReceivingListeners(final MessageHandler msgHandler) {
		msgHandler.registerReceiveMessageListener(MessageTag.EXIT, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				disconnect();
			}
		});
		
		msgHandler.registerReceiveMessageListener(MessageTag.REJECT, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				Logger.log("Registration rejected: " + msg.data.toString());
				disconnect ();
			}
		});
		
		msgHandler.registerReceiveMessageListener(MessageTag.START_GAME, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				DialogueNode rootNode = (DialogueNode) msg.data;
				rootNode.msgHandler = msgHandler;
				gEngine.setRoot (rootNode);
				gEngine.addDialogueNode(rootNode);
			}
		});

		msgHandler.registerReceiveMessageListener(MessageTag.RESPONSE, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				DialogueNode node = (DialogueNode) msg.data;
				node.msgHandler = msgHandler;
				gEngine.addDialogueNode(node);
			}
		});
	}

	private void registerSendingListeners(MessageHandler msgHandler) {
		MessageListener defaultSend = new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				sendData(msg);
			}
		};

		msgHandler.registerSendingMessageListener(MessageTag.RESPONSE, new MessageListener() {
			@Override
			public void messageReceived(Message msg) {
				DialogueNode partialNode = (DialogueNode) msg.data;
				partialNode.playerName = name;
				sendData(MessageTag.RESPONSE, partialNode);
			}
		});

		msgHandler.registerSendingMessageListener(MessageTag.START_GAME, defaultSend);
		msgHandler.registerSendingMessageListener(MessageTag.EXIT, defaultSend);
		msgHandler.registerSendingMessageListener(MessageTag.SKIP_TURN, defaultSend);
	}

	private Thread createListenThread() {
		return new Thread(new Runnable() {
			private Message msg;

			@Override
			public void run() {
				try {
					Logger.logDebug("Listening...");
					while (true) {
						msg = Message.cast(inFromServer.readObject());

						//Handle message
						if (msg != null) {
							msgHandler.submitReceivedMessage(msg);
						} else {
							Logger.logDebug("Invalid message from server");
						}
					}
				} catch (IOException | ClassNotFoundException ex) {
					Logger.logDebug("Disconnecting");
				} finally {
					disconnect();
				}
			}
		});
	}

	public MessageHandler getMessageHandler() {
		return this.msgHandler;
	}

	public void startListening() {
		if (this.listenThread != null) {
			this.listenThread.start();
		}
	}

	public final void disconnect() {

		try {
			this.clientSocket.close();
			this.inFromServer.close();
			this.outToServer.close();
		} catch (Exception ex) {
			Logger.logDebug("disconnect: " + ex.getMessage());
		} finally {
			System.exit(0);
		}
	}

	public final void sendData(Message msg) {
		try {
			this.outToServer.writeObject(msg);
			this.outToServer.flush();
		} catch (Exception ex) {
			Logger.logDebug(ex.getMessage());
			ex.printStackTrace();
		}
	}

	public final void sendData(MessageTag tag, Object data) {
		this.sendData(new Message(tag, this.name, data));
	}
}
