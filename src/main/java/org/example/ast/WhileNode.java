package org.example.ast;

public class WhileNode implements Node {
    private final Node condition;
    private final Node body;

    public WhileNode(Node condition, Node body) {
        this.condition = condition;
        this.body = body;
    }

    public Node getCondition() {
        return condition;
    }

    public Node getBody() {
        return body;
    }
}

