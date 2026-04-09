package org.example.parser;

import org.example.ast.AssignNode;
import org.example.ast.BinaryOpNode;
import org.example.ast.Node;
import org.example.ast.NumberNode;
import org.example.tokenizer.Tokenizer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void parsesNumberLiteral() {
        Node node = parse("2");

        NumberNode number = assertInstanceOf(NumberNode.class, node);
        assertEquals(2, numberValue(number));
    }

    @Test
    void parsesAssignment() {
        Node node = parse("x = 2");

        AssignNode assign = assertInstanceOf(AssignNode.class, node);
        assertEquals("x", assignName(assign));

        NumberNode value = assertInstanceOf(NumberNode.class, assignValue(assign));
        assertEquals(2, numberValue(value));
    }

    @Test
    void parsesBinaryOperationsWithPrecedence() {
        Node node = parse("1 + 2 * 3");

        BinaryOpNode plus = assertInstanceOf(BinaryOpNode.class, node);
        assertEquals("+", binaryOp(plus));

        NumberNode left = assertInstanceOf(NumberNode.class, binaryLeft(plus));
        assertEquals(1, numberValue(left));

        BinaryOpNode multiply = assertInstanceOf(BinaryOpNode.class, binaryRight(plus));
        assertEquals("*", binaryOp(multiply));
        assertEquals(2, numberValue(assertInstanceOf(NumberNode.class, binaryLeft(multiply))));
        assertEquals(3, numberValue(assertInstanceOf(NumberNode.class, binaryRight(multiply))));
    }

    @Test
    void parsesParenthesizedExpressionBeforeMultiplication() {
        Node node = parse("(1 + 2) * 3");

        BinaryOpNode multiply = assertInstanceOf(BinaryOpNode.class, node);
        assertEquals("*", binaryOp(multiply));

        BinaryOpNode plus = assertInstanceOf(BinaryOpNode.class, binaryLeft(multiply));
        assertEquals("+", binaryOp(plus));
        assertEquals(1, numberValue(assertInstanceOf(NumberNode.class, binaryLeft(plus))));
        assertEquals(2, numberValue(assertInstanceOf(NumberNode.class, binaryRight(plus))));

        NumberNode right = assertInstanceOf(NumberNode.class, binaryRight(multiply));
        assertEquals(3, numberValue(right));
    }

    private Node parse(String source) {
        List<org.example.tokenizer.Token> tokens = new Tokenizer(source).tokenize();
        return new Parser(tokens).parse();
    }

    private static int numberValue(NumberNode node) {
        return (int) readField(node, "value");
    }

    private static String assignName(AssignNode node) {
        return (String) readField(node, "name");
    }

    private static Node assignValue(AssignNode node) {
        return (Node) readField(node, "value");
    }

    private static String binaryOp(BinaryOpNode node) {
        return (String) readField(node, "op");
    }

    private static Node binaryLeft(BinaryOpNode node) {
        return (Node) readField(node, "left");
    }

    private static Node binaryRight(BinaryOpNode node) {
        return (Node) readField(node, "right");
    }

    private static Object readField(Object target, String fieldName) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to read field '" + fieldName + "' from " + target.getClass().getSimpleName(), e);
        }
    }
}