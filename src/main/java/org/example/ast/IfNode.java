package org.example.ast;

public class IfNode implements Node {
    private final Node condition;
    private final Node thenBranch;
    private final Node elseBranch;

    public IfNode(Node condition, Node thenBranch, Node elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Node getCondition() {
        return condition;
    }

    public Node getThenBranch() {
        return thenBranch;
    }

    public Node getElseBranch() {
        return elseBranch;
    }
}

