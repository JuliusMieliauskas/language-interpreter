package org.example.ast;

public class BinaryOpNode implements Node {
    private final Node left;
    private final Node right;
    private final String op;

    public BinaryOpNode(Node left, String op, Node right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public String getOp() {
        return op;
    }
}
