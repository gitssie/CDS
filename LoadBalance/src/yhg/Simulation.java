package yhg;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;

public class Simulation {
	private static Algorithm algorithm = null;
	
	private static Hashtable<Controller, Queue<Request>> waitingQueues = new Hashtable<Controller, Queue<Request>>(); // The queue wait when the servers are busy
	private static Vector<Request> runningQueue = new Vector<Request>();
	private static long totalWaitTime = 0;
	private static long totalDecisionTime = 0;
	private static int timeStamp = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Syntax: Java yhg.Simulation {1|2} < {Input filename}");
			System.exit(0);
		}
		if (args[0].equals("1")) 
			algorithm = new Algorithm1();
		else 
			algorithm = new Algorithm2();
		
		simulate();
	}
	
	
	private static void simulate() {
		Scanner sc = null;
		
		try {
			int CONTROLLER_COUNT = 0;
			int SERVER_PER_CONTROLLER = 0;
			int REQUEST_COUNT = 0;
			
			sc = new Scanner(System.in);
			String line;
			
			// Parse the three parameters
			for (int i = 0; i < 3; i++) {
				line = sc.nextLine();
				while (line.matches("\\s*") || line.matches("\\s*#.*")) { // Blank row or comments
					line = sc.nextLine();
				}
				String[] tmp = line.split("\\t");
				if (tmp[0].equals("CONTROLLER_COUNT"))
					CONTROLLER_COUNT = Integer.parseInt(tmp[1]);
				else if (tmp[0].equals("SERVER_PER_CONTROLLER"))
					SERVER_PER_CONTROLLER = Integer.parseInt(tmp[1]);
				else if (tmp[0].equals("REQUEST_COUNT"))
					REQUEST_COUNT = Integer.parseInt(tmp[1]);
			}
			
			String[] servers = new String[CONTROLLER_COUNT + CONTROLLER_COUNT * SERVER_PER_CONTROLLER];
			// Prepare for the controllers
			for (int i = 0; i < CONTROLLER_COUNT; i++) {
				line = sc.nextLine();
				while (line.matches("\\s*") || line.matches("\\s*#.*")) { // Blank row or comments
					line = sc.nextLine();
				}
				servers[i] = line;
			}
			
			// Prepare for the servers
			for (int i = 0; i < CONTROLLER_COUNT * SERVER_PER_CONTROLLER; i++) {
				line = sc.nextLine();
				while (line.matches("\\s*") || line.matches("\\s*#.*")) { // Blank row or comments
					line = sc.nextLine();
				}
				servers[i + CONTROLLER_COUNT] = line;
			}
			
			ServerFactory.getInstance().buildServers(CONTROLLER_COUNT, SERVER_PER_CONTROLLER, servers);
			
			// Handle the requests
			for (int i = 0; i < REQUEST_COUNT; i++) {
				line = sc.nextLine();
				while (line.matches("\\s*") || line.matches("\\s*#.*")) { // Blank row or comments
					line = sc.nextLine();
				}
				
				// Handle requests.
				Request r = parseRequest(line);
				while (timeStamp < r.getArrivingTime()) {
					runningServersForOneTimeSlot();
					timeStamp++;
				}
				handleARequest(r);
			}
			while (! runningQueue.isEmpty()) {
				runningServersForOneTimeSlot();
				timeStamp++;
			}
			
			System.out.println("Total decision time in nano time: " + totalDecisionTime);
			System.out.println("Total waiting time for free servers: " + totalWaitTime);
			System.out.println("Complete timestamp at: " + timeStamp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}

	private static Request parseRequest(String line) {
		String tmp[] = line.split("\\t");
		String id = "Request" + tmp[0];
		int x = Integer.parseInt(tmp[1]);
		int y = Integer.parseInt(tmp[2]);
		int controllerIndex = Integer.parseInt(tmp[3]) - 1;
		int loadOfTask = Integer.parseInt(tmp[4]);
		int timeForTask = Integer.parseInt(tmp[5]);
		int timestamp = Integer.parseInt(tmp[6]);
		Request r = new Request(id, x, y, loadOfTask, timeForTask, timestamp, ServerFactory.getInstance().getControllers()[controllerIndex]);
		return r;
	}
	
	private static void runningServersForOneTimeSlot() {
		// All the waited requests need to add one time slot for each.
		for (Queue<Request> v : waitingQueues.values())
			totalWaitTime += v.size();
		
		Vector<Request> removingFromRunning = new Vector<Request>();
		Vector<Request> addToRunning = new Vector<Request>();
		// Running
		for (Request r: runningQueue) {
			if (! r.runASlot()) {
				// Request r finished
				Server s = r.getRunningServer();
				StringBuffer sb = new StringBuffer("Request " ).append(r.getID()).append(" has been finished by server ")
						.append(s.getID()).append(" at timestamp ").append(timeStamp).append(", now server load is ").append(s.getLoad());
				System.out.println(sb.toString());
				removingFromRunning.add(r);
				// Once a request is finished, get one request from the waiting list which belongs to the same controller of the finished request.
				Queue<Request> v = waitingQueues.get(r.getDefaultController());
				if ((v != null) && (! v.isEmpty())) {
					Request r1 = v.peek();
					if (s.testSafe(r1)) {
						addToRunning.add(r1);
						v.remove(r1);
						r1.setRunningServer(s);
						sb = new StringBuffer("Request " ).append(r1.getID()).append(" is handled by server ")
								.append(s.getID()).append(" at timestamp ").append(timeStamp).append(", request load is: ")
								.append(r1.getLoad()).append(", Server load is ").append(s.getLoad());
						System.out.println(sb.toString());
					}
				}
			}
		}
		for (Request r: removingFromRunning) {
			runningQueue.remove(r);
		}

		for (Request r: addToRunning) {
			runningQueue.add(r);
		}
	}

	
	private static void handleARequest(Request r) {
		RequestHandlePlan rhp = algorithm.selectServer(r);
		// Calculate the decision time
		totalDecisionTime += rhp.getTimeUsed();
		if (rhp.getServer() == null) { // All servers are overloaded, put it into the task wait queue.
			Queue<Request> v = waitingQueues.get(r.getDefaultController());
			if (v == null) {
				v = new LinkedList<Request>();
				waitingQueues.put(r.getDefaultController(), v);
			}
			v.add(r);
			StringBuffer sb = new StringBuffer("All the servers are busy. Request " ).append(r.getID())
					.append(" is waiting at timestamp ").append(timeStamp).append(", request load is: ")
					.append(r.getLoad()).append(", coordinator load is ").append(r.getDefaultController().getLoad());
			System.out.println(sb.toString());
		} else { // Can run on some server, confirm with that server again
			Server s = rhp.getServer();
			if (s.testSafe(r)) {
				runningQueue.add(r);
				r.setRunningServer(s);
				StringBuffer sb = new StringBuffer("Request " ).append(r.getID()).append(" is handled by server ")
						.append(s.getID()).append(" at timestamp ").append(timeStamp).append(", request load is: ")
						.append(r.getLoad()).append(", Server load is ").append(s.getLoad());
				System.out.println(sb.toString());
			} else {
				// System.out.println("!!!!!!! Should not happen !!!!!!!!!!");
				Queue<Request> v = waitingQueues.get(r.getDefaultController());
				if (v == null) {
					v = new LinkedList<Request>();
					waitingQueues.put(r.getDefaultController(), v);
				}
				v.add(r);
				StringBuffer sb = new StringBuffer("All the servers are busy. Request " ).append(r.getID())
						.append(" is waiting at timestamp ").append(timeStamp).append(", request load is: ")
						.append(r.getLoad()).append(", coordinator load is ").append(r.getDefaultController().getLoad());
				System.out.println(sb.toString());
			}
		}
	}
}
