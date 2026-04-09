package org.example.eval;

import org.example.ast.AssignNode;
import org.example.ast.BinaryOpNode;
import org.example.ast.Node;
import org.example.ast.NumberNode;
import org.example.ast.VariableNode;

import java.util.Map;

public class Evaluator {

	public Map<String, Integer> evaluate(Node node) {
		Environment environment = new Environment();
		evaluate(node, environment);
		return environment.snapshot();
	}

	private int evaluate(Node node, Environment environment) {
		if (node instanceof NumberNode numberNode) {
			return numberNode.getValue();
		}

		if (node instanceof VariableNode variableNode) {
			return environment.get(variableNode.getName());
		}

		if (node instanceof AssignNode assignNode) {
			int value = evaluate(assignNode.getValue(), environment);
			environment.set(assignNode.getName(), value);
			return value;
		}

		if (node instanceof BinaryOpNode binaryOpNode) {
			int left = evaluate(binaryOpNode.getLeft(), environment);
			int right = evaluate(binaryOpNode.getRight(), environment);
			return switch (binaryOpNode.getOp()) {
				case "+" -> left + right;
				case "-" -> left - right;
				case "*" -> left * right;
				case "/" -> left / right;
				default -> throw new IllegalArgumentException("Unsupported operator '" + binaryOpNode.getOp() + "'.");
			};
		}

		throw new IllegalArgumentException("Unsupported node type: " + node.getClass().getSimpleName());
	}
}
