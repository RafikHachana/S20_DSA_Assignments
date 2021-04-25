package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


/*
    Rafik Hachana
    Group: BS19-04
    Telegram: @RafikHachana
*/


/*
    CF Submissions:
        - Task 1: http://codeforces.com/group/3ZU2JJw8vQ/contest/276900/submission/78288606
        - Task 2: http://codeforces.com/group/3ZU2JJw8vQ/contest/276900/submission/78288719
        - Task 3: http://codeforces.com/group/3ZU2JJw8vQ/contest/276900/submission/78288827
 */

public class Main {

    static class FastReader {  //Fast reader class to read input faster
        BufferedReader br; StringTokenizer st;
        private FastReader() {
            br = new BufferedReader(new InputStreamReader(System.in));
        }
        String next() {
            while (st == null || !st.hasMoreElements())
                try { st = new StringTokenizer(br.readLine()); } catch (IOException e) { e.printStackTrace();}
            return st.nextToken();
        }
        int nextInt() { return Integer.parseInt(next()); }
    }



    public static void main(String[] args) throws GraphException{
        //each method is the solution to a task
        //most of the documentation is inside the Graph class

        task1(); //Overall time complexity : O(|V|+|E|.log|E|)

        task2(); //Overall time complexity : O(|V|+|E|.log|E|)

        task3(); //Overall time complexity : O(|V|+|E|.log|E|)
    }

    public static void task1() throws GraphException {
        Graph<Integer,Integer> g = input(false);
        System.out.println(g.analyzeConnectivity());
    }

    public static void task2() throws GraphException {
        Graph<Integer,Integer> g = input(false);
        HashMap<Integer,Integer> component = g.vertexComponents();
        for(int i=1;i<=g.numberVertices();i++) {
            System.out.print(component.get(i)+" ");
        }
        System.out.println();
    }
    public static void task3() throws GraphException {
        Graph<Integer,Integer> g = input(true);
        ArrayList<Graph<Integer,Integer>> forest = g.minimumSpanningForest();
        System.out.println(forest.size());
        for(Graph tree:forest) tree.print();
    }
    public static Graph<Integer,Integer> input(boolean weighted) throws GraphException //input function for the tasks (in O(|V|+|E|.log|E|))
    {
        FastReader in  = new FastReader();
        int n = in.nextInt(), m = in.nextInt();
        Graph<Integer,Integer> g = new Graph<>(1);
        for(int i=1;i<=n;i++)  g.addVertex(i);
        for(int i=0;i<m;i++) {
            if(weighted) g.addEdge(in.nextInt(),in.nextInt(),in.nextInt());
            else g.addEdge(in.nextInt(),in.nextInt());  // This takes O(log|E|) time.
        }
        return g;
    }
}

/*
Graph representation:
    - The class Graph below is a generic class that represents graphs with both an adjacency list and an edge list, using vertex and edge objects.
    - The class is generic in terms of the vertex value (T) and edge weight (U),both required to be comparable types.
    - The class doesn't support duplicate vertex values and throws an exception when the user adds 2 vertices with the same value.
    - The class contains 4 parts: methods and members related to the graph representation, and 3 parts with methods related to each task.
    - The class can represent weighted and unweighted graphs, though for unweighted graphs a default edge weight should be specified
    (because of the usage of a generic type).
    - Small note: There different dfs functions defined in the class, each one has a different signature and serves a different purpose (there is one for each task)
 */

class Graph<T extends  Comparable, U extends Comparable> //T is the object type in vertices, U is the type used to represent weights
{
    /*
    Vertex class:
        - Contains a value for the vertex (type T), and an ArrayList of edges that represents the adjacency list of the vertex.
     */
    private class Vertex implements Comparable<Vertex> {
        T value;
        ArrayList<Edge> AdjList;
        Vertex(T value) {
            this.value = value;
            AdjList = new ArrayList<>();
        }
        public boolean equals(Vertex v) {
            return value.compareTo(v.value)==0;
        }

        @Override
        public int compareTo(Vertex vertex) {
            return value.compareTo(vertex.value);
        }
    }
    /*
    Edge class:
        - Contains 2 vertices and a weight of type U.
     */
    private class Edge implements Comparable<Edge> {
        Vertex u,v;
        U weight;
        Edge(Vertex u,Vertex v,U weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge edge) {
            int compWeight = weight.compareTo(edge.weight);
            if(compWeight!=0) return compWeight;
            int compFirst = u.compareTo(edge.u);
            if(compFirst!=0) return compFirst;
            return v.compareTo(edge.v);
        }
    }
    private U defaultWeight; //the default weight in case the graph is unweighted
    private HashMap<T,Vertex> vertices; //A mapping from values to vertices for fast access using the vertex value.
    private TreeSet<Edge> edgeList; //The edge list, used TreeSet for the fast lookup and keeping a sorted order for Kruskal's algorithm
    private HashSet<Vertex> visited; //A HashSet that contains visited vertices during a DFS, will be used by the DFS functions below
    //Constructors
    public Graph() {  //Constructor for weighted graph (in O(1))
        vertices = new HashMap<>();
        edgeList = new TreeSet<>();
        defaultWeight = null;
        visited = new HashSet<>();
    }
    public Graph(U defaultWeight) { //2nd constructor, for a fixed edge weight (a specific case is an unweighted graph) (in O(1))
        vertices = new HashMap<>();
        edgeList = new TreeSet<>();
        this.defaultWeight = defaultWeight;
        visited = new HashSet<>();
    }
    public int numberVertices() {  //Returns the number of vertices in the graph (in O(1))
        return vertices.size();
    }

    public void addVertex(T value) throws GraphException {  //adds a vertex to the graph (in O(1))
        if(vertices.keySet().contains(value)) throw new GraphException("Vertex with value "+value+" already exists. Duplicate vertex values not allowed.");
        Vertex u = new Vertex(value);
        vertices.put(value,u);
    }


    public void addEdge(T first,T second) throws GraphException {  //adds an unweighted edge (or a default weight edge) (in O(log|E|))
        if(defaultWeight==null) throw new GraphException("Default weight undefined.");
        addEdge(first,second,defaultWeight);
    }
    public void addEdge(T first,T second,U weight) throws GraphException { //adds a weighted edge (in O(log|E|))
        if(!vertices.keySet().contains(first)) throw new GraphException("Vertex "+first+" is not part of the graph.");
        if(!vertices.keySet().contains(second)) throw new GraphException("Vertex "+second+" is not part of the graph.");
        if(first.compareTo(second)==0) throw new GraphException("Self-loop from vertex "+first);
        Vertex u = vertices.get(first);
        Vertex v = vertices.get(second);
        Edge e = new Edge(u,v,weight);
        if(edgeList.contains(e)) throw new GraphException("An edge between "+first+" and "+second+" already exists.");
        edgeList.add(e);
        u.AdjList.add(e); v.AdjList.add(e);
    }

    //CODE RELATED TO TASK 1

    /*
    Algorithm:
        - Do a DFS from any vertex and mark nodes as visited.
        - 2 cases:
            * All vertices are visited --> return "GRAPH IS CONNECTED"
            * A vertex is not visited --> it is not connected to the vertex from which we started the DFS.
    Time Complexity: O(|V|+|E|)
     */
    public String analyzeConnectivity() { //(in O(|V|+|E|))
        visited.clear();  //clear from any previous DFS call
        Vertex start = vertices.values().iterator().next(); //pick start as the first vertex value the iterator points at
        dfs(start);  //do DFS  (in O(|V|+|E|))
        for(Vertex u:vertices.values()) {  //Look for unvisited vertices
            if(!visited.contains(u)) {
                return "VERTICES "+start.value+" AND "+u.value+" ARE NOT CONNECTED BY A PATH";
            }
        }
        return "GRAPH IS CONNECTED";
    }
    private void dfs(Vertex current) { //The DFS function (marks a vertex as visited and recursively does DFS on the adj. vertices)
        visited.add(current);
        for(Edge e: current.AdjList) {
            Vertex next = (e.u.equals(current)?e.v:e.u);
            if(!visited.contains(next)) dfs(next);
        }
    }


    //CODE RELATED TO TASK 2
    /*
    Algorithm:
        - Have a component counter that starts at 1
        - Loop through vertices, each unvisited vertex means that there is an unvisited component:
            * Run DFS from the unvisited vertex and map the connected vertices to the component number (value of component counter)
            * Increment the component counter.
        - Return the HashMap used in the DFS to map each vertex to the respective component number.
    Time Complexity:  O(|V|+|E|)
     */
    public HashMap<T,Integer> vertexComponents() {  // (in O(|V|+|E|)) (amortized since the sum of time of all DFS calls inside the loop is O(|V|+|E|))
        visited.clear(); //clear from any previous DFS call
        HashMap<T,Integer> answer = new HashMap<>();  //the result HashMap
        Integer component = 1;  //component counter
        for(Vertex u:vertices.values()) {  //Overall complexity of the loop is O(|V|+|E|)
            if(!visited.contains(u)) {  //for each unvisited vertex, run dfs and increment the number of components
                dfs(u,answer,component);
                component++;
            }
        }
        return answer;
    }
    private void dfs(Vertex current,HashMap<T,Integer> map,Integer currentComponent) {  //the associated DFS function
        visited.add(current);
        map.put(current.value,currentComponent); //maps the vertex to the component
        for(Edge e: current.AdjList) { //visits all unvisited adjacent vertices
            Vertex next = (e.u.equals(current)?e.v:e.u);
            if(!visited.contains(next)) dfs(next,map,currentComponent);
        }
    }

    //CODE RELATED TO PART 3

    /*
    Algorithm: Kruskal
        - No need to sort the edge since it is already sorted (TreeSet)
        - Create a new empty graph that will be the forest
        - Add the vertices to the forest
        - Iterate through the edge list (already sorted from least to greatest by weight):
            * Add the edge to the forest it if doesn't cause a cycle (use the DSU data structure to check)
        - Call the split to components function on the forest to return an array of graphs instead of one graph (easier for output)
        - Return the ArrayList of Graphs
    Time Complexity: O(|V|+|E|)
        Complexity of Kruskal's: O(|V|+|E|) (However there is a log factor when adding an edge to the graph)
        Complexity of splitComponents: O(|V|+|E|) )
     */

    public ArrayList<Graph<T,U>> minimumSpanningForest() throws GraphException {
        Graph<T,U> forest = new Graph<>();  //the initially empty forest
        DisjointSets<T> dsu = new DisjointSets<>();  //the DSU data structure
        for(T value:vertices.keySet()) {  //O(|V|)
            forest.addVertex(value); //add vertices to the forest
            dsu.add(value);  //add vertices to the DSU
        }
        for(Edge e:edgeList) {  //loop through edges sorted in non-decreasing order by their weight O(|E|)
            T firstTree = dsu.find(e.u.value);  //check the sets in the DSU
            T secondTree = dsu.find(e.v.value);  //this call is in O(1) amortized time
            if(firstTree.compareTo(secondTree)!=0) {  //if they are in different sets --> disconnected --> the new edge wouldn't cause a cycle
                forest.addEdge(e.u.value,e.v.value,e.weight);  //add the edge to the forest
                dsu.union(firstTree,secondTree);  //call union on the sets of the 2 vertices in the DSU
            }
        }
        return forest.splitComponents();  //split the forest to its components and return the resulting ArrayList of graphs (which are trees)
    }
    public ArrayList<Graph<T,U>> splitComponents() throws GraphException {  //function that splits the graph into its components (in O(|V|+|E|))
        visited.clear();  //clear from any previous DFS call
        ArrayList<Graph<T,U>> answer = new ArrayList<>();  //the result: an ArrayList of graphs
        Integer component = 0;  //number of components
        for(Vertex u:vertices.values()) {  //with amortized analysis the overall complexity of all DFS calls is O(|V|+|E|)
            if(!visited.contains(u)) {  //an unvisited vertex means a new component
                answer.add(new Graph<>());  //make an empty graph of the new component
                answer.get(component).addVertex(u.value);  //and add the vertex
                dfs(u,answer.get(component));  //call DFS from the vertex and add all accessible vertices to the same component
                component++; //increment the number of components
            }
        }
        return answer;
    }
    private void dfs(Vertex current,Graph<T,U> g)  throws GraphException {  //dfs function for splitting to components
        visited.add(current);
        for(Edge e: current.AdjList) {
            Vertex next = (e.u.equals(current)?e.v:e.u);
            if(!visited.contains(next)) {
                g.addVertex(next.value);  //add the vertex to the current component
                g.addEdge(e.u.value,e.v.value,e.weight); //add the edge to the current component
                dfs(next,g);  //call dfs recursively
            }
        }
    }
    public void print() { //a print function that prints the graph in the format required by task 3 (in O(|E|))
        Vertex start = vertices.values().iterator().next();
        System.out.println(vertices.size()+" "+start.value);
        for(Edge e:edgeList) {
            System.out.println(e.u.value+" "+e.v.value+" "+e.weight);
        }
    }
}

class DisjointSets<T extends Comparable>  { //DSU data structure for Kruskal's algorithm
    HashMap<T,T> parent;
    DisjointSets() {
        parent = new HashMap<>();
    }
    void add(T x) { //adds a new disjoint element to the data structure in O(1)
        parent.put(x,x);
    }
    T find(T x) { //finds the set (represented by its root) of an element in O(1) amortized (using path compression)
        if(parent.get(x).compareTo(x)==0) return x;
        parent.replace(x,find(parent.get(x)));     //path compression
        return parent.get(x);
    }
    void union(T x,T y) {  //unites 2 sets by setting one root as the parent of the other root (O(1) amortized)
        parent.replace(find(x),find(y));
    }
}

class GraphException extends Exception  //an exception class created for the graph representation
{
    String message;
    GraphException(String message) {
        this.message = message;
    }
    @Override
    public String toString() {
        return message;
    }
}

