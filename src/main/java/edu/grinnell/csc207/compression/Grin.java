package edu.grinnell.csc207.compression;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

// If 0, go left in the tree, if 1, go right.

/**
 * The driver for the Grin compression program.
 */
public class Grin {
    /**
     * Decodes the .grin file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to decode
     * @param outfile the file to ouptut to
     * @throws IOException if the file cannot be read
     */
    public static void decode (String infile, String outfile) throws IOException {
        BitInputStream s = new BitInputStream(infile);
        BitOutputStream out = new BitOutputStream(outfile);
        Map<Short, Integer> frequencyMap = createFrequencyMap(infile);
        HuffmanTree hTree = new HuffmanTree(frequencyMap);
        hTree.decode(s, out);
    }

    /**
     * Creates a mapping from 8-bit sequences to number-of-occurrences of
     * those sequences in the given file. To do this, read the file using a
     * BitInputStream, consuming 8 bits at a time.
     * @param file the file to read
     * @return a freqency map for the given file
     * @throws IOException if the file cannot be read
     */
    public static Map<Short, Integer> createFrequencyMap (String file) throws IOException {
        BitInputStream s = new BitInputStream(file);
        Map<Short, Integer> frequencyMap = new java.util.HashMap<>();
        while (s.hasBits()) {
            int b = s.readBits(8);
            if(b == -1) {
                break; // EOF encountered
            }
            short byteValue = (short) b;
            frequencyMap.put(byteValue, frequencyMap.getOrDefault(byteValue, 0) + 1);
        } 
        return frequencyMap;
    }

    /**
     * Encodes the given file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * @param infile the file to encode.
     * @param outfile the file to write the output to.
     */
    public static void encode(String infile, String outfile) {
        // TODO: fill me in!
    }

    /**
     * The entry point to the program.
     * @param args the command-line arguments.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (args.length != 3) {
            System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");
            System.exit(1);
        }
        System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");
        String command = args[0];
        String infile = args[1];
        String outfile = args[2];
        if (command.equals("encode")) {
            encode(infile, outfile);
        } else if (command.equals("decode")) {
            try {
                decode(infile, outfile);
            } catch (IOException e) {
                System.err.println("Error decoding file: " + e.getMessage() + "\n" +
                                   "This may be because your file does not exist, is " + 
                                   "not the right format, or has an incorrect path.");
            }
        } else {
            System.out.println("Invalid command! : " + command);
        }
    }
}
