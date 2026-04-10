package org.example.eval;

import org.example.ast.BooleanNode;
import org.example.ast.AssignNode;
import org.example.ast.BinaryOpNode;
import org.example.ast.FunctionCallNode;
import org.example.ast.FunctionDefNode;
import org.example.ast.IfNode;
import org.example.ast.Node;
import org.example.ast.NumberNode;
import org.example.ast.UnaryOpNode;
import org.example.ast.ReturnNode;
import org.example.ast.SequenceNode;
import org.example.ast.VariableNode;
import org.example.ast.WhileNode;

import java.util.Map;

public class Evaluator {

	public Map<String, Integer> evaluate(Node node) {
		Environment environment = new Environment();
		evaluate(node, environment);
		return environment.snapshot();
	}

	private int evaluate(Node node, Environment environment) {
		if (node instanceof SequenceNode sequenceNode) {
			int value = 0;
			for (Node expression : sequenceNode.getExpressions()) {
				value = evaluate(expression, environment);
			}
			return value;
		}

		if (node instanceof NumberNode numberNode) {
			return numberNode.getValue();
		}

		if (node instanceof BooleanNode booleanNode) {
			return booleanNode.getValue() ? 1 : 0;
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
				case "==" -> bool(left == right);
				case "!=" -> bool(left != right);
				case "<" -> bool(left < right);
				case "<=" -> bool(left <= right);
				case ">" -> bool(left > right);
				case ">=" -> bool(left >= right);
				default -> throw new IllegalArgumentException("Unsupported operator '" + binaryOpNode.getOp() + "'.");
			};
		}

		if (node instanceof UnaryOpNode unaryOpNode) {
			int value = evaluate(unaryOpNode.getOperand(), environment);
			if ("-".equals(unaryOpNode.getOp())) {
				return -value;
			}
			throw new IllegalArgumentException("Unsupported unary operator '" + unaryOpNode.getOp() + "'.");
		}

		if (node instanceof IfNode ifNode) {
			return truthy(evaluate(ifNode.getCondition(), environment))
					? evaluate(ifNode.getThenBranch(), environment)
					: evaluate(ifNode.getElseBranch(), environment);
		}

		if (node instanceof WhileNode whileNode) {
			int value = 0;
			while (truthy(evaluate(whileNode.getCondition(), environment))) {
				value = evaluate(whileNode.getBody(), environment);
			}
			return value;
		}

		if (node instanceof FunctionDefNode functionDefNode) {
			environment.defineFunction(functionDefNode.getName(), functionDefNode);
			return 0;
		}

		if (node instanceof FunctionCallNode callNode) {
			FunctionDefNode function = environment.getFunction(callNode.getName());
			if (function == null) {
				throw new IllegalStateException("Undefined function '" + callNode.getName() + "'.");
			}

			if (function.getParams().size() != callNode.getArguments().size()) {
				throw new IllegalArgumentException("Function '" + callNode.getName() + "' expects " + function.getParams().size() + " arguments but got " + callNode.getArguments().size() + ".");
			}

			Environment local = environment.child();
			for (int i = 0; i < function.getParams().size(); i++) {
				int argValue = evaluate(callNode.getArguments().get(i), environment);
				local.set(function.getParams().get(i), argValue);
			}

			try {
				return evaluate(function.getBody(), local);
			} catch (ReturnSignal signal) {
				return signal.value;
			}
		}

		if (node instanceof ReturnNode returnNode) {
			throw new ReturnSignal(evaluate(returnNode.getValue(), environment));
		}

		throw new IllegalArgumentException("Unsupported node type: " + node.getClass().getSimpleName());
	}

	private static int bool(boolean value) {
		return value ? 1 : 0;
	}

	private static boolean truthy(int value) {
		return value != 0;
	}

	private static final class ReturnSignal extends RuntimeException {
		private final int value;

		private ReturnSignal(int value) {
			super(null, null, false, false);
			this.value = value;
		}
	}
}
