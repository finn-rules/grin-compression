package edu.grinnell.csc207.compression;

import java.util.Map;
import java.util.PriorityQueue;

// import javax.swing.tree.AbstractLayoutCache.NodeDimensions;

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
         * Constructs a new Node with the given value and frequency. Includes a left and right node.
         * 
         * @param value     the value of this node
         * @param frequency the frequency of this node
         * @param left a left node
         * @param right a right node.
         */
        public Node(short value, int frequency, Node left, Node right) {
            this.value = 300;
            this.frequency = frequency;
            this.left = left;
            this.right = right;
        }

        /**
         * Creates a leaf node with null left and right trees. These store our characters.
         * @param value : the character, as a short.
         * @param frequency : how often this character occurs
         */
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
        this.root = huffmanInHelper(in);
    }

    /**
     * HuffmanInhelper : a recursive function to create a huffman tree from an input file.
     * @param in : the input file as a bitStream
     * @return : a node, ideally recursion ultimately makes this the loop.
     */
    public Node huffmanInHelper(BitInputStream in) {
        short newValue = (short) in.readBit();
        System.out.println("Recursive call : comparing " + newValue);
        if (newValue == 0) {
            short key = (short) in.readBit();
            return new Node(key, 1); // 1 or 0 frequency?
        } else {
            System.out.println("Left attempt");
            Node left = huffmanInHelper(in);
            System.out.println("Right attempt");
            Node right = huffmanInHelper(in); // preorder traversal
            System.out.println("Total frequency : " + left.frequency + right.frequency);
            return new Node((short) 1, left.frequency + right.frequency, left, right); 
            // will this work? should I use 1?
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
            out.writeBits(node.value, 9); // 8 or 9?
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
        serialize(out);
        // need to do two things. keep track of characters somehow?
        // need traversal and valid encoding.
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
        Node temp = root;
        while (temp.value != 256) {
            if (temp.value == 0) {
                out.writeBits(temp.value, 8);
                temp = root;
            } else {
                int bit = in.readBit();
                if (bit == 0) {
                    temp = temp.left;
                } else if (bit == 1) {
                    temp = temp.right;
                }
            }
        }
    }
}
