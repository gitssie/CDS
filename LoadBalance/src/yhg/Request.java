package yhg;

/**
 * The concrete request class will implement this interface and the Observer class (the request need to observe
 * the running status of a server in case it is down). 
 * And the objects will be created by the RequestsFactory. 
 * @author Kenny
 *
 */
public class Request {
	private String id;
	private int x, y;
	private int load;
	private int runningTime;
	private int waitingTime1;
	private int waitingTime2;
	private int remainTime;
	private int distRC[];
	private int distRS[];
	private Controller controller;
	private Controller runningController;
	private Server runningServer = null;
	private RunningState rs;
	private int arrivingTime;
	
	public RunningState getRs() {
		return rs;
	}

	public void setRs(RunningState rs) {
		this.rs = rs;
	}

	enum RunningState {
		WAIT,
		RUNNING,
		FINNISHED
	}
	
	public Request(String id, int x, int y, int load, int runningTime, int timestamp, Controller controller) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.load = load;
		this.runningTime = runningTime;
		this.arrivingTime = timestamp;
		this.controller = controller;
		this.runningController = controller;
		this.remainTime = runningTime;
		rs = RunningState.WAIT;
	}
	
	public void setRunningController(Controller c) {
		this.runningController = c;
	}
	
	public void setRunningServer(Server s) {
		this.runningServer = s;
		s.handleRequest(this);
	}
	
	/** Get the ID of the Request*/
	public String getID() {
		return id;
	}
	
	/** get the position of this Request */
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	
	/** Get the running time and average load that this Request needs to complete */
	public int getRunningTime() {
		return runningTime;
	}	// A fixed value for a request object
	public int getLoad() {
		return load;
	}			// A fixed value for a request object
 	// A run-time value for a request object
	public int getRemainTime() {
		return remainTime;
	}
	
	/** Get the time for a new server free from busy for this task */
	public int getWaitingTime() {
		return waitingTime2;
	}
	
	/** Get the time for the CDS or controller to decide which server to be used for this task. */
	public int getDecisionTime() {
		return waitingTime1;
	}
	
	/** Get the distance to a controller */
	public int getDistance(Controller c) {
		return distRC[c.getIndex()];
	}
	
	/** Get the distance to a server */
	public int getDistance(Server s) {
		return distRS[s.getIndex()];
	}
	
	/** Get default Controller */
	public Controller getDefaultController() {
		return controller;
	}
	
	/** Get the controller that this request is handled by */
	public Controller getRunningController() {
		return runningController;
	}
	
	/** Run the algorithm to find the best controller, server, and make out the time used for selecting the controller and server. */
	public RequestHandlePlan tryHandleRequest(Algorithm a) {
		return a.selectServer(this);
	}
	
	/** Get the server that this request is run on */
	public Server getRunningServer() {
		return runningServer;
	}
	
	/** Get the time stamp that this request comes */
	public int getArrivingTime() {
		return arrivingTime;
	}
	
	public boolean runASlot() {
		remainTime--;
		if (remainTime > 0) {
			rs = RunningState.RUNNING;
			return true;
		} else {
			rs = RunningState.FINNISHED;
			runningServer.finishRequest(this);
			return false;
		}
	}
	
}
