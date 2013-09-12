package yhg;

import java.util.Hashtable;
import java.util.Vector;

/** This is a singleton class to handle all the servers and controllers, creation, container. */

public class ServerFactory {
	
	// All servers and controllers
	private Server[] servers;
	private Controller[] controllers;
	
	// Initial architecture of the servers and controllers, in a map. The mapping in individual object are dynamic status.
	Hashtable<Controller, Vector<Server>> initialArch = new Hashtable<Controller, Vector<Server>>();
	
	// Server distance matrixes
	int distSC[][];	// distances between servers and controllers, first index is server index, second is controller index
	int distCC[][]; // distances between controllers and controllers
	
	static private ServerFactory instance = null;

	static public synchronized ServerFactory getInstance() {
		if (instance == null)
			instance = new ServerFactory();
		
		return instance;
	}
	
	private ServerFactory () {
	}
	
	// Builder to create the servers from the configuration file
	public void buildServers(int controllerCount, int serverPerController, String[] line) {
		// Initialize the controllers, servers, and distance matrix
		controllers = new Controller[controllerCount];
		servers = new Server[serverPerController * controllerCount];
		distCC = new int[controllerCount][controllerCount];
		distSC = new int[serverPerController * controllerCount][controllerCount];
		
		// Create the controllers
		for (int i = 0; i < controllerCount; i++) {
			String[] tmp = line[i].split("\\t");
			String id = "Controller" + tmp[0];
			int x = Integer.parseInt(tmp[1]);
			int y = Integer.parseInt(tmp[2]);
			controllers[i] = new Controller(id, i, x, y);
		}
		// Set the backup relationship and the distance among controllers
		for (int i = 0; i < controllerCount; i++) {
			String[] tmp = line[i].split("\\t");
			int backup1 = Integer.parseInt(tmp[3]);
			int backup2 = Integer.parseInt(tmp[4]);
			if (backup2 > 0) {
				controllers[i].backups = new Controller[2];
				controllers[i].backups[0] = controllers[backup1 - 1];
				controllers[i].backups[1] = controllers[backup2 - 1];
			} else {
				controllers[i].backups = new Controller[1];
				controllers[i].backups[0] = controllers[backup1 - 1];
			}
			for (int j = 0; j < controllerCount; j++) {
				distCC[i][j] = Integer.parseInt(tmp[j + 5]);
			}
		}
		
		// Create servers
		for (int i = 0; i < controllerCount * serverPerController; i++) {
			String[] tmp = line[i + controllerCount].split("\\t");
			String id = "Server" + tmp[0];
			int x = Integer.parseInt(tmp[1]);
			int y = Integer.parseInt(tmp[2]);
			int capacity = Integer.parseInt(tmp[3]);
			int controllerIndex = Integer.parseInt(tmp[4]) - 1;
			servers[i] = new Server(id, i, x, y, capacity, controllers[controllerIndex]);
			
			for (int j = 0; j < controllerCount; j++) {
				distSC[i][j] = Integer.parseInt(tmp[j + 5]);
			}
		}
	}
	
	Server[] getServers() {
		return servers;
	}
	
	
	Controller[] getControllers() {
		return controllers;
	}
	
}
