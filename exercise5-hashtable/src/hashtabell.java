import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
public class hashtabell {
    public static void main(String[] args) {
        String filepath = "navn.txt";
        try (Scanner reader = new Scanner(new File(filepath))) {
            HashTable table = new HashTable();
            int totalCollisions = 0;
            int totalInsertions = 0;
            while (reader.hasNextLine()) {
                String name = reader.nextLine();
                int collisions = table.put(name);
                totalCollisions += collisions;
                totalInsertions++;
            }
            double loadFactor = (double) totalInsertions / table.getSize();
            System.out.println("Load Factor: " + loadFactor);
            System.out.println("Total Collisions: " + totalCollisions);
            System.out.println("Average Collisions per person: " + (totalCollisions / (double) totalInsertions));
            System.out.println("The person \"Emil Johnsen\" is in the list: " + table.lookup("Emil Johnsen"));
            System.out.println("The person \"Svein Kåre Sørestad\" is in the list: " + table.lookup("Svein Kåre Sørestad"));
            System.out.println("The person \"Zahid André Kristiansen\" is in the list: " + table.lookup("Zahid André Kristiansen"));
            System.out.println("The person \"Scott Sanya Emonanekkul\" is in the list: " + table.lookup("Scott Sanya Emonanekkul"));
            System.out.println("The person \"Michael Jordan\" is in the list: " + table.lookup("Michael"
                    + " Jordan"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private static class HashTable {
        private LinkedStringList[] table;
        private int size;
        public HashTable() {
            size = 150;
            table = new LinkedStringList[size];
        }
        public int put(String name) {
            int hash = hash(name);
            if (table[hash] == null) {
                table[hash] = new LinkedStringList();
                table[hash].addInFront(name);
                return 0;
            } else {
                System.out.println("Collision between: " + name + " and " + table[hash].findHead().text);
                table[hash].addInFront(name);
                return 1;
            }
        }
        public int getSize() {
            return size;
        }
        private int hash(String name) {
            int hash = 0;
            int m = table.length;
            for (int i = 0; i < name.length(); i++) {
                hash = (7 * hash + name.charAt(i)) % m;
            }
            return hash;
        }
        private boolean lookup(String name){
            int index = hash(name);
            if (table[index] == null){
                return false;
            } else {
                hashtabell.LinkedStringList.Node currentNode = table[index].findHead();
                while (currentNode != null){
                    if (currentNode.text.equals(name)){
                        return true;
                    }
                    currentNode = currentNode.next;
                }
            }
            return false;
        }
    }
    private static class LinkedStringList {
        private Node head = null;
        private int amountElements = 0;
        public Node findHead() {
            return head;
        }
        public void addInFront (String value) {
            Node temp = head;
            head = new Node(value);
            head.next = temp;
            amountElements++;
        }
        private static class Node {
            private Node next;
            private String text;
            public Node(String text) {
                this.text = text;
            }
        }
    }
}