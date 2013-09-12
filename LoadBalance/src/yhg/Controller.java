package yhg;

import java.util.Vector;

public class Controller extends Node {
	public Controller(String id, int index, int x, int y) {
		super(id, index, x, y);
	}

	Controller[] backups;
	private Vector<Server> servers = new Vector<Server>();

	/** Get default backup controller and actual backup controller */
	public Controller getDefaultBackupController1() {
		return backups[0];
	}
	
	public Controller getDefaultBackupController2() {
		if (backups.length > 1)
			return backups[1];
		else
			return null;
	}
	
	public int countBackups() {
		return backups.length;
	}
	
	/** Get all servers attached to this controller. */
	Vector<Server> getAllServers() {
		return servers;
	}

	@Override
	NodeType getType() {
		return NodeType.Controller;
	}

	@Override
	int getDistance(Controller c) {
		return ServerFactory.getInstance().distCC[index][c.getIndex()];
	}

	@Override
	int getLoad() {
		int load = 0;
		for (Server s: servers)
			load += s.getLoad();
		return load;
	}
	
	
	@Override
	public void handleRequest(Request r) {
		requests.add(r);
	}

	@Override
	public void finishRequest(Request r) {
		requests.remove(r);
	}
	
	public void addServer(Server s) {
		servers.add(s);
	}
}
