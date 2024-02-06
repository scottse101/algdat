import java.io.File;
import java.util.Scanner;
public class Oving6TopoSort {
    public static void main(String[] args) {
        String filePath = "./ø6g7";
        Graph g = createGraph(filePath);
        Node node = topologicalSort(g);
        while(node != null) {
            System.out.printf("Node: %d%n", node.number);
            node = node.list.next;
        }
    }
    public static Graph createGraph(String filePath) {
        try (Scanner file = new Scanner(new File(filePath))) {
            int numberOfNodes = Integer.parseInt(file.nextLine().split(" ")[0]);
            Graph graph = new Graph(numberOfNodes);
            while(file.hasNext()) {
                String[] fromTo = file.nextLine().split(" ");
                int from = Integer.parseInt(fromTo[0]);
                int to = Integer.parseInt(fromTo[1]);
                graph.addEdge(from, to);
            }
            return graph;
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return null;
        }
    }
    public static Node dfsTopological(Node node, Node listHead) {
        TopologicalList nodeList = node.getTopoHead();
        if (nodeList.isFound()) return listHead;
        nodeList.setFound();
        for (Edge e = node.getFirstEdge(); e != null; e = e.next()) {
            listHead = dfsTopological(e.toNode, listHead);
        }
        nodeList.next = listHead;
        return node;
    }
    public static Node topologicalSort(Graph g) {
        Node[] node = g.nodes;
        Node listHead = null;
        for (int i = node.length; i-- > 0;) {
            node[i].list = new TopologicalList();
        }
        for (int i = node.length; i-- > 0;) {
            listHead = dfsTopological(node[i], listHead);
        }
        return listHead;
    }
    private static class TopologicalList {
        private boolean found;
        private Node next;
        public boolean isFound() {
            return found;
        }
        public void setFound() {
            found = true;
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

        //Creates two edges to allow navigation both ways.
        public void addEdge(int fromNode, int toNode) {
            nodes[fromNode].addEdge(nodes[toNode]);
        }
    }
    private static class Node {
        private Edge firstEdge;
        private int number;
        private TopologicalList list;
        public Node(int number, Edge firstEdge) {
            this.number = number;
            this.firstEdge = firstEdge;
        }
        public Edge getFirstEdge() {
            return firstEdge;
        }
        public void addEdge(Node to) {
            if (firstEdge == null) {
                firstEdge = new Edge(null, to);
            } else {
                Edge newEdge = new Edge(firstEdge, to);
                firstEdge = newEdge;
            }
        }
        public TopologicalList getTopoHead() {
            return list;
        }
    }
    private static class Edge {
        private Edge next;
        private Node toNode;
        public Edge(Edge next, Node toNode) {
            this.next = next;
            this.toNode = toNode;
        }
        public Edge next() {
            return next;
        }
        public Node getToNode() {
            return toNode;
        }
    }
}