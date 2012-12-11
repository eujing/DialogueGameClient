package Networking;


import Core.Message;
import Game.GameEngine;
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
	private ClientMessageHandler msgHandler;
	private GameEngine gEngine;

	public Client (final String name, String ipAddress, short port, IUpdatable updatable) {
		try {
			this.name = name;
			System.out.println ("Connecting to server...");
			this.clientSocket = new Socket (ipAddress, port);
			this.outToServer = new ObjectOutputStream (this.clientSocket.getOutputStream ());
			this.outToServer.flush();
			this.inFromServer = new ObjectInputStream (this.clientSocket.getInputStream ());
			this.gEngine = new GameEngine ();
			this.msgHandler = new ClientMessageHandler (this, updatable, this.gEngine);

			//Send info here
			logDEBUG ("Sending info to server");
			sendData ("info", name);
			logDEBUG ("Info sent!");

			this.listenThread = new Thread (new Runnable () {
				private Message msg;

				@Override
				public void run () {
					try {
						System.out.println ("Listening...");
						while (true) {
							msg = Message.cast(inFromServer.readObject());

							//Handle message
							if (msg != null) {
								msgHandler.response.get (msg.tag).execute (msg);
							}
							else {
								System.out.println ("Invalid message from server");
							}
						}
					}
					catch (Exception ex) {
						logDEBUG ("Disconnecting");
					}
					finally {
						disconnect ();
					}
				}
			});

		}
		catch (Exception ex) {
			System.out.println ("Client: " + ex.getMessage ());
		}
	}

	public void startListening () {
		this.listenThread.start ();
	}

	public final void disconnect () {

		try {
			this.clientSocket.close ();
			this.inFromServer.close ();
			this.outToServer.close ();
		}
		catch (Exception ex) {
			System.out.println ("disconnect: " + ex.getMessage ());
		}
		finally {
			System.exit (0);
		}
	}

	public final void sendData (String type, Object data) {
		try {
			this.outToServer.writeObject(new Message (type, this.name, data));
			this.outToServer.flush ();
		}
		catch (Exception ex) {
			System.out.println (ex.getMessage ());
		}
	}

	private void logDEBUG (String msg) {
		if (DEBUG) {
			System.out.println (msg);
		}
	}
}
