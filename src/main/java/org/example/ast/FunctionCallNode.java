package org.example.ast;

import java.util.List;

public class FunctionCallNode implements Node {
    private final String name;
    private final List<Node> arguments;

    public FunctionCallNode(String name, List<Node> arguments) {
        this.name = name;
        this.arguments = List.copyOf(arguments);
    }

    public String getName() {
        return name;
    }

    public List<Node> getArguments() {
        return arguments;
    }
}

