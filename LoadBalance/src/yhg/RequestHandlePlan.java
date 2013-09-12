package yhg;

/** How to handle the current task */
public final class RequestHandlePlan {
	private Controller c;
	private Server s;
	private Request r;
	private long time_used; // The time used for making out this request handle plan. It is calculated by the algorithm.
	
	public RequestHandlePlan(Controller controller, Server server, Request request, long t) {
		this.c = controller;
		this.s = server;
		this.r = request;
		this.time_used = t;
	}

	/**
	 * @return the controller
	 */
	public final Controller getController() {
		return c;
	}

	/**
	 * @return the server
	 */
	public final Server getServer() {
		return s;
	}

	/**
	 * @return the request
	 */
	public final Request getRequest() {
		return r;
	}

	/**
	 * @return the time used for making out this plan
	 */
	public final long getTimeUsed() {
		return time_used;
	}
}
