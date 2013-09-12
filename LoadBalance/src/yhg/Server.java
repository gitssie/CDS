package yhg;

/** The objects will be created by the Server Factory. 
 * @author Kenny
 *
 */
public class Server extends Node {
	private int capacity;
	private Controller controller;
	private int load = 0;

	public Server(String id, int index, int x, int y, int capacity, Controller c) {
		super(id, index, x, y);
		this.capacity = capacity;
		this.controller = c;
		c.addServer(this);
	}

	/** Get the max load this server can handle */
	public int getCapacity() {
		return capacity;
	}
	
	/** Get default controller */
	public Controller getController() {
		return controller;
	}

	/** Test whether the request is safe to this server */
	boolean testSafe(Request r) {
		return ((r.getLoad() + load) <= capacity);
	}
	
	@Override
	public void handleRequest(Request r) {
		load += r.getLoad();
		requests.add(r);
		controller.handleRequest(r);
	}
	
	@Override
	public void finishRequest(Request r) {
		load -= r.getLoad();
		requests.remove(r);
		controller.finishRequest(r);
	}

	@Override
	NodeType getType() {
		return NodeType.Server;
	}

	@Override
	int getDistance(Controller c) {
		return ServerFactory.getInstance().distSC[index][c.getIndex()];
	}

	@Override
	int getLoad() {
		return load;
	}
}
