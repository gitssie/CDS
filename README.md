CDS
===
Code
There are many classes and interfaces defined in our project; this section will briefly describe 
each of them. To see how they have been implemented, please see our source code in the 
appendix.

The Controller in our architecture is the manager for an individual cluster of servers, it is in 
charge of many servers, and keeps track of their loads to communicate with the CDS.
The Server is there to run and execute the job requests that are submitted to the system. The 
controller manages them, and they have certain load limits that they can handle.
The Node is the parent class of both the controller and the server, and it contains all the shared 
methods of the controller and server. It is the parent class because the server and controller are 
just nodes in the architecture that just have different jobs.
The Server Factory is used for parsing and managing all the servers in the system. It also has the 
controller information and is a container of servers and controllers. This is the class that 
maintains the distances between each server or controller in our architecture.
The Request class represents all the requests from the clients. Each request has some basic 
information, like load amount and location.
The Request Handler Plan is a class that is used for the output of the algorithm. It’s a type of data 
structure, as it contains some key information needed from the load-balancing algorithms. It 
contains which server the algorithm chose, the controller the algorithm chose, and the time the 
algorithm took to pick the server and controller. Whenever there is a request, we create a request 14
handle plan. The chosen server and controller in request handler plan are going to be the ones 
that will be executing the newly arrived request.
The algorithm interface gets the requests and does calculations to find a server and controller to 
handle the request. There are two different algorithms that are concrete classes.
Algorithm 1 is a concrete class to implement algorithm. In this algorithm, we have a somewhat 
heuristic approach where we weigh each controller based on the amount of load it has with 
respect to the other controllers, we will tend to choose the one that is least loaded. Then within 
that controller, we weigh all the servers against one another and then decide which server to pick. 
This algorithm was found in our research, and we are using this as the benchmark algorithm with 
which we compare our algorithm’s performance to.
Algorithm 2 is a concrete class that implements algorithm. In this algorithm, we look at the 
amount of load that the new request will take up, and we then look to see which controller’s 
location will be closest to the request’s location. These locations are physical locations, and we 
want to minimize this so that data transfer time in a real distributed system would be minimized. 
Now, within this closest proximity controller, we look for the closest server and we check the 
load on that server to see if it can safely handle the new load from the request. If it can, great, we 
have found the server to execute the request, if not, then we need to find another server within 
that controller that can handle the request. If none of the servers within the proximity controller 
can safely handle the request, then we need to move to a backup controller and search through its 
servers to find a server that can safely handle the request. Each controller has 2 backup 
controllers and if after checking the 2nd backup we are still unable to find a server that can handle 
the request’s load, we issue a waiting time or delay for that request until one of the servers has 
enough space to take the request. We will be comparing the results of this algorithm with the 
results of the benchmark algorithm to see if we can reduce the number of failures or the amount 
of time the algorithm take, or both.
The Simulate class is used to do the simulation for our system. We pass the simulate class our 
input data, it runs both algorithms, and it provides us with the output data.
