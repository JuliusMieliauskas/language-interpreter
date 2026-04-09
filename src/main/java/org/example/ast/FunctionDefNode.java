package org.example.ast;

import java.util.List;

public class FunctionDefNode implements Node {
    private final String name;
    private final List<String> params;
    private final Node body;

    public FunctionDefNode(String name, List<String> params, Node body) {
        this.name = name;
        this.params = List.copyOf(params);
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<String> getParams() {
        return params;
    }

    public Node getBody() {
        return body;
    }
}

