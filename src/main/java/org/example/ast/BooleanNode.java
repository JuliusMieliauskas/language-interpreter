package org.example.ast;

public class BooleanNode implements Node {
    private final boolean value;

    public BooleanNode(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}

