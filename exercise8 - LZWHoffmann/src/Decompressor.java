import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
public class Decompressor {
    public static void main(String[] args) {
        try {
 /*//Alternatively, write names directly in the source code:
 String input = "";
 String output = "";
 decompress(input, output);*/
            decompress(args[0], args[1]);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Please enter the name of the file to decompress, and name of the output file as arguments.");
        }
    }
    private static void decompress(String inputFile, String outputFile) throws IOException {
        int maxCharValue;
        List<Integer> codes = new ArrayList<>();
        try(DataInputStream s = new DataInputStream(new FileInputStream("./" + inputFile))) {
            maxCharValue = s.readShort();
            while (s.available() > 0) {
                codes.add((int) s.readShort());
            }
        }
        Dictionary dictionary = new Dictionary(maxCharValue);
        //Creates the dictionary based on the highest found character value. This value will be stored first in the compressed file.
        for (int i = 0; i <= maxCharValue; i++) {
            StringBuilder ch = new StringBuilder();
            ch.append((char) i);
            dictionary.add(new DictEntry(ch, i));
        }
        StringBuilder decompressedText = new StringBuilder();
        decompressedText.append(dictionary.entryOfCode(codes.get(0)).entry);
        int nextCodeNumber = maxCharValue + 1;
        StringBuilder previous = new StringBuilder(decompressedText);
        //Iterate over all the codes, only stopping to add new entries to the dictionary when an unknown code is found
        for (int i = 1; i < codes.size(); i++) {
            StringBuilder nextCode = dictionary.entryOfCode(codes.get(i)).entry;
            if (nextCode != null) { //nextText is in the dictionary
                dictionary.add(new DictEntry(new StringBuilder(previous.toString() + nextCode.charAt(0)), nextCodeNumber++));
                previous = new StringBuilder(nextCode);
                decompressedText.append(nextCode);
            } else { //nextText is not in the dictionary
                previous.append(previous.charAt(0));
                dictionary.add(new DictEntry(new StringBuilder(previous), nextCodeNumber++));
                decompressedText.append(previous);
            }
        }
        try (FileWriter writer = new FileWriter("./" + outputFile)) {
            writer.write(decompressedText.toString());
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