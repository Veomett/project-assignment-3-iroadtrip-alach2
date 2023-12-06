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
import java.util.Queue;
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
    // Set up the linked list that will act as the graph for this code
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
        public int compareTo(NodeCost nc1){
            return this.cost - nc1.cost;
        }
    }

    private NodeCost[] nodeCosts;

    //I have two maps - one holds the country code and name pairs and the other holds the country names and their indexes
    private Map<String, String> countryCodes;
    private Map<String, Integer> countryInGraph;
    private List<String> countriesWithLandBorders; //To save which countries have a border

    public IRoadTrip (String [] args) {
        if (args.length != 3) {
            System.err.println("ERROR: not all the files were passed");
            System.exit(-1);
        }

        //This initializes the size of the vertexArr and nodeCosts based on the number of countries passed in the file
        numVertices = getNumCountries(args[0]);
        vertexArr = new LinkedList[numVertices];
        nodeCosts = new NodeCost[numVertices];
        countryInGraph = new HashMap<>();
        for (int i = 0; i < numVertices; i++){
            vertexArr[i] = new LinkedList<>();
            nodeCosts[i] = new NodeCost(i, MAX_VALUE);
        }

        countryCodes = new HashMap<>();
        
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

    //Increases count everytime a country is found in the borders.txt file
    private int getNumCountries(String borderFile) {
        int count = 0;
        try (BufferedReader borders = new BufferedReader(new FileReader(borderFile))) {
            String line;
            while ((line = borders.readLine()) != null) {
                String[] part = line.split("=");
                count++;
                String[] border = part[1].trim().split(";");
                for (int i = 0; i < border.length; i++) {
                    count++;
                }

            }
        } catch (IOException e) {
            System.err.println("ERROR: can't read the Border file");
        }
        return count;
    }
   
    //Creates the graph of every country and their borders
    private void createBorderGraph(String borderFile){
        countriesWithLandBorders = new ArrayList<>();
        try (BufferedReader borders = new BufferedReader(new FileReader(borderFile))) {
            String line;
            while ((line = borders.readLine()) != null) {
                String[] part = line.split("=");
                String country = part[0].trim(); 
                
                int source = addCountry(country);
                if(source == -1){
                    System.out.println("Country not found  " + country);
                }
                if(part.length > 1){
                    String[] border = part[1].trim().split(";");
                    for (String b : border) {
                        String[] borderName = b.trim().split("\\s+(?=[0-9])", 2); //splits the border name before the border length
                        String borderOne = borderName[0].trim();
                        int dest = addCountry(borderOne);
                        if(dest == -1){
                            System.out.println("Country not found  " + borderOne.trim());
                        }
                    addEdge(source, dest, 0); //the weight is zero for now, it will change with the updateDistance method
                    if (!countriesWithLandBorders.contains(country)) {
                        countriesWithLandBorders.add(country);
                    }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: cant read the Border file");
        }
    }

    //Method to add countries to the countryInGraph map
    public int addCountry(String country){
            int index = countryInGraph.size();
            if(!countryInGraph.containsKey(country)){
                countryInGraph.put(country, index);
                return index;
            }else{
                return countryInGraph.get(country);
            }
    }

    //Method to switch the names to the proper country name
    private String standardCountryName(String input){
        switch(input){
            case "United States of America":
                return "United States";

            case "Bahamas":
                return "Bahamas, The";

            case "Surinam":
                return "Suriname";
            
            case "German Federal Republic":
                return "Germany";

            case "Czech Republic":
                return "Czechia";

            case "Italy/Sardinia":
                return "Italy";

            case "Macedonia (Former Yugoslav Republic of)":
                return "North Macedonia";

            case "Bosnia-Herzegovina":
                return "Bosnia and Herzegovina";

            case "Rumania":
                return "Romania";

            case "Russia (Soviet Union)":
                return "Russia";

            case "Belarus (Byelorussia)":
                return "Belarus";

            case "Yemen (Arab Republic of Yemen)":
                return "Yemen";

            case "Vietnam, Democratic Republic of":
                return "Vietnam";

            default:
                return input;
        }
    }

    //Method to fill the countryCodes map with the correct code and name for each country
    private void setCountryCodes(String stateNameFile){
        try (BufferedReader stateName = new BufferedReader(new FileReader(stateNameFile))) {  
            stateName.readLine();
            String line;
            while ((line = stateName.readLine()) != null) {
                String[] columns = line.split("\t");
                String countryCode = columns[1].trim();
                String countryName = columns[2].trim();
            
                countryName = standardCountryName(countryName); 
                if(countryInGraph.containsKey(countryName)){
                    countryCodes.put(countryCode, countryName);
                }  
            }
        } catch (IOException e) {
            System.err.println("ERROR: cant read the State Name file");
        }
    }

    //Method to change the edge weights of each edge between two countries
    private void updateDistances(String capDistFile){ 
        try (BufferedReader capDist = new BufferedReader(new FileReader(capDistFile))) {   
            capDist.readLine();
            String line;
            while ((line = capDist.readLine()) != null) {
                String[] part = line.split(",");
                String countryOneInitial = part[1].trim();
                String countryTwoInitial = part[3].trim();

                countryOneInitial = countryCodes.get(countryOneInitial);
                countryTwoInitial = countryCodes.get(countryTwoInitial);
                if(countryOneInitial != null && countryTwoInitial != null){
                    int distance = Integer.parseInt(part[4].trim());
                    
                    int nodeOne = countryInGraph.get((countryOneInitial));
                    int nodeTwo = countryInGraph.get((countryTwoInitial));

                    //They both need to have borders to be updated with the proper distance between them two
                    if (countriesWithLandBorders.contains(countryOneInitial) && countriesWithLandBorders.contains(countryTwoInitial)) {
                        updateEdgeWeight(nodeOne, nodeTwo, distance);
                        updateEdgeWeight(nodeTwo, nodeOne, distance);
                    } 
                }   
            }
        } catch (IOException e) {
            System.err.println("ERROR: cant read the Distance file");
        }
    }

    private void updateEdgeWeight(int src, int dst, int newWeight){
        for(Edge e : vertexArr[src]){
            if(e.dest == dst && e.source == src){
                e.weight = newWeight;
                break;
            }
        }
    }

    //Method to find the distance between two countries that share a border
    public int getDistance (String country1, String country2) {
        int source = countryInGraph.get(country1);
        int dest = countryInGraph.get(country2);
        if (!countriesWithLandBorders.contains(country1) || !countriesWithLandBorders.contains(country2)) {
            return -1;
        } 
 
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
            if(currVertex == dest){
                return shortestDist[dest];
            }
            for(Edge neighbor : vertexArr[currVertex]){
                int newDist = shortestDist[currVertex] + neighbor.weight;
                if(newDist < shortestDist[neighbor.dest]){
                    shortestDist[neighbor.dest] = newDist;
                    previous[neighbor.dest] = currVertex;
                    minHeap.add(new NodeCost(neighbor.dest, newDist));
                }
            }
        }
        if(shortestDist[dest] == MAX_VALUE){
            return 0;
        } else{
            return shortestDist[dest];
        }
    }

    //Method that finds the shortest path using bfs
    public List<String> findPath (String country1, String country2) {
        int source = countryInGraph.get(country1);
        int dest = countryInGraph.get(country2);

        boolean[] visited = new boolean[numVertices];
        int[] previous = new int[numVertices];

        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        visited[source] = true;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (Edge neighbor : vertexArr[current]) {
                int neighborNode = neighbor.dest;
                if (!visited[neighborNode]) {
                    visited[neighborNode] = true;
                    previous[neighborNode] = current;
                    queue.add(neighborNode);
                    if (neighborNode == dest) {
                        queue.clear();
                        break;
                    }
                }
            }
        }
        if (!visited[dest]) {
            System.out.println("No path exists between " + country1 + " and " + country2);
            return Collections.emptyList(); //Returns an empty list if no path exisits between two countries
        }
        List<String> path = new ArrayList<>();
        int curr = dest;
        while (curr != source) {
            int weight = getEdgeWeight(curr, previous[curr]);
            path.add(getCountryName(previous[curr]) + " --> " + getCountryName(curr) + " (" + weight + " km.)");
            curr = previous[curr];
        }
        path.add(getCountryName(source));

        Collections.reverse(path);
        return path;
    }

    private int getEdgeWeight(int src, int dest) {
        for (Edge edge : vertexArr[src]) {
            if (edge.dest == dest) {
                return edge.weight;
            }
        }
        return -1;
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
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("Enter the name of the first country (type EXIT to quit)");
            String country1 = input.nextLine().trim();
            if (country1.equals("EXIT")) {
                break;
            } else if (!countryInGraph.containsKey(country1)){ //Checks for validity of input, if it is a country or not
                System.out.println("This is an invaild country, choose again:");
                continue;
            }

            System.out.println("Enter the name of the second country (type EXIT to quit)");
            String country2 = input.nextLine().trim();
            if (country2.equals("EXIT")) {
                break;
            }else if (!countryInGraph.containsKey(country2)){ 
                System.out.println("This is an invaild country, choose again:");
                continue;
            }

            int dist = getDistance(country1, country2);
            if (dist == -1) {
                System.out.println("There is no path between " + country1 + " and " + country2);
            } else {
                List<String> path = findPath(country1, country2);
                if(path.isEmpty()){
                    continue;
                }
                System.out.println("Route from " + country1 + " to " + country2 + ":");
                for (String s : path) {
                    System.out.println("* " + s);
                }
            }
        } 
        input.close();
    }

    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();
    }
}