package yhg;

/**
 * You must define the methods in the interface or abstract class of the Algorithm.
 * Such methods will be called in the "tryHandleRequest()" method of a concrete implementation of the Request interface.
 * In that method the simulation program will call your algorithm to find the best controller and server to handle the request.
 * 
 * The concrete algorithm class can be a Singleton that it has only one instance in the memory.
 */

/**
 * @author Chris and Sonny
 * Please define the parameters of the methods in this interface. You can also change it to a class or abstracted class.
 */

public interface Algorithm {
	public RequestHandlePlan selectServer(Request r) ;
}
