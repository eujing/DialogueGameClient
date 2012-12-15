package Networking;

import Game.GameEngine;
import Core.DialogueNode;
import Core.Message;
import Core.ResponseHandler.Response;
import java.util.HashMap;

public class ClientMessageHandler {

	private Client client;
	private IUpdatable updatable;
	private GameEngine gEngine;
	public HashMap<String, Func> response;

	public ClientMessageHandler (Client client, IUpdatable updatable, GameEngine gEngine) {
		this.response = new HashMap<> ();
		this.client = client;
		this.updatable = updatable;
		this.gEngine = gEngine;
		registerResponses ();
	}

	public interface Func {

		public void execute (Message message);
	}

	private void registerResponses () {
		response.put ("exit", new Func () {
			@Override
			public void execute (Message message) {
				client.disconnect ();
			}
		});
		
		response.put ("response", new Func () {
			@Override
			public void execute (Message message) {
				DialogueNode node = (DialogueNode) message.data;
				DialogueNode parentNode = gEngine.getNode(node.parentId);
				parentNode.childrenNodes.add(node);
			}
		});
	}
}
