package org.example.eval;

import org.example.parser.Parser;
import org.example.tokenizer.Tokenizer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * This class tests the provided custom language snippets defined in `language-example.txt` file
 */
class GivenExamplesTest {

    private final Evaluator evaluator = new Evaluator();

    @Test
    void evaluatesMultipleAssignment() {
        String codeSnippet = "x = 2\ny = (x + 2) * 2\n";
        Map<String, Integer> result = evaluate(codeSnippet);

        assertEquals(Map.of("x", 2, "y", 8), result);
    }

    @Test
    void evaluateBinaryOperandOnAssignedVariable() {
        String codeSnippet = """        
                        x = 20
                        if x > 10 then y = 100 else y = 0
                """;
        Map<String, Integer> result = evaluate(codeSnippet);

        assertEquals(Map.of("x", 20, "y", 100), result);
    }

    @Test
    void evaluateSimpleWhileLoop() {
        String codeSnippet = """
                x = 0
                y = 0
                while x < 3 do if x == 1 then y = 10 else y = y + 1, x = x + 1
                """;
        Map<String, Integer> result = evaluate(codeSnippet);

        assertEquals(Map.of("x", 3, "y", 11), result);
    }

    @Test
    void evaluateSimpleFunction() {
        String codeSnippet = """
                fun add(a, b) { return a + b }
                four = add( 2, 2)
                """;
        Map<String, Integer> result = evaluate(codeSnippet);

        assertEquals(Map.of("four", 4), result);
    }

    @Test
    void evaluateRecursiveFunction() {
        String codeSnippet = """
                fun fact_rec(n) { if n <= 0 then return 1 else return n*fact_rec(n-1) }
                a = fact_rec(5)
                """;
        Map<String, Integer> result = evaluate(codeSnippet);

        assertEquals(Map.of("a", 120), result);
    }

    @Test
    void evaluateSequencedExpressions() {
        String codeSnippet = """
                fun fact_iter(n) { r = 1, while true do if n == 0 then return r else r = r * n, n = n - 1 }
                b = fact_iter(5)
                """;
        Map<String, Integer> result = evaluate(codeSnippet);

        assertEquals(Map.of("b", 120), result);
    }

    @Test
    void evaluateCustomFunctionExpression() {
        String codeSnippet = """
                fun square(n) {
                    return n*n
                }
                
                fun square_sum(a, b) {
                    return square(a) + square(b)
                }
                
                b = square_sum(2, 3)
                """;
        Map<String, Integer> result = evaluate(codeSnippet);

        assertEquals(Map.of("b", 13), result);
    }

    private Map<String, Integer> evaluate(String source) {
        return evaluator.evaluate(new Parser(new Tokenizer(source).tokenize()).parse());
    }
}


