import java.util.List;

import static java.lang.Integer.compare;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    }
    
    private LinkedList<Edge>[] vertexArr;
    private int numVertices;
    private class NodeCost implements Comparable<NodeCost>{
        int node;
        int cost;

        NodeCost(int n, int c){
            node = n;
            cost = c;
        };

        public int compareTo(NodeCost n1){
            return this.cost - n1.cost;
        }
    }
    private NodeCost[] nodeCosts;
    private List<Edge>[] graph;
    private Map<String, Integer> countryInGraph;
    private Map<String, String> countryCodes;
    private Map<String, Double> distances;

    public IRoadTrip (String [] args) {
        // Replace with your code
        if (args.length != 3) {
            System.err.println("ERROR: not all the files were passed");
        }

        countryCodes = new HashMap<>();
        distances = new HashMap<>();

        createBorderGraph(args[0]);
        setCountryCodes(args[2]);
        updateDistances(args[1]);
    }

    private void createBorderGraph(String borderFile){
        try (BufferedReader borders = new BufferedReader(new FileReader(borderFile))) {
            String line;
            while ((line = borders.readLine()) != null) {
                String[] part = line.split("=");
                String country = part[0].trim(); 
                String[] border = part[1].trim().split(";");
                int source = addCountryToGraph(country);
                for (String b : border) {
                    int dest = addCountryToGraph(b.trim());
                    graph[source].add(new Edge(source, dest, 0));
                }
            }
        } catch (IOException e) {
            System.err.println("ERROR: cant read the Border file");
        }
    }

    private int addCountryToGraph(String country){
        if(!countryInGraph.containsKey(country)){
            int newCountry = countryInGraph.size();
            countryInGraph.put(country, newCountry);
            graph[newCountry] = new ArrayList<>();
        }
        return countryInGraph.get(country);
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
        //make this into a method that can edit country capital distances 
        try (BufferedReader capDist = new BufferedReader(new FileReader(capDistFile))) {   
            String line;
            while ((line = capDist.readLine()) != null) {
                String[] part = line.split(",");
                String countryOne = part[1].trim();
                String countryTwo = part[3].trim();
                double distance = Double.parseDouble(part[4].trim());
            
                int nodeOne = countryInGraph.get(countryOne);
                int nodeTwo = countryInGraph.get(countryTwo);

                graph[nodeOne].add(new Edge(nodeOne, nodeTwo, (int) distance));
                graph[nodeTwo].add(new Edge(nodeTwo, nodeOne, (int) distance));
            }
        } catch (IOException e) {
            System.err.println("ERROR: cant read the Distance file");
        }
    }


    public int getDistance (String country1, String country2) {
        int source = countryInGraph.get(country1);
        int dest = countryInGraph.get(country2);
    }


    public List<String> findPath (String country1, String country2) {
        // Replace with your code
        return null;
    }


    public void acceptUserInput() {
        // Replace with your code
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("Enter the name of the first country (type EXIT to quit)");
            String country1 = input.nextLine();
            if (country1.equals("EXIT")) {
                break;
            }
            System.out.println("Enter the name of the second country (type EXIT to quit)");
            String country2 = input.nextLine();

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

