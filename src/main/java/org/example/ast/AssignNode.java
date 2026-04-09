package org.example.ast;

public class AssignNode implements Node {
    String name;
    Node value;

    public AssignNode(String name, Node value) {
        this.name = name;
        this.value = value;
    }
}
