public class Oving4Oppg1 {
    static class Node {
        int data;
        Node next;
        public Node(int data) {
            this.data = data;
            this.next = null;
        }
    }
    static class CircularLinkedList {
        Node head;
        Node startPerson;
        public void append(int data) {
            Node newNode = new Node(data);
            if (head == null) {
                head = newNode;
                head.next = head;
            } else {
                Node currentPerson = head;
                while (currentPerson.next != head) {
                    currentPerson = currentPerson.next;
                }
                currentPerson.next = newNode;
                newNode.next = head;
                startPerson = newNode;
            }
        }
        public void eliminateEveryNth(int m) {
            if (head == null || head.next == head) {
                System.out.println("No one to eliminate.");
                return;
            }
            Node currentPerson = startPerson;
            while (currentPerson.next != currentPerson) {
                for (int i = 1; i < m ; i++) {
                    currentPerson = currentPerson.next;
                }
                System.out.println("Eliminating: " + currentPerson.next.data);
                currentPerson.next = currentPerson.next.next;
            }
            System.out.println("Josephus should stand in position: " + currentPerson.data);
        }
    }
    public static void main(String[] args) {
        CircularLinkedList circle = new CircularLinkedList();
        int n = 10;
        int m = 4;
        for (int i = 1; i <= n; i++) {
            circle.append(i);
        }
        circle.eliminateEveryNth(m);
    }
}
