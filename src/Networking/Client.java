package Networking;

import Core.Logger;
import Core.Message;
import Core.MessageHandler;
import Core.MessageTag;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
	private String playerName;
	private Socket clientSocket;
	private Thread listenThread;
	private ObjectInputStream inFromServer;
	private ObjectOutputStream outToServer;
	private MessageHandler msgHandler;

	public Client (MessageHandler msgHandler, String name, String ipAddress, short port) {
		try {
			Logger.log ("Connecting to server...");
			this.playerName = name;
			this.clientSocket = new Socket (ipAddress, port);
			this.outToServer = new ObjectOutputStream (this.clientSocket.getOutputStream ());
			this.outToServer.flush ();
			this.inFromServer = new ObjectInputStream (this.clientSocket.getInputStream ());
			this.msgHandler = msgHandler;

			//Send info here
			Logger.logDebug ("Sending info to server");
			sendData (MessageTag.INFO, name);
			Logger.logDebug ("Info sent!");

			this.listenThread = createListenThread ();

		}
		catch (Exception ex) {
			Logger.logException ("Client::Client", ex);
			System.exit (0);
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
							msgHandler.submitReceivedMessage (msg);
						}
						else {
							Logger.logDebug ("Invalid message from server");
						}
					}
				}
				catch (Exception ex) {
					Logger.log ("Disconnecting");
					Logger.logException ("Client::createListenThread", ex);
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
			Logger.logException ("Client::disconnect", ex);
		}
		finally {
			System.exit (0);
		}
	}

	public final void sendData (Message msg) {
		try {
			this.outToServer.writeObject (msg);
			this.outToServer.flush ();
		}
		catch (Exception ex) {
			Logger.logException ("Client::sendData", ex);
		}
	}

	public final void sendData (MessageTag tag, Object data) {
		this.sendData (new Message (tag, this.playerName, data));
	}
}
