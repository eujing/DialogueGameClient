package Networking;

import Core.DialogueNode;
import Core.Logger;
import Core.Message;
import Core.MessageHandler;
import Core.MessageListener;
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

	public Client (final String name, String ipAddress, short port) {
		try {
			this.name = name;
			Logger.log ("Connecting to server...");
			this.clientSocket = new Socket (ipAddress, port);
			this.outToServer = new ObjectOutputStream (this.clientSocket.getOutputStream ());
			this.outToServer.flush ();
			this.inFromServer = new ObjectInputStream (this.clientSocket.getInputStream ());
			this.msgHandler = new MessageHandler ();
			this.gEngine = new GameEngine (this.msgHandler);
			

			//Send info here
			Logger.logDebug ("Sending info to server");
			sendData ("info", name);
			Logger.logDebug ("Info sent!");

			this.msgHandler.registerMessageListener ("exit", new MessageListener () {
				@Override
				public void messageReceived (Message msg) {
					disconnect ();
				}
			});

			this.msgHandler.registerMessageListener ("response", new MessageListener () {
				@Override
				public void messageReceived (Message msg) {
					DialogueNode node = (DialogueNode) msg.data;
					node.msgHandler = msgHandler;
					gEngine.addDialogueNode (node);
				}
			});
			
			this.msgHandler.registerMessageListener ("sendResponse", new MessageListener () {
				@Override
				public void messageReceived (Message msg) {
					DialogueNode partialNode = (DialogueNode) msg.data;
					partialNode.playerName = name;
					sendData ("response", partialNode);
				}
			});

			this.listenThread = createListenThread ();

		}
		catch (Exception ex) {
			Logger.logDebug ("Client: " + ex.getMessage ());
		}
	}

	private Thread createListenThread () {
		return new Thread (new Runnable () {
			private Message msg;

			@Override
			public void run () {
				try {
					Logger.logDebug ("Listening...");
					while (true) {
						msg = Message.cast (inFromServer.readObject ());

						//Handle message
						if (msg != null) {
							msgHandler.submitMessage (msg);
						}
						else {
							Logger.logDebug ("Invalid message from server");
						}
					}
				}
				catch (IOException | ClassNotFoundException ex) {
					Logger.logDebug ("Disconnecting");
				}
				finally {
					disconnect ();
				}
			}
		});
	}

	public void startListening () {
		if (this.listenThread != null) {
			this.listenThread.start ();
		}
	}

	public final void disconnect () {

		try {
			this.clientSocket.close ();
			this.inFromServer.close ();
			this.outToServer.close ();
		}
		catch (Exception ex) {
			Logger.logDebug ("disconnect: " + ex.getMessage ());
		}
		finally {
			System.exit (0);
		}
	}

	public final void sendData (String type, Object data) {
		try {
			this.outToServer.writeObject (new Message (type, this.name, data));
			this.outToServer.flush ();
		}
		catch (Exception ex) {
			Logger.logDebug (ex.getMessage ());
			ex.printStackTrace ();
		}
	}
}
