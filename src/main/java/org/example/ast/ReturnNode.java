package org.example.ast;

public class ReturnNode implements Node {
    private final Node value;

    public ReturnNode(Node value) {
        this.value = value;
    }

    public Node getValue() {
        return value;
    }
}

