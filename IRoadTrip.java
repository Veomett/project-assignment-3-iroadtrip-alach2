import java.util.List;

import static java.lang.Integer.MAX_VALUE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Collections;

public class IRoadTrip {
    class Edge implements Comparable<Edge>{
        int source;
        int dest;
        int weight;

        public Edge(int s, int d, int w){
            source = s;
            dest = d;
            weight = w;
        }
        public int compareTo(Edge e){
            return this.weight - e.weight;
        }
    }
    private LinkedList<Edge>[] vertexArr;
    private int numVertices;
    // This is used for Dijkstra's algorithm
    private class NodeCost implements Comparable<NodeCost>{
        int node;
        int cost;
        NodeCost(int n, int c){
            node=n;
            cost=c;
        };
        @Override
        public int compareTo(NodeCost nc1)
        {
            return this.cost - nc1.cost;
        }
    }

    private NodeCost[] nodeCosts;

    /*class Graph{
        private List<Edge>[] graph;
        private Map<String, Integer> countryInGraph;

        public Graph(int numCountries){
            graph = new ArrayList[numCountries];
            for (int i = 0; i < numCountries; i++) {
                graph[i] = new ArrayList<>();
            }
            countryInGraph = new HashMap<>();
        }

        public void addEdge(int source, int dest, int weight){
            graph[source].add(new Edge(source, dest, weight));
            graph[dest].add(new Edge(dest, source, weight));
        }

        public int addCountry(String country){
            int index = countryInGraph.size();
            countryInGraph.put(country, index);
            return index;
        }

        public List<Edge>[] getGraph(){
            return graph;
        }
    */

    //private Graph graph;

    private Map<String, String> countryCodes;
    private Map<String, Integer> countryInGraph;

    public IRoadTrip (String [] args) {
        // Replace with your code
        if (args.length != 3) {
            System.err.println("ERROR: not all the files were passed");
            System.exit(-1);
        }
        numVertices = getNumCountries(args[0]);
        vertexArr = new LinkedList[getNumCountries(args[0])];
        nodeCosts = new NodeCost[numVertices];
        countryInGraph = new HashMap<>();
        for (int i = 0; i < numVertices; i++){
            vertexArr[i] = new LinkedList<>();
            nodeCosts[i] = new NodeCost(i, MAX_VALUE);
        }

        countryCodes = new HashMap<>();
        
        //int numCountries = getNumCountries(args[0]);
        //graph = new Graph(numCountries);
        
        createBorderGraph(args[0]);
        setCountryCodes(args[2]);
        updateDistances(args[1]);
    }
    public void addEdge(int v1, int v2, int weight){
        Edge v1Edge = new Edge(v1, v2, weight);
        vertexArr[v1].add(v1Edge);
        Edge v2Edge = new Edge(v2, v1, weight);
        vertexArr[v2].add(v2Edge);
    }

    private int getNumCountries(String borderFile) {
        int count = 0;
        try (BufferedReader borders = new BufferedReader(new FileReader(borderFile))) {
            String line;
            while ((line = borders.readLine()) != null) {
                line.split("=");
                count++;

            }
        } catch (IOException e) {
            System.err.println("ERROR: can't read the Border file");
        }
        return count;
    }
   
    private void createBorderGraph(String borderFile){
        try (BufferedReader borders = new BufferedReader(new FileReader(borderFile))) {
            String line;
            while ((line = borders.readLine()) != null) {
                String[] part = line.split("=");
                String country = part[0].trim(); 
                System.out.println(country);
        
                int source = addCountry(country);
                if(source == -1){
                    System.out.println("Country not found  " + country);
                }
                if(part.length > 1){
                    String[] border = part[1].trim().split(";");
                for (String b : border) {
                    int dest = addCountry(b.trim());
                    if(dest == -1){
                    System.out.println("Country not found  " + b.trim());
                }
                    addEdge(source, dest, 0);
                }
            }
            }
        } catch (IOException e) {
            System.err.println("ERROR: cant read the Border file");
        }
    }

    public int addCountry(String country){
            int index = countryInGraph.size();
            countryInGraph.put(country, index);
            return index;
    }

    private void setCountryCodes(String stateNameFile){
        try (BufferedReader stateName = new BufferedReader(new FileReader(stateNameFile))) {   
            String line;
            while ((line = stateName.readLine()) != null) {
                String[] columns = line.split("\t");
                String countryCode = columns[1].trim();
                String countryName = columns[2].trim();
                countryCodes.put(countryName,countryCode);
            }
        } catch (IOException e) {
            System.err.println("ERROR: cant read the State Name file");
        }
    }

    private void updateDistances(String capDistFile){ 
        try (BufferedReader capDist = new BufferedReader(new FileReader(capDistFile))) {   
            capDist.readLine();
            String line;
            while ((line = capDist.readLine()) != null) {
                String[] part = line.split(",");
                String countryOne = part[1].trim();
                String countryTwo = part[3].trim();
                double distance = Double.parseDouble(part[4].trim());

                    int nodeOne = countryInGraph.get(countryOne);
                    int nodeTwo = countryInGraph.get(countryTwo);
                    addEdge(nodeOne, nodeTwo, (int) distance);
                    addEdge(nodeTwo, nodeOne, (int) distance);
                
            }
        } catch (IOException e) {
            System.err.println("ERROR: cant read the Distance file");
        }
    }


    public int getDistance (String country1, String country2) {
        int source = countryInGraph.get(country1);
        int dest = countryInGraph.get(country2);

        int[] shortestDist = new int[numVertices];
    
        for(int i = 0; i < numVertices; i++){
            if(i == source){
                shortestDist[source] = 0;
            }
            shortestDist[i] = MAX_VALUE;
        }
        PriorityQueue<NodeCost> minHeap = new PriorityQueue<>();
        minHeap.add(new NodeCost(source, 0));

        while(!minHeap.isEmpty()){
            NodeCost current = minHeap.poll();
            int currVertex = current.node;
            int currCost = current.cost;
            if(currVertex == dest){
                return shortestDist[dest];
            }
            for(Edge neighbor : vertexArr[currVertex]){
                int newDist = shortestDist[currVertex] + neighbor.weight;
                if(newDist < shortestDist[neighbor.dest]){
                    shortestDist[neighbor.dest] = newDist;
                    minHeap.add(new NodeCost(neighbor.dest, newDist));
                }
            }
        }
        return -1;
    }


    public List<String> findPath (String country1, String country2) {
        int source = countryInGraph.get(country1);
        int dest = countryInGraph.get(country2);

        int[] shortestDist = new int[numVertices];
        int[] previous = new int[numVertices];
        for(int i = 0; i < numVertices; i++){
            if(i == source){
                shortestDist[source] = 0;
            }
            shortestDist[i] = MAX_VALUE;
        }
        PriorityQueue<NodeCost> minHeap = new PriorityQueue<>();
        minHeap.add(new NodeCost(source, 0));

        while(!minHeap.isEmpty()){
            NodeCost current = minHeap.poll();
            int currVertex = current.node;

            for(Edge neighbor : vertexArr[currVertex]){
                int newDist = shortestDist[currVertex] + neighbor.weight;
                if(newDist < shortestDist[neighbor.dest]){
                    shortestDist[neighbor.dest] = newDist;
                    previous[neighbor.dest] = currVertex;
                    minHeap.add(new NodeCost(neighbor.dest, newDist));
                }
            }
        }
        List<String> path = new ArrayList<>();
        int curr = dest;
        while(curr != source){
            path.add(getCountryName(curr));
            curr = previous[curr];
        }
        path.add(getCountryName(source));

        Collections.reverse(path);
        return path;
    }

    private String getCountryName(int node){
        for(Map.Entry<String, Integer> entry : countryInGraph.entrySet()){
            if(entry.getValue() == node){
                return entry.getKey();
            }
        }
        return "";
    }
    public void acceptUserInput() {
        // Replace with your code
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("Enter the name of the first country (type EXIT to quit)");
            String country1 = input.nextLine();
            if (country1.equals("EXIT")) {
                break;
            } else if (countryInGraph.get(country1) == null){
                System.out.println("This is an invaild country, choose again:");
                continue;
            }
            System.out.println("Enter the name of the second country (type EXIT to quit)");
            String country2 = input.nextLine();
            if (country2.equals("EXIT")) {
                break;
            }else if (countryInGraph.get(country2) == null){
                System.out.println("This is an invaild country, choose again:");
                continue;
            }

            int dist = getDistance(country1, country2);
            List<String> path = findPath(country1, country2);

            if (dist == -1) {
                System.out.println("There is no path between " + country1 + " and " + country2);
            } else {
                System.out.println("Route from " + country1 + " to " + country2 + ":");
                for (String s : path) {
                    System.out.println("* " + s);
                }
            }
        } 
        input.close();
        System.out.println("IRoadTrip - skeleton");
    }

    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();
    }

}

