package org.example.ast;

import java.util.List;

public class SequenceNode implements Node {
    private final List<Node> expressions;

    public SequenceNode(List<Node> expressions) {
        this.expressions = List.copyOf(expressions);
    }

    public List<Node> getExpressions() {
        return expressions;
    }
}

