import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class Dijkstra {
    private Map<Integer, List<Edge>> graph = new HashMap<>();
    private Map<Integer, NodeCoordinates> nodeCoordinates = new HashMap<>();
    private int nodesSelected = 0;
    private List<Integer> shortestPath = new ArrayList<>();
    private int totalTravelTime = 0;

    public Dijkstra(String nodeFile, String edgeFile) {
        loadGraph(nodeFile, edgeFile);
    }

    private void loadGraph(String nodeFile, String edgeFile) {
        try {
            BufferedReader nodeReader = new BufferedReader(new FileReader(nodeFile));
            nodeReader.readLine();

            String nodeLine;
            while ((nodeLine = nodeReader.readLine()) != null) {
                String[] nodeData = nodeLine.split("\\s+");
                int nodeId = Integer.parseInt(nodeData[0]);
                double lat = Double.parseDouble(nodeData[1]);
                double lon = Double.parseDouble(nodeData[2]);
                nodeCoordinates.put(nodeId, new NodeCoordinates(lat, lon));
                graph.put(nodeId, new ArrayList<>());
            }
            nodeReader.close();

            BufferedReader edgeReader = new BufferedReader(new FileReader(edgeFile));
            String line = edgeReader.readLine();

            String edgeLine;
            while ((edgeLine = edgeReader.readLine()) != null) {
                String[] edgeData = edgeLine.split("\\s+");
                int source = Integer.parseInt(edgeData[0]);
                int target = Integer.parseInt(edgeData[1]);
                int travelTime = Integer.parseInt(edgeData[2]);
                int length = Integer.parseInt(edgeData[3]);
                int speedLimit = Integer.parseInt(edgeData[4]);
                graph.get(source).add(new Edge(target, travelTime));
            }
            edgeReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void findShortestPath(int startNode, int endNode) {
        PriorityQueue<NodeDistance> minHeap = new PriorityQueue<>(Comparator.comparingInt(nd -> nd.distance));
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> parents = new HashMap<>();

        minHeap.add(new NodeDistance(startNode, 0));
        distances.put(startNode, 0);
        nodesSelected = 0;

        while (!minHeap.isEmpty()) {
            NodeDistance current = minHeap.poll();
            int currentNode = current.node;

            // Check if the current node is the endNode
            if (currentNode == endNode) {
                // Reconstruct the shortest path and calculate total travel time
                int node = endNode;
                while (node != startNode) {
                    shortestPath.add(node);
                    node = parents.get(node);
                }
                shortestPath.add(startNode);

                // Reverse the list to get the path from start to end
                Collections.reverse(shortestPath);

                // Calculate the total travel time based on the edges in the path
                for (int i = 0; i < shortestPath.size() - 1; i++) {
                    int currentNodeId = shortestPath.get(i);
                    int nextNodeId = shortestPath.get(i + 1);
                    for (Edge edge : graph.get(currentNodeId)) {
                        if (edge.target == nextNodeId) {
                            totalTravelTime += edge.weight;
                            break;
                        }
                    }
                }

                // No need to continue since the endNode has been reached
                break;
            }

            // Skip nodes with suboptimal distances
            if (current.distance > distances.get(currentNode)) {
                continue;
            }

            // Update distances and parents
            for (Edge edge : graph.get(currentNode)) {
                int neighborNode = edge.target;
                int newDistance = current.distance + edge.weight;

                if (newDistance < distances.getOrDefault(neighborNode, Integer.MAX_VALUE)) {
                    distances.put(neighborNode, newDistance);
                    parents.put(neighborNode, currentNode); // Store the parent node
                    minHeap.add(new NodeDistance(neighborNode, newDistance));
                }
            }

            nodesSelected++;
        }
    }

    public List<Integer> findNearestInterestPoints(int startNode, int numPoints, Map<Integer, Integer> interestPoints, int bitValueToSearch) {
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Boolean> visited = new HashMap<>();
        PriorityQueue<NodeDistance> minHeap = new PriorityQueue<>(Comparator.comparingInt(nd -> nd.distance));
        nodesSelected = 0;

        minHeap.add(new NodeDistance(startNode, 0));
        distances.put(startNode, 0);

        List<Integer> nearestInterestPoints = new ArrayList<>();

        while (!minHeap.isEmpty()) {
            NodeDistance current = minHeap.poll();
            int currentNode = current.node;
            nodesSelected++;

            if (visited.get(currentNode) != null && visited.get(currentNode)) {
                continue; // Skip nodes that have already been processed
            }

            visited.put(currentNode, true);

            if ((interestPoints.get(currentNode) != null) && ((interestPoints.get(currentNode) & bitValueToSearch) != 0) && (currentNode != startNode)) {
                nearestInterestPoints.add(currentNode);
            }

            if (nearestInterestPoints.size() >= numPoints) {
                break; // found the desired number of interest points
            }

            for (Edge edge : graph.get(currentNode)) {
                int neighborNode = edge.target;
                int newDistance = current.distance + edge.weight;

                if (newDistance < distances.getOrDefault(neighborNode, Integer.MAX_VALUE)) {
                    distances.put(neighborNode, newDistance);
                    minHeap.add(new NodeDistance(neighborNode, newDistance));
                }
            }
        }

        return nearestInterestPoints;
    }

    public void loadInterestPoints(String interestFile, Map<Integer, Integer> interestPoints) {
        try {
            BufferedReader interestReader = new BufferedReader(new FileReader(interestFile));

            String interestLine;
            while ((interestLine = interestReader.readLine()) != null) {
                String[] interestData = interestLine.split("\\s+");
                if (interestData.length >= 2) {
                    String nodeIdStr = interestData[0];
                    String bitValueStr = interestData[1];
                    if (!nodeIdStr.isEmpty() && !bitValueStr.isEmpty()) {
                        int nodeId = Integer.parseInt(nodeIdStr);
                        int bitValue = Integer.parseInt(bitValueStr);
                        interestPoints.put(nodeId, bitValue);
                    }
                }
            }

            interestReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Integer> getShortestPath() {
        return shortestPath;
    }

    public String getTotalTime() {
        totalTravelTime = totalTravelTime / 100;
        int hours = totalTravelTime / 3600;
        int minutes = (totalTravelTime % 3600) / 60;
        int seconds = totalTravelTime % 60;
        return "Time taken (hh:mm:ss): " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds";
    }

    public NodeCoordinates getNodeCoordinates(int nodeId) {
        return nodeCoordinates.get(nodeId);
    }

    public static void main(String[] args) {
        String nodeFile = "src\\noder.txt";
        String edgeFile = "src\\kanter.txt";
        String interestFile = "src\\interessepkt.txt";
        HashMap<Integer, Integer> interestPoints = new HashMap<>();

        Dijkstra dijkstra = new Dijkstra(nodeFile, edgeFile);
        dijkstra.loadInterestPoints(interestFile, interestPoints);

        // int startNode = 2266026; // insert start node
        // int numInterestPoints = 5; // number of nearest interest points to find
        // int bitValueToSearch = 4; // choose bit value to search for

        // long startTime = System.currentTimeMillis(); // Record start time

        // List<Integer> nearestPoints = dijkstra.findNearestInterestPoints(startNode, numInterestPoints, interestPoints, bitValueToSearch);

        // long endTime = System.currentTimeMillis(); // Record end time
        // long executionTime = endTime - startTime;

        // System.out.println("5 Nearest Charging Stations to Orkanger (Node 2266026):");
        // for (int point : nearestPoints) {
        //     System.out.println("Node " + point);
        // }
        // System.out.println("Number of nodes selected: " + dijkstra.nodesSelected);
        // System.out.println("Execution Time: " + executionTime + " ms");

        long startTime = System.currentTimeMillis();

        dijkstra.findShortestPath(5009309, 999080);

        long endTime = System.currentTimeMillis(); // Record end time
        long executionTime = endTime - startTime;

        List<Integer> shortestPath = dijkstra.getShortestPath();
        System.out.println(dijkstra.getTotalTime());

        if (!shortestPath.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("path.txt"))) {
                for (int nodeId : shortestPath) {
                    NodeCoordinates coordinates = dijkstra.getNodeCoordinates(nodeId);
                    writer.write(String.format("(%.7f, %.7f),%n", coordinates.getLat(), coordinates.getLon()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No path found.");
        }

        System.out.println("Number of nodes selected: " + dijkstra.nodesSelected);
        System.out.println("Time taken for execution: " + executionTime + " ms");
    }
}

class Edge {
    int target;
    int weight;

    Edge(int target, int weight) {
        this.target = target;
        this.weight = weight;
    }
}

class NodeDistance {
    int node;
    int distance;

    NodeDistance(int node, int distance) {
        this.node = node;
        this.distance = distance;
    }
}

class NodeCoordinates {
    private double lat;
    private double lon;

    public NodeCoordinates(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
