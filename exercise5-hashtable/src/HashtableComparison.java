import java.util.*;
public class HashtableComparison {
    private long size;
    private int doubleHashTable[];
    private int linearProbingTable[];
    private int doubleHashCollisions;
    private int linearProbingCollisions;
    public HashtableComparison(long size) {
        this.size = size;
        doubleHashTable = new int[(int)size];
        linearProbingTable = new int[(int)size];
        doubleHashCollisions = 0;
        linearProbingCollisions = 0;
    }
    private int linearProbingHash(int key){
        return key % linearProbingTable.length;
    }
    private int doubleHash(int key) {
        int hash1 = key % doubleHashTable.length;
        int hash2 = 1 + (key % (doubleHashTable.length - 1));
        return (hash1 + hash2) % doubleHashTable.length;
    }
    public void insertDoubleHash(int value) {
        int index = doubleHash(value);
        int hashJump = 1 + (value % (doubleHashTable.length - 1));
        while( doubleHashTable[index] != 0 ) {
            index = ((index + hashJump) % doubleHashTable.length);
            doubleHashCollisions++;
        }
        doubleHashTable[index] = value;
    }
    public void insertLinearProbing (int value) {
        int index = linearProbingHash(value);
        for (int i = 1; linearProbingTable[index] != 0; i++) {
            index = ((index + i ) % linearProbingTable.length);
            linearProbingCollisions++;
        }
        linearProbingTable[index] = value;
    }
    public int getDoubleHashCollisions() {
        return doubleHashCollisions;
    }
    public int getLinearProbingCollisions() {
        return linearProbingCollisions;
    }
    public static List<Integer> generateRandomList(int numElements, int maxValue, double percentage) {
        List<Integer> randomNumbers = new ArrayList<>();
        Random r = new Random();
        numElements = (int) (numElements * (percentage*0.01));
        for (int i = 0; i < numElements; i++) {
            randomNumbers.add(r.nextInt(maxValue));
        }
        return randomNumbers;
    }
    public static void main(String[] args) {
        long size = 14285983;
        int numElements = (int) Math.pow(10, 7);
        List<Double> percentages = new ArrayList<>(Arrays.asList(50.0, 80.0, 90.0, 99.0, 100.0));
        for (double percentage : percentages) {
            HashtableComparison hashTable = new HashtableComparison(size);
            List<Integer> randomNumbers = generateRandomList(numElements, (int) size, percentage);
            long startTimeDoubleHash = System.nanoTime();
            for (int randomValue : randomNumbers) {
                hashTable.insertDoubleHash(randomValue);
            }
            long endTimeDoubleHash = System.nanoTime();
            long durationDoubleHash = (endTimeDoubleHash - startTimeDoubleHash) / 1_000_000; // Convert nanoseconds to milliseconds
            long startTimeLinearProbing = System.nanoTime();
            for (int randomValue : randomNumbers) {
                hashTable.insertLinearProbing(randomValue);
            }
            long endTimeLinearProbing = System.nanoTime();
            long durationLinearProbing = (endTimeLinearProbing - startTimeLinearProbing) / 1_000_000; // Convert nanoseconds to milliseconds
            System.out.println("Percentage: " + percentage + "\n");
            System.out.println("Double hash collisions: " + hashTable.getDoubleHashCollisions());
            System.out.println("Time taken for Double Hashing (ms): " + durationDoubleHash + "\n");
            System.out.println("Linear probing collisions: " + hashTable.getLinearProbingCollisions());
            System.out.println("Time taken for Linear Probing (ms): " + durationLinearProbing);
            System.out.println("--------------------------");
        }
    }
}