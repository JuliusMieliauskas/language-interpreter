package org.example.eval;

import org.example.ast.FunctionDefNode;

import java.util.LinkedHashMap;
import java.util.Map;

public class Environment {

	private final Map<String, Integer> values = new LinkedHashMap<>();
	private final Map<String, FunctionDefNode> functions;
	private final Environment parent;

	public Environment() {
		this(null, new LinkedHashMap<>());
	}

	private Environment(Environment parent, Map<String, FunctionDefNode> functions) {
		this.parent = parent;
		this.functions = functions;
	}

	public Environment child() {
		return new Environment(this, functions);
	}

	public int get(String name) {
		Integer value = values.get(name);
		if (value != null) {
			return value;
		}

		if (parent != null) {
			return parent.get(name);
		}

		throw new IllegalStateException("Undefined variable '" + name + "'.");
	}

	public void set(String name, int value) {
		values.put(name, value);
	}

	public void defineFunction(String name, FunctionDefNode function) {
		functions.put(name, function);
	}

	public FunctionDefNode getFunction(String name) {
		return functions.get(name);
	}

	public Map<String, Integer> snapshot() {
		return new LinkedHashMap<>(values);
	}
}
