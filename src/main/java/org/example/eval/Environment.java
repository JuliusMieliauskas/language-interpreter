package org.example.eval;

import java.util.LinkedHashMap;
import java.util.Map;

public class Environment {

	private final Map<String, Integer> values = new LinkedHashMap<>();

	public int get(String name) {
		Integer value = values.get(name);
		if (value == null) {
			throw new IllegalStateException("Undefined variable '" + name + "'.");
		}
		return value;
	}

	public void set(String name, int value) {
		values.put(name, value);
	}

	public Map<String, Integer> snapshot() {
		return Map.copyOf(values);
	}
}
