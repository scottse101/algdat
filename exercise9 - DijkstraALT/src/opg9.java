import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Scanner;
public class opg9 {
    public static void main(String[] args) {
        System.out.println("Started reading data from files...");
        int[][] toNodeFromLandmark;
        int[][] fromNodeToLandmark;
        System.out.println("Started reading graph...");
        Graph graph = createGraph("./noder.txt", "./kanter.txt");
        System.out.println("Done reading!");
        if (!new File("./reverse.txt").exists()) {
            System.out.println("Creating reverse graph...");
            createReverseGraph("kanter.txt", "reverse.txt");
            System.out.println("Done!");
        }
        System.out.println("Reading reverse graph...");
        Graph reverse = createGraph("./noder.txt", "./reverse.txt");
        System.out.println("Done reading reverse graph!");
        System.out.println("Preprocessing...");
        toNodeFromLandmark = preprocessing(graph);
        fromNodeToLandmark = preprocessing(reverse);
        reverse = null;
        readInterestPointsFromFile("interessepkt.txt", graph);
        System.out.println("Preprocessing done!");
        System.out.println("Reading completed.");
        //Input node numbers for travel path here
        int fromNode = 2266026; //Orkanger
        int toNode = 7826348; //Trondheim
        Date start = new Date();
        Node n = alt(graph.nodes[fromNode], graph.nodes[toNode], toNodeFromLandmark, fromNodeToLandmark);
        int dist1 = n.data.distance;
        resetNodeData(graph);
        Date slutt = new Date();
        System.out.println("ALT search took " + (slutt.getTime() - start.getTime()) + " ms. " + getTotalTime(dist1));

        start = new Date();
        Node m = dijkstra(graph.nodes[fromNode], graph.nodes[toNode]);
        int dist2 = m.data.distance;
        resetNodeData(graph);
        slutt = new Date();
        System.out.println("Dijkstra search took " + (slutt.getTime() - start.getTime()) + " ms. " + getTotalTime(dist2));
        //Input node number and code for interest point here.
 /* int fromNode = 2266026;
 int code = 4;

 InterestPoint[] nearestPointsOfInterest = findPointsOfInterest(graph.nodes[fromNode], code);
 resetNodeData(graph);
 for (InterestPoint p : nearestPointsOfInterest) {
 System.out.println(p.name);
 } */
    }
    public static String getTotalTime(int totalTravelTime) {
        totalTravelTime = totalTravelTime / 100;
        int hours = totalTravelTime / 3600;
        int minutes = (totalTravelTime % 3600) / 60;
        int seconds = totalTravelTime % 60;
        return "Time taken (hh:mm:ss): " + hours + " hours, " + minutes + " minutes, " + seconds + " seconds";
    }
    public static Node alt(Node from, Node to, int[][] toNode, int[][] fromNode) {
        PriorityHeap queue = new PriorityHeap();
        from.data.distance = 0;
        queue.add(from);
        int nodesChecked = 0;
        while(!queue.isEmpty()) {
            Node current = queue.poll();
            nodesChecked++;
            current.data.isChecked = true;
            if (current == to) {
                System.out.println(nodesChecked + " nodes checked in ALT search.");
                return current;
            }
            for(Edge e = current.firstEdge; e != null; e = e.next) {
                if (!e.toNode.data.isChecked && e.toNode.data.distance > current.data.distance + e.weight) {
                    NodeData d = e.toNode.data;
                    d.distance = current.data.distance + e.weight;
                    d.previousNode = current;
                    d.priority = d.distance + d.distanceToTarget;
                    if(d.heapIndex < 0) {
                        //Estimate distance to target
                        for (int i = 0; i < toNode[0].length; i++) {
                            int estimate = toNode[to.number][i] - toNode[e.toNode.number][i];
                            estimate = estimate < 0 ? 0 : estimate;
                            int estimate2 = fromNode[e.toNode.number][i] - fromNode[to.number][i];
                            estimate = estimate2 > estimate ? estimate2 : estimate;
                            d.distanceToTarget = estimate > d.distanceToTarget ? estimate : d.distanceToTarget;
                        }
                        d.priority = d.distance + d.distanceToTarget;
                        queue.add(e.toNode);
                    }
                    else queue.updatePosition(e.toNode);
                }
            }
        }
        return null;
    }
    public static Node dijkstra(Node from, Node to) {
        PriorityHeap queue = new PriorityHeap();
        from.data.distance = 0;
        queue.add(from);
        int nodesChecked = 0;
        while(!queue.isEmpty()) {
            Node current = queue.poll();
            nodesChecked++;
            current.data.isChecked = true;
            if (current == to) {
                System.out.println(nodesChecked + " nodes checked in dijkstra search.");
                return current;
            }
            for(Edge e = current.firstEdge; e != null; e = e.next) {
                if (!e.toNode.data.isChecked && e.toNode.data.distance > current.data.distance + e.weight) {
                    NodeData d = e.toNode.data;
                    d.distance = current.data.distance + e.weight;
                    d.previousNode = current;
                    d.priority = d.distance;
                    if(d.heapIndex < 0) queue.add(e.toNode);
                    else queue.updatePosition(e.toNode);
                }
            }
        }
        return null;
    }
    static InterestPoint[] findPointsOfInterest(Node start, int code) {
        InterestPoint[] points = new InterestPoint[5];
        int pointsI = 0;
        PriorityHeap queue = new PriorityHeap();
        start.data.distance = 0;
        queue.add(start);
        int nodesChecked = 0;
        while(!queue.isEmpty()) {
            Node current = queue.poll();
            nodesChecked++;
            current.data.isChecked = true;

            if (current.intPointNext != null && current != start) {//There are interest points at this location
                InterestPoint intPoint = current.intPointNext;
                while (intPoint != null) {
                    if ((intPoint.code & code) == code) {
                        points[pointsI++] = intPoint;
                    }
                    if (pointsI == 5) {
                        System.out.println("Checked " + nodesChecked + " nodes to find nearest points of interest.");
                        return points;
                    }
                    intPoint = intPoint.next;
                }
            }
            for(Edge e = current.firstEdge; e != null; e = e.next) {
                if (!e.toNode.data.isChecked && e.toNode.data.distance > current.data.distance + e.weight) {
                    NodeData d = e.toNode.data;
                    d.distance = current.data.distance + e.weight;
                    d.previousNode = current;
                    d.priority = d.distance;
                    if(d.heapIndex < 0) queue.add(e.toNode);
                    else queue.updatePosition(e.toNode);
                }
            }
        }
        return points;
    }
    private static void dijkstraLandmark(Node from) {
        PriorityHeap queue = new PriorityHeap();
        from.data.distance = 0;
        queue.add(from);
        while(!queue.isEmpty()) {
            Node current = queue.poll();
            current.data.isChecked = true;
            for(Edge e = current.firstEdge; e != null; e = e.next) {
                if (!e.toNode.data.isChecked && e.toNode.data.distance > current.data.distance + e.weight) {
                    NodeData d = e.toNode.data;
                    d.distance = current.data.distance + e.weight;
                    d.priority = d.distance;
                    if(d.heapIndex < 0) queue.add(e.toNode);
                    else queue.updatePosition(e.toNode);
                }
            }
        }
    }
    private static void createReverseGraph(String edgeFile, String reverseFile) {
        try(Scanner reader = new Scanner(new File("./" + edgeFile));
            FileWriter writer = new FileWriter("./" + reverseFile)) {
            writer.write(reader.nextLine() + "\n");
            while (reader.hasNext()) {
                String[] nextLine = reader.nextLine().split("\\s+");
                writer.write(nextLine[1] + " " + nextLine[0] + " " + nextLine[2] + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static int[][] preprocessing(Graph graph) {
        int[] landmarks = {925129, 5872365, 5542364, 2531818}; //Helsinki, Finland | Bov, Denmark | Bergen, Norway | Nordkapp, Norway
        int[][] table = new int[graph.amountOfNodes][landmarks.length];
        resetNodeData(graph);
        for (int i = 0; i < landmarks.length; i++) {
            dijkstraLandmark(graph.nodes[landmarks[i]]);
            for (int j = 0; j < graph.amountOfNodes; j++) {
                table[j][i] = graph.nodes[j].data.distance;
            }
            resetNodeData(graph);
        }
        return table;
    }
    private static void resetNodeData(Graph g) {
        for (int j = 0; j < g.amountOfNodes; j++) {
            g.nodes[j].data.distance = Integer.MAX_VALUE;
            g.nodes[j].data.previousNode = null;
            g.nodes[j].data.heapIndex = -1;
            g.nodes[j].data.isChecked = false;
            g.nodes[j].data.priority = -1;
            g.nodes[j].data.distanceToTarget = -1;
        }
    }
    public static Graph createGraph(String nodesPath, String edgesPath) {
        try (Scanner file = new Scanner(new File(nodesPath));
             Scanner file2 = new Scanner(new File(edgesPath))) {
            int numberOfNodes = Integer.parseInt(file.nextLine().strip());
            Graph graph = new Graph(numberOfNodes);
            while(file.hasNext()) {
                String[] fromTo = file.nextLine().split(" +");
                int number = Integer.parseInt(fromTo[0]);
                graph.nodes[number] = new Node(number, null);
                graph.nodes[number].breddegrad = Double.parseDouble(fromTo[1]);
                graph.nodes[number].lengdegrad = Double.parseDouble(fromTo[2]);
            }
            file2.nextLine();
            while(file2.hasNext()) {
                String[] edge = file2.nextLine().split("\\s+");
                int fromNode = Integer.parseInt(edge[0]);
                int toNode = Integer.parseInt(edge[1]);
                int weight = Integer.parseInt(edge[2]);
                graph.nodes[fromNode].addEdge(graph.nodes[toNode], weight);
            }
            return graph;
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }
    public static void writeTableToFile(int[][] table, String filename) throws IOException {
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream("./" + filename))) {
            writer.writeInt(table.length);
            writer.writeInt(table[0].length);
            for (int i = 0; i < table.length; i++) {
                for (int j = 0; j < table[0].length; j++) {
                    writer.writeInt(table[i][j]);
                }
            }
        }
    }
    public static int[][] readTableFromFile(String filename) throws IOException {
        int[][] t;
        try (DataInputStream reader = new DataInputStream(new FileInputStream("./" + filename))) {
            int nodes = reader.readInt();
            int landmarks = reader.readInt();
            t = new int[nodes][landmarks];
            for (int i = 0; i < nodes; i++) {
                for (int j = 0; j < landmarks; j++) {
                    t[i][j] = reader.readInt();
                }
            }
        }
        return t;
    }
    static void readInterestPointsFromFile(String filename, Graph g) {
        try (Scanner reader = new Scanner(new File("./" + filename))) {
            reader.nextLine();
            while (reader.hasNext()) {
                String nextLine = reader.nextLine();
                String[] values = nextLine.split("\\s+");
                int nodeNumber = Integer.parseInt(values[0]);
                int code = Integer.parseInt(values[1]);
                StringBuilder nameB = new StringBuilder();
                for (int i = 2; i < values.length; i++) {
                    nameB.append(values[i]);
                }
                String name = nameB.toString();

                if (g.nodes[nodeNumber].intPointNext == null) {
                    g.nodes[nodeNumber].intPointNext = new InterestPoint();
                    g.nodes[nodeNumber].intPointNext.code = code;
                    g.nodes[nodeNumber].intPointNext.name = name;
                } else {
                    InterestPoint newHead = new InterestPoint();
                    newHead.name = name;
                    newHead.code = code;
                    newHead.next = g.nodes[nodeNumber].intPointNext;
                    g.nodes[nodeNumber].intPointNext = newHead;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Graph {
        private Node[] nodes;
        int amountOfNodes;
        public Graph(int amountOfNodes) {
            nodes = new Node[amountOfNodes];
            this.amountOfNodes = amountOfNodes;
        }
    }
    private static class Node {
        private Edge firstEdge;
        private int number;
        private String name;
        private NodeData data;
        private InterestPoint intPointNext;
        double breddegrad;
        double lengdegrad;
        public Node(int number, Edge firstEdge) {
            this.number = number;
            this.firstEdge = firstEdge;
            this.name = "";
            data = new NodeData();
            data.distance = Integer.MAX_VALUE;
            data.distanceToTarget = -1;
            data.heapIndex = -1;
            data.isChecked = false;
        }
        public Edge getFirstEdge() {
            return firstEdge;
        }
        public void addEdge(Node to, int weight) {
            firstEdge = new Edge(firstEdge, to, weight);
        }
    }
    private static class Edge {
        private Edge next;
        private int weight;
        private Node toNode;
        public Edge(Edge next, Node toNode, int weight) {
            this.next = next;
            this.toNode = toNode;
            this.weight = weight;
        }
        public Edge next() {
            return next;
        }
        public Node getToNode() {
            return toNode;
        }
    }
    private static class NodeData {
        int distance;
        int priority;
        int distanceToTarget;
        Node previousNode;
        int heapIndex;
        boolean isChecked;
        public NodeData() {
            heapIndex = -1;
            isChecked = false;
        }
    }
    private static class PriorityHeap {
        int arrayLength;
        int heapLength;
        Node[] nodes;
        public PriorityHeap() {
            arrayLength = 524288;
            nodes = new Node[arrayLength];
        }
        public void updateHeap(int i) {
            int m = left(i);
            if (m + 1 > arrayLength) {
                increaseSize();
            }
            if (m < heapLength) {
                int r = m + 1;
                if (r < heapLength && nodes[r].data.priority < nodes[m].data.priority) {
                    m = r;
                }
                if (nodes[m].data.priority < nodes[i].data.priority) {
                    Node temp = nodes[i];
                    nodes[i] = nodes[m];
                    nodes[i].data.heapIndex = i;
                    nodes[m] = temp;
                    nodes[m].data.heapIndex = m;
                    updateHeap(m);
                }
            }
        }
        public Node poll() {
            Node next = nodes[0];
            nodes[0] = nodes[--heapLength];
            updateHeap(0);
            return next;
        }
        public void add(Node node) {
            int i = heapLength++;
            nodes[i] = node;
            node.data.heapIndex = i;
            updatePosition(node);
        }
        public void updatePosition(Node node) {
            int parent;
            int i = node.data.heapIndex;
            while (i > 0 && nodes[i].data.priority < nodes[parent = over(i)].data.priority) {
                Node temp = nodes[i];
                nodes[i] = nodes[parent];
                nodes[i].data.heapIndex = i;
                nodes[parent] = temp;
                nodes[parent].data.heapIndex = parent;
                i = parent;
            }
        }
        private int over(int i) {
            return (i - 1) >> 1;
        }
        private int left(int i) {
            return (i << 1) + 1;
        }
        private int right(int i) {
            return (i + 1) << 1;
        }
        private void increaseSize() {
            arrayLength *= 2;
            Node[] newNodes = new Node[arrayLength];
            for (int i = 0; i < nodes.length; i++) {
                newNodes[i] = nodes[i];
            }
            nodes = newNodes;
        }
        public boolean isEmpty() {
            return heapLength <= 0;
        }
    }
    private static class InterestPoint {
        InterestPoint next;
        int code;
        String name;
    }

}
