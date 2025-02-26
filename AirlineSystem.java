import java.util.Set;
import java.util.ArrayList;
import java.util.*;
import java.io.*;

final public class AirlineSystem implements AirlineInterface
{
	//Will contain the cityNames, stored in an array list as required
	//by interface. Used to find index of cities.
	ArrayList<String> cityNames;
	
	//Digraph that will be used to create graph through the program
	private Digraph G;
	
	//Global variable that is used to fill dijkstras while waiting for
	//graph to fill completely.
	private static final int INFINITY = Integer.MAX_VALUE;
	
	public boolean loadRoutes(String fileName)
	{
		try
		{
			//Read in the file from the user, create a new graph of
			//the files  contents
			Scanner fileScan = new Scanner(new FileInputStream(fileName));
			G = new Digraph(Integer.parseInt(fileScan.nextLine()));
			
			//Initialize cityNames.
			cityNames = new ArrayList<String>();
			
			//Adds all cities from the file given by the user.
			for(int i = 0; i < G.v; i++)
				cityNames.add(fileScan.nextLine());
			
			//Create edges between each vertex. Two edges are needed to
			//adhere to assignment rules.
			while(fileScan.hasNext())
			{
				//Creates an edge from current vertex to neighbor.
				WeightedDirectedEdge temp = new WeightedDirectedEdge(fileScan.nextInt() - 1, fileScan.nextInt() - 1, fileScan.nextInt(), fileScan.nextDouble());
				
				//Creates an edge from neighbor to vertex.
				WeightedDirectedEdge temp2 = new WeightedDirectedEdge(temp.w, temp.v, temp.distance, temp.price);
				
				//Add both edges to the graph
				G.addEdge(temp);
				G.addEdge(temp2);
			}
			
			fileScan.close();
			
			return true;
		}
		catch(FileNotFoundException e)
		{
			//If the file was not found, return false as the program
			//could not continue.
			return false;
		}
	}

	public Set<String> retrieveCityNames()
	{
		//To return the cityNames, return a new hashset that is made
		//using the citynames list.
		return new HashSet<String>(cityNames);
	}

	public Set<Route> retrieveDirectRoutesFrom(String city)throws CityNotFoundException
	{
		//If the city is not found, return an error
		if(!cityNames.contains(city))
			throw new CityNotFoundException(city + "was not found.");
		else
		{
			//Create a hashset of routes that will contain the routes from
			//all vertexs
			Set<Route> routes = new HashSet<Route>();
			
			//For each loop that will add a new route into the routes
			//set
			for(WeightedDirectedEdge e: G.adj.get(cityNames.indexOf(city)))
				routes.add(new Route(city, cityNames.get(e.to()), e.distance(), e.price()));
			
			return routes;
		}
	}

	public Set<ArrayList<String>> fewestStopsItinerary(String source, String destination) throws CityNotFoundException
	{
		//If the source is not found, return an error to alert the user.
		if(!cityNames.contains(source))
			throw new CityNotFoundException(source + " was not found.");
		
		//If the destination is not found, return an error to alert the user.
		if(!cityNames.contains(destination))
			throw new CityNotFoundException(destination + "was not found.");
		
		//Make a new set of our array list.
		Set<ArrayList<String>> set = new HashSet<ArrayList<String>>();
		
		//Make a new array list to hold each route.
		ArrayList<String> route = new ArrayList<String>();
		
		//Add the array list to the set.
		set.add(route);
		
		//Create a bfs of our graph, partially taken from labs.
		G.bfs(cityNames.indexOf(source));
		
		//If the destination has not been marked, return the graph
		if(!G.marked[cityNames.indexOf(destination)])
			return set;
		else
		{
			//Create a new stack to hold the paths from city to city.
			Stack<Integer> path = new Stack<>();
			
			//From lab, push the paths onto the stack.
			for(int x = cityNames.indexOf(destination); x != cityNames.indexOf(source); x = G.edgeTo[x])
				path.push(x);
			
			//Create a previous vertext variable that holds the index
			//of the source.
			int prevVertex = cityNames.indexOf(source);
			
			//Add the source to the route linked list.
			route.add(source);
			
			
			//While the stack is not empty, pop and add all routes
			//into the routes linked list.
			while(!path.empty())
			{
				int v = path.pop();
				route.add(cityNames.get(v));
				
				//Change the vertex to be the vertex popped off the
				//stack.
				prevVertex = v;
			}
			
			//Return the set to the program.
			return set;
		}
	}

	public Set<ArrayList<Route>> shortestDistanceItinerary(String source, String destination) throws CityNotFoundException
	{
		//If the source was not found, return an error and alert the user.
		if(!cityNames.contains(source))
			throw new CityNotFoundException(source + " was not found.");
		
		//If the destination was not found, return an error and aler the user.
		if(!cityNames.contains(destination))
			throw new CityNotFoundException(destination + "was not found.");
		
		//Create a new set that will hold the arraylist of routes.
		Set<ArrayList<Route>> set = new HashSet<ArrayList<Route>>();
		
		//Create a new array list to hold the routes from each vertex.
		ArrayList<Route> route = new ArrayList<Route>();
		
		//Add the array list to the set.
		set.add(route);
		
		//Create a dijkstras of the graph. Taken from lab.
		G.dijkstras(cityNames.indexOf(source), cityNames.indexOf(destination));
		
		//If the graph does not have the destination marked, return the graph.
		if(!G.marked[cityNames.indexOf(destination)])
			return set;
		else
		{
			//Create a new stack to hold the paths from each vertex.
			Stack<Integer> path = new Stack<>();
			
			//Fill the stack with paths.
			for(int x = cityNames.indexOf(destination); x != cityNames.indexOf(source); x = G.edgeTo[x])
				path.push(x);
			
			//Create a previous Vertex variable.
			int prevVertex = cityNames.indexOf(source);
			
			//While the path is not empty, pop each vertex and search for our vertex.
			while(!path.empty())
			{
				int v = path.pop();
				
				for(WeightedDirectedEdge e: G.adj.get(prevVertex))
				{
					//Once the correct edge has been found, add this route to the list.
					if(e.to() == v)
					{
						route.add(new Route(cityNames.get(prevVertex), cityNames.get(v), e.distance(), e.price()));
						break;
					}
				}
				
				prevVertex = v;
			}
			
			return set;
		}
	}

	public Set<ArrayList<Route>> shortestDistanceItinerary(String source, String transit, String destination) throws CityNotFoundException
	{
		//If the cityNames does not contain source city, return an error to alert the user.
		if(!cityNames.contains(source))
			throw new CityNotFoundException(source + " was not found.");
		
		//If the cityNames does not contain the transit city, return an error to alert the user.
		if(!cityNames.contains(transit))
			throw new CityNotFoundException(transit + "was not found.");
		
		//If the cityNames does not contain the destination city, return an error to alert the user.
		if(!cityNames.contains(destination))
			throw new CityNotFoundException(destination + "was not found.");
		
		//Create a set to hold the array list of routes.
		Set<ArrayList<Route>> set = new HashSet<ArrayList<Route>>();
		
		//Create an array list to holds the routes
		ArrayList<Route> route = new ArrayList<Route>();
		
		//Add the routes to the set.
		set.add(route);
		
		//Create a dijkstras of the graph.
		G.dijkstras(cityNames.indexOf(source), cityNames.indexOf(transit));
		
		//If the graph does not have the transit city marked, return the graph.
		if(!G.marked[cityNames.indexOf(transit)])
			return set; 
		else
		{
			//Create a stack that holds the paths.
			Stack<Integer> path = new Stack<>();
			
			//Push all paths onto the stack.
			for(int x = cityNames.indexOf(transit); x != cityNames.indexOf(source); x = G.edgeTo[x])
				path.push(x);
			
			//Create a previous vertex variable to hold the index of source city.
			int prevVertex = cityNames.indexOf(source);
			
			//Loop through all paths on the stackk.
			while(!path.empty())
			{
				int v = path.pop();
				
				for(WeightedDirectedEdge e: G.adj.get(prevVertex))
				{
					//Once the correct vertex has been found, add it to the
					//routes and break.
					if(e.to() == v)
					{
						route.add(new Route(cityNames.get(prevVertex), cityNames.get(v), e.distance(), e.price()));
						break;
					}
				}
				
				prevVertex = v;
			}
		}
		
		//Above was only source to transit, we now need to do transit to destination.
		G.dijkstras(cityNames.indexOf(transit), cityNames.indexOf(destination));
		
		if(!G.marked[cityNames.indexOf(transit)])
			return set;
		else
		{
			Stack<Integer> path = new Stack <>();
			for(int x = cityNames.indexOf(destination); x != cityNames.indexOf(transit); x = G.edgeTo[x])
				path.push(x);
			
			int prevVertex = cityNames.indexOf(transit);
			
			while(!path.empty())
			{
				int v = path.pop();
				
				for(WeightedDirectedEdge e: G.adj.get(prevVertex))
				{
					if(e.to() == v)
					{
						route.add(new Route(cityNames.get(prevVertex), cityNames.get(v), e.distance(), e.price()));
						break;
					}
				}
				prevVertex = v;
			}
		}
		
		return set;
	}

	public boolean addCity(String city)
	{
		//If the city has already been added, return false.
		if(cityNames.contains(city))
			return false;
		else
		{
			//Add the city to the list.
			cityNames.add(city);
			
			//Add the directed edge to the graph.
			G.adj.add(new LinkedList<WeightedDirectedEdge>());
			
			//Increment the vertexes.
			G.v++;
			
			return true;
		}
	}

	public boolean addRoute(String source, String destination, int distance, double price) throws CityNotFoundException
	{
		//In order to add a route to the graph, must make sure the city
		//and destination are both in the graph.
		if(!cityNames.contains(source))
			throw new CityNotFoundException(source + " was not found.");
		
		if(!cityNames.contains(destination))
			throw new CityNotFoundException(destination + "was not found.");
		
		//If the route already exists return false as we do not
		//want to add it twice.
		for(Route route: retrieveDirectRoutesFrom(source))
			if(route.destination.equals(destination))
				return false;
		
		//Add both edge to and from the vertex.
		G.addEdge(new WeightedDirectedEdge(cityNames.indexOf(source), cityNames.indexOf(destination), distance, price));
		G.addEdge(new WeightedDirectedEdge(cityNames.indexOf(destination), cityNames.indexOf(source), distance, price));
		
		return true;
	}

	public boolean updateRoute(String source, String destination, int distance, double price) throws CityNotFoundException
	{
		//To update the route, make sure both the city and the destination
		//exist in the graph.
		if(!cityNames.contains(source))
			throw new CityNotFoundException(source + " was not found.");
		
		if(!cityNames.contains(destination))
			throw new CityNotFoundException(destination + "was not found.");
		
		//Loop over all routes to find the route that needs to be changed.
		for(Route route : retrieveDirectRoutesFrom(source))
		{
			if(route.destination.equals(destination))
			{
				for(WeightedDirectedEdge e: G.adj.get(cityNames.indexOf(source)))
				{
					//Once the correct route has been found, update the
					//distance and price passed in from the parameters. 
					if(e.to() == cityNames.indexOf(destination))
					{
						e.distance = distance;
						e.price = price;
						break;
					}
				}
				
				for(WeightedDirectedEdge e: G.adj.get(cityNames.indexOf(destination)))
				{
					//Above for loop was only for one edge from source to
					//destination. Must update both edges in the graph.
					if(e.to() == cityNames.indexOf(source))
					{
						e.distance = distance;
						e.price = price;
						break;
					}
				}
				
				return true;
			}
			
		}
		
		return false;
	}
  
	//Taken from labs
	private class Digraph
	{
		private int v;
		private int e;
		private ArrayList<LinkedList<WeightedDirectedEdge>> adj;
		private boolean[] marked;
		private int[] edgeTo;
		private int[] distTo;
	  
		//Taken from labs
		public Digraph(int v)
		{
			if(v < 0) throw new RuntimeException("Number of vertices must be nonnegative");
			this.v = v;
			this.e = 0;
			
			this.adj = new ArrayList<LinkedList<WeightedDirectedEdge>>();
			
			for(int i = 0; i < v; i++)
				adj.add(new LinkedList<WeightedDirectedEdge>());
		}
		
		
		//Taken from labs
		public void addEdge(WeightedDirectedEdge edge)
		{
			int from = edge.from();
			adj.get(from).add(edge);
			e++;
		}
	  
		//Taken from labs
		public Iterable<WeightedDirectedEdge> adj(int v)
		{
			return adj.get(v);
		}
		
		//Taken from labs
		public void bfs(int source)
		{
			marked = new boolean[this.v];
			distTo = new int[this.e];
			edgeTo = new int[this.v];

			Queue<Integer> q = new LinkedList<Integer>();
			for (int i = 0; i < v; i++)
			{
				distTo[i] = INFINITY;
				marked[i] = false;
			}
			distTo[source] = 0;
			marked[source] = true;
			q.add(source);

			while (!q.isEmpty())
			{
				int v = q.remove();
			
				//For loop over all neighbors of the current vertex
				for (WeightedDirectedEdge w : adj(v))
				{
					//Check whiched edges are marked
					if (!marked[w.to()])
					{
						//TODO: Complete BFS implementation
						//update values and add current neighbor
			  
						//Mark the edge to be true
						marked[w.to()] = true;
			  
						//Set the current edgeTo to be the vertex
						edgeTo[w.to()] = v;
			  
						//Set the distanceTo to the current distance plus 1
						distTo[w.to()] = distTo[v] + 1;
			  
						//Add to the queue
						q.add(w.to());
					}
				}
			}
		}
		
		//Taken from labs
		public void dijkstras(int source, int destination)
		{
			marked = new boolean[this.v];
			distTo = new int[this.v];
			edgeTo = new int[this.v];


			for (int i = 0; i < v; i++)
			{
				distTo[i] = INFINITY;
				marked[i] = false;
			}
			distTo[source] = 0;
			marked[source] = true;
			int nMarked = 1;

			int current = source;
			while (nMarked < this.v)
			{
				for (WeightedDirectedEdge w : adj(current))
				{
					if (distTo[current]+w.distance() < distTo[w.to()])
					{
						//set the edge to be the current vertex
						edgeTo[w.to()] = current;
				
						//set the distance to be the edge's current distance
						distTo[w.to()] = distTo[current] + w.distance();
					} 
				}
				//Find the vertex with minimim path distance
				//This can be done more effiently using a priority queue!
				int min = INFINITY;
				current = -1;

				for(int i=0; i<distTo.length; i++)
				{
					if(marked[i])
						continue;
			
					if(distTo[i] < min)
					{
						min = distTo[i];
						current = i;
					}
				}

				//TODO: Update marked[] and nMarked. Check for disconnected graph.
			
				//If current == -1 then no short distance was found, graph is
				//disconnected
				if(current == -1 )
					break;
			
				//Update the amount of vertex's marked and change the current
				//vertex marked to be true.
				nMarked++;
				marked[current] = true;
			}
		}
	}
  
	//Taken from labs
	private class WeightedDirectedEdge
	{
		private final int v;
		private final int w;
		private int distance;
		private double price;
		
		public WeightedDirectedEdge(int v, int w, int distance, double price)
		{
			this.v = v;
			this.w = w;
			this.distance = distance;
			this.price = price;
		}
		
		public int from()
		{
			return v;
		}
		
		public int to()
		{
			return w;
		}
		
		public int distance()
		{
			return distance;
		}
		
		public double price()
		{
			return price;
		}
	}
}