package org.example.ast;

public class AssignNode implements Node {
    private final String name;
    private final Node value;

    public AssignNode(String name, Node value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Node getValue() {
        return value;
    }
}
