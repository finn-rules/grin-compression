package edu.grinnell.csc207.compression;

import java.util.Map;
import java.util.PriorityQueue;

/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally
 * take 8 bits. However, we also need to encode a special EOF character to
 * denote the end of a .grin file. Thus, we need 9 bits to store each
 * byte value. This is fine for file writing (modulo the need to write in
 * byte chunks to the file), but Java does not have a 9-bit data type.
 * Instead, we use the next larger primitive integral type, short, to store
 * our byte values.
 */
public class HuffmanTree {
    /**
     * The root of the Huffman tree.
     */
    private Node root;

    private PriorityQueue<Node> queue;

    /**
     * A node in the Huffman tree.
     */
    private class Node implements Comparable<Node> {
        private short value;
        private int frequency;
        private Node left;
        private Node right;

        /**
         * Constructs a new Node with the given value and frequency.
         * 
         * @param value     the value of this node
         * @param frequency the frequency of this node
         */
        public Node(short value, int frequency, Node left, Node right) {
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        public Node(short value, int frequency) {
            this.value = value;
            this.frequency = frequency;
            this.left = null;
            this.right = null;
        }

        @Override
        public int compareTo(Node node) {
            return this.frequency - node.frequency;
        }
    }

    /**
     * Constructs a new HuffmanTree from a frequency map.
     * 
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree(Map<Short, Integer> freqs) {
        freqs.put((short) 256, 1); // Add EOF manually. (Is this eof?)
        queue = new PriorityQueue<>();
        for (Map.Entry<Short, Integer> entry : freqs.entrySet()) {
            queue.add(new Node(entry.getKey(), entry.getValue()));
        }
        while (queue.size() > 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            Node newNode = new Node((short) 0, left.frequency + right.frequency, left, right);
            queue.add(newNode);
        }
        // Do something with the head here?
        root = queue.poll();
    }

    /**
     * Constructs a new HuffmanTree from the given file.
     * 
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree(BitInputStream in) {
        this.root = HuffmanInHelper(in);
    }

    public Node HuffmanInHelper(BitInputStream in) {
        short newValue = (short) in.readBit();
        if (newValue == 0) {
            short key = (short) in.readBit();
            return new Node(key, 1); // 1 or 0 frequency?
        } else {
            Node left = HuffmanInHelper(in);
            Node right = HuffmanInHelper(in); // preorder traversal
            return new Node((short) 0, left.frequency + right.frequency, left, right); // will this work?
        }
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * 
     * @param out the output file as a BitOutputStream
     */
    public void serialize(BitOutputStream out) { // useful for encode?
        serializeHelper(root, out);
    }

    private void serializeHelper(Node node, BitOutputStream out) {
        if (node == null) {
            return;
        }
        if (node.left == null && node.right == null) {
            out.writeBit(0); // ???
            out.writeBits(node.value, 8);
        } else {
            out.writeBit(1);
            serializeHelper(node.left, out);
            serializeHelper(node.right, out); // preorder traversal
        }
    }

    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * 
     * @param in  the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode(BitInputStream in, BitOutputStream out) {
        // TODO: fill me in!
        // run through it twice.
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * 
     * @param in  the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode(BitInputStream in, BitOutputStream out) {
        BitInputStream s = in;
        while (s.hasBits()) {
            int b = s.readBits(8);
            if (b == -1) {
                break; // EOF encountered
            }
            short byteValue = (short) b;
            out.writeBits(byteValue, 8);
        }
    }
}
