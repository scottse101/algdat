import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class Compressor {
    private Compressor() {}
    public static void main(String[] args) {
        try {
 /*//Alternatively, write names directly in the source code:
 String input = "";
 String output = "";
 compress(input, output);*/
            compress(args[0], args[1]);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Please enter the name of the file to compress, and name of the compressed file as arguments.");
        }
    }
    private static void compress(String inputFile, String outputFile) throws IOException {
        String input = Files.readString(Path.of("./" + inputFile));
        List<Integer> compressed = new ArrayList<>(input.length());
        int maxCharValue = 0;
        for (int i = 0; i < input.length(); i++) {
            maxCharValue = input.charAt(i) > maxCharValue ? input.charAt(i) : maxCharValue;
        }
        Dictionary dictionary = new Dictionary(input.length());
        //Creates the dictionary based on the highest found character value. This value will be stored first in the compressed file.
        for (int i = 0; i <= maxCharValue; i++) {
            StringBuilder ch = new StringBuilder();
            ch.append((char) i);
            dictionary.add(new DictEntry(ch, i));
        }
        int nextCode = maxCharValue + 1;
        StringBuilder newWord = new StringBuilder(128);
        StringBuilder previousWord = new StringBuilder(128);
        for (int i = 0; i < input.length(); i++) {
            char nextChar = input.charAt(i);
            newWord.append(nextChar);
            if (!dictionary.contains(newWord)) {
                compressed.add(dictionary.get(previousWord).code);
                DictEntry e = new DictEntry(newWord, nextCode++);
                dictionary.add(e);
                newWord = new StringBuilder(128);
                newWord.append(nextChar);
                previousWord = new StringBuilder(newWord);
            } else {
                previousWord.append(nextChar);
            }
        }
        compressed.add(dictionary.get(previousWord).code);
        try (DataOutputStream writer = new DataOutputStream(new FileOutputStream("./" + outputFile))) {
            writer.writeShort(maxCharValue);
            for (int i : compressed) {
                writer.writeShort(i);
            }
        }
    }
    private static class Dictionary {
        List<DictEntry> entries;
        public Dictionary(int length) {
            entries = new ArrayList<>(length);
        }
        public void add(DictEntry e) {
            int index = find(e.entry);
            index = (index + 1) * -1;
            entries.add(index, e);
        }
        public DictEntry get(StringBuilder s) {
            return entries.get(find(s));
        }
        public DictEntry entryOfCode(int code) {
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).code == code) {
                    return entries.get(i);
                }
            }
            return new DictEntry(null, -1);
        }
        public int find(StringBuilder s) {
            return Collections.binarySearch(entries, new DictEntry(s, -1), Comparator.comparing(e -> e.entry));
        }
        public boolean contains(StringBuilder s) {
            int index = find(s);
            return index > -1;
        }
    }
    private static class DictEntry {
        StringBuilder entry;
        int code;
        public DictEntry(StringBuilder entry, int code) {
            this.entry = entry;
            this.code = code;
        }
        @Override
        public boolean equals(Object o) {
            DictEntry o1 = (DictEntry) o;
            return this.entry.equals(o1.entry);
        }
    }
}