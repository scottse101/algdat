import java.io.File;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
public class EdmondsKarp {
    public static void main(String[] args) {
        Graph test = readGraphFromFile("./flytgraf1");
        int max = test.maxFlow(0, 7);
        System.out.println("Done! " + max);
    }
    public static Graph readGraphFromFile(String filePath) {
        try (Scanner file = new Scanner(new File(filePath))) {
            int numberOfNodes = Integer.parseInt(file.nextLine().split(" ")[0]);
            Graph graph = new Graph(numberOfNodes);
            while(file.hasNext()) {
                String[] fromTo = file.nextLine().strip().split("\\s+");
                int from = Integer.parseInt(fromTo[0]);
                int to = Integer.parseInt(fromTo[1]);
                int capacity = Integer.parseInt(fromTo[2]);
                graph.addEdge(from, to, capacity);
            }
            for (Node thisNode : graph.nodes) {
                for (Edge current = thisNode.getFirstEdge(); current != null; current = current.next()) {
                    Edge reverse = current.toNode.edgeTo(thisNode);
                    if (reverse == null) {
                        current.reverse = current.toNode.addEdge(thisNode, 0);
                        current.reverse.reverse = current; //Ensures that these two edges refer to each other.
                    } else {
                        current.reverse = reverse;
                    }
                }
            }
            return graph;
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }
    private static class Graph {
        private Node[] nodes;
        public Graph(int amountOfNodes) {
            nodes = new Node[amountOfNodes];
            for (int i = 0; i < amountOfNodes; i++) {
                nodes[i] = new Node(i, null);
            }
        }

        public void addEdge(int fromNode, int toNode, int capacity) {
            nodes[fromNode].addEdge(nodes[toNode], capacity);
        }
        public int maxFlow(int src, int sink) {
            while (increaseFlow(src, sink));
            int maxFlow = 0;
            for (Edge e = nodes[src].firstEdge; e != null; e = e.next) {
                maxFlow += e.flow;
            }
            return maxFlow;
        }
        public boolean increaseFlow(int src, int sink) {
            int lowestRestCap = Integer.MAX_VALUE;
            if (!getAugmentPath(src, sink)) return false; //There are no more augment paths
            for (Node current = nodes[sink]; current.data.previous != null; current = current.data.previous) {
                if (current.number == src) break;
                int rest = current.data.previous.edgeTo(current).getRestCapacity();
                if (rest < lowestRestCap) {
                    lowestRestCap = rest;
                }
            }
            System.out.printf("Økning: %d // ", lowestRestCap);
            String pathToSink = "";
            for (Node current = nodes[sink]; current.data.previous != null; current = current.data.previous) {
                Edge e = current.data.previous.edgeTo(current);
                if (e == null) break;
                e.flow += lowestRestCap;
                e.reverse.capacity += lowestRestCap;
                pathToSink = current.number + " " + pathToSink;
            }
            pathToSink = src + " " + pathToSink;
            System.out.printf("%s%n", pathToSink);
            return true;
        }
        private void initBFS(int src) {
            for (Node n : nodes) {
                n.data = new BFSData();
            }
            nodes[src].data.previous = new Node();
        }
        public boolean getAugmentPath(int strt, int sink) {
            initBFS(strt);
            Node start = nodes[strt];
            Queue<Node> queue = new LinkedList<>();
            queue.add(start);
            while (!queue.isEmpty()) {
                Node n = queue.poll();
                for (Edge e = n.firstEdge; e != null; e = e.next) {
                    if (e.getRestCapacity() == 0) continue;
                    if (e.toNode.data.previous == null) { //Node is unvisited, and is added to queue
                        e.toNode.data.previous = n;
                        queue.add(e.toNode);
                    }
                    if (e.toNode.number == sink) {
                        return true; //BFS has reached sink, and is done
                    }
                }
            }
            return false; //Sink has not been reached, there is no augment path
        }
    }
    private static class Node {
        private Edge firstEdge;
        private int number;
        private BFSData data;
        public Node() { //Creates dummy node
            firstEdge = null;
            number = -1;
            data = null;
        }
        public Node(int number, Edge firstEdge) {
            this.number = number;
            this.firstEdge = firstEdge;
        }
        public Edge getFirstEdge() {
            return firstEdge;
        }
        public Edge addEdge(Node to, int capacity) {
            Edge newEdge = new Edge(firstEdge, to, capacity);
            firstEdge = newEdge;
            return firstEdge;
        }
        public Edge edgeTo(Node to) {
            for (Edge current = firstEdge; current != null; current = current.next()) {
                if (current.toNode.number == to.number) {
                    return current;
                }
            }
            return null;
        }
    }
    private static class Edge {
        private Edge next;
        private Node toNode;
        private int capacity;
        private int flow;
        private Edge reverse;
        public Edge(Edge next, Node toNode, int capacity) {
            this.next = next;
            this.toNode = toNode;
            this.capacity = capacity;
            flow = 0;
        }
        public Edge next() {
            return next;
        }
        public int getRestCapacity() {
            return capacity - flow;
        }
    }
    private static class BFSData {
        private Node previous;
    }
}