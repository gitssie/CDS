package yhg;

import java.util.Vector;

public abstract class Node {
	enum NodeType {
		Controller,
		Server
	}
	
	protected String id;
	protected int index;
	protected int x;
	protected int y;
	protected Vector<Request> requests = new Vector<Request>();
	
	protected Node(String id, int index, int x, int y) {
		this.id = id;
		this.index = index;
		this.x = x;
		this.y = y;
	}
	
	/** Get the ID of the node*/
	String getID() {
		return id;
	}
	
	/** The index in the distance matrix */
	int getIndex() {
		return index;
	}
	
	/** The type of the node */
	abstract NodeType getType(); 
	
	/** get the position of this node */
	int getX() {
		return x;
	}
	int getY() {
		return y;
	}
	
	/** Get all the requests current controller is handling */
	public Vector<Request> getHandlingRequests() {
		return requests;
	}

	/** Get the distance to a controller */
	abstract int getDistance(Controller c);
	
	/** Get the load. For server, it's the load it takes; for controller, it's the sum of load on all servers. */
	abstract int getLoad();
	
	abstract void handleRequest(Request r);

	abstract void finishRequest(Request r);
}
