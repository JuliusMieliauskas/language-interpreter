package org.example;

import org.example.eval.Evaluator;
import org.example.parser.Parser;
import org.example.tokenizer.Tokenizer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class LanguageInterpreter {
    static void main() throws Exception {
        String source = new String(System.in.readAllBytes(), StandardCharsets.UTF_8);

        Parser parser = new Parser(new Tokenizer(source).tokenize());
        Map<String, Integer> result = new Evaluator().evaluate(parser.parse());

        result.forEach((name, value) -> System.out.println(name + ": " + value));
    }
}
