package org.example.eval;

import org.example.parser.Parser;
import org.example.tokenizer.Tokenizer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EvaluatorTest {

    private final Evaluator evaluator = new Evaluator();

    @Test
    void evaluatesSingleAssignment() {
        Map<String, Integer> result = evaluate("x = 2");

        assertEquals(Map.of("x", 2), result);
    }

    @Test
    void evaluatesBinaryExpressionOnAssignedVariable() {
        Map<String, Integer> result = evaluate("x = 2 + 3 * 4");

        assertEquals(Map.of("x", 14), result);
    }

    @Test
    void evaluatesRightAssociativeAssignments() {
        Map<String, Integer> result = evaluate("x = y = 2");

        assertEquals(Map.of("x", 2, "y", 2), result);
    }

    @Test
    void failsOnUndefinedVariableLookup() {
        assertThrows(IllegalStateException.class, () -> evaluator.evaluate(new Parser(new Tokenizer("x + 1").tokenize()).parse()));
    }

    private Map<String, Integer> evaluate(String source) {
        return evaluator.evaluate(new Parser(new Tokenizer(source).tokenize()).parse());
    }
}


