import java.io.File;
import java.io.IOException;
import java.util.Scanner;
class Oving4Oppg2 {
    public static void main(String[] args) {
        int lineNumber = 0;
        StackNode head = null;
        String filepath = "./testFile.java";
        try (Scanner reader = new Scanner(new File(filepath))){
            boolean syntaxIsCorrect = true;
            while(reader.hasNext() && syntaxIsCorrect) {
                lineNumber++;
                char[] nextLine = reader.nextLine().toCharArray();
                for (int i = 0; i < nextLine.length && syntaxIsCorrect; i++) {
                    switch (nextLine[i]) {
                        case '(' -> head = new StackNode(head, ')');
                        case ')' -> {
                            if (head.correctCloseCondition(')')) head = head.pop();
                            else syntaxIsCorrect = false;
                        }
                        case '{' -> head = new StackNode(head, '}');
                        case '}' -> {
                            if (head.correctCloseCondition('}')) head = head.pop();
                            else syntaxIsCorrect = false;
                        }
                        case '[' -> head = new StackNode(head, ']');
                        case ']' -> {
                            if (head.correctCloseCondition(']')) head = head.pop();
                            else syntaxIsCorrect = false;
                        }
                    }
                }
            }
            if (head == null) {
                System.out.println("Syntax for parentheses, brackets, and curly brackets is correct for this file.");
            } else {
                System.out.println("This file has an incorrect bracket/parentheses/curly bracket on line " + lineNumber);
            }
        } catch (IOException e) {
            System.out.println("An IO error occurred.");
        } catch(NullPointerException e) {
            System.out.println("Incorrect syntax at: " + lineNumber);
        }
    }
    private static class StackNode {
        private StackNode next;
        private char closeCondition;
        public StackNode(StackNode next, char closeCondition) {
            this.next = next;
            this.closeCondition = closeCondition;
        }
        public boolean correctCloseCondition(char condition) {
            return condition == closeCondition;
        }
        public StackNode pop() {
            StackNode temp = this.next;
            this.next = null;
            return temp;
        }
    }
}
