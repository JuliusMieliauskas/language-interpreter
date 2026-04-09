package org.example.ast;

public class BinaryOpNode implements Node {
    private Node left;
    private Node right;
    private String op;

    public BinaryOpNode(Node left, String op, Node right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }
}
