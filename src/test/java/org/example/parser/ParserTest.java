package org.example.parser;

import org.example.ast.AssignNode;
import org.example.ast.BooleanNode;
import org.example.ast.BinaryOpNode;
import org.example.ast.FunctionCallNode;
import org.example.ast.FunctionDefNode;
import org.example.ast.IfNode;
import org.example.ast.Node;
import org.example.ast.NumberNode;
import org.example.ast.ReturnNode;
import org.example.ast.SequenceNode;
import org.example.ast.WhileNode;
import org.example.ast.VariableNode;
import org.example.tokenizer.Tokenizer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void parsesNumberLiteral() {
        NumberNode number = assertInstanceOf(NumberNode.class, parse("2"));
        assertEquals(2, number.getValue());
    }

    @Test
    void parsesAssignment() {
        AssignNode assign = assertInstanceOf(AssignNode.class, parse("x = 2"));
        assertEquals("x", assign.getName());
        assertEquals(2, assertInstanceOf(NumberNode.class, assign.getValue()).getValue());
    }

    @Test
    void parsesMultilineAssignment() {
        SequenceNode sequence = assertInstanceOf(SequenceNode.class, parse("x = 2\ny = 4 * x"));
        assertEquals(2, sequence.getExpressions().size());

        AssignNode first = assertInstanceOf(AssignNode.class, sequence.getExpressions().get(0));
        assertEquals("x", first.getName());
        assertEquals(2, assertInstanceOf(NumberNode.class, first.getValue()).getValue());

        AssignNode second = assertInstanceOf(AssignNode.class, sequence.getExpressions().get(1));
        assertEquals("y", second.getName());

        BinaryOpNode multiply = assertInstanceOf(BinaryOpNode.class, second.getValue());
        assertEquals("*", multiply.getOp());
        assertEquals("x", assertInstanceOf(VariableNode.class, multiply.getRight()).getName());
    }

    @Test
    void parsesFactIterFunctionBodyAcrossLinesAndCommas() {
        Node node = parse("""
                fun fact_iter(n) { r = 1, while true do if n == 0 then return r else r = r * n, n = n - 1 }
                b = fact_iter(5)
                """);

        SequenceNode program = assertInstanceOf(SequenceNode.class, node);
        assertEquals(2, program.getExpressions().size());

        FunctionDefNode function = assertInstanceOf(FunctionDefNode.class, program.getExpressions().get(0));
        assertEquals("fact_iter", function.getName());
        assertEquals(List.of("n"), function.getParams());

        SequenceNode body = assertInstanceOf(SequenceNode.class, function.getBody());
        assertEquals(2, body.getExpressions().size());

        AssignNode init = assertInstanceOf(AssignNode.class, body.getExpressions().get(0));
        assertEquals("r", init.getName());
        assertEquals(1, assertInstanceOf(NumberNode.class, init.getValue()).getValue());

        WhileNode loop = assertInstanceOf(WhileNode.class, body.getExpressions().get(1));
        assertTrue(assertInstanceOf(BooleanNode.class, loop.getCondition()).getValue());

        SequenceNode loopBody = assertInstanceOf(SequenceNode.class, loop.getBody());
        assertEquals(2, loopBody.getExpressions().size());

        IfNode conditional = assertInstanceOf(IfNode.class, loopBody.getExpressions().get(0));
        BinaryOpNode condition = assertInstanceOf(BinaryOpNode.class, conditional.getCondition());
        assertEquals("==", condition.getOp());

        ReturnNode thenBranch = assertInstanceOf(ReturnNode.class, conditional.getThenBranch());
        assertInstanceOf(VariableNode.class, thenBranch.getValue());

        AssignNode elseBranch = assertInstanceOf(AssignNode.class, conditional.getElseBranch());
        assertEquals("r", elseBranch.getName());

        AssignNode decrement = assertInstanceOf(AssignNode.class, loopBody.getExpressions().get(1));
        assertEquals("n", decrement.getName());

        AssignNode callSite = assertInstanceOf(AssignNode.class, program.getExpressions().get(1));
        assertEquals("b", callSite.getName());
//        assertInstanceOf(FunctionCallNode.class, assignValue(callSite));
    }

    @Test
    void parsesBinaryOperationsWithPrecedence() {
        BinaryOpNode plus = assertInstanceOf(BinaryOpNode.class, parse("1 + 2 * 3"));
        assertEquals("+", plus.getOp());

        NumberNode left = assertInstanceOf(NumberNode.class, plus.getLeft());
        assertEquals(1, left.getValue());

        BinaryOpNode multiply = assertInstanceOf(BinaryOpNode.class, plus.getRight());
        assertEquals("*", multiply.getOp());
        assertEquals(2, assertInstanceOf(NumberNode.class, multiply.getLeft()).getValue());
        assertEquals(3, assertInstanceOf(NumberNode.class, multiply.getRight()).getValue());
    }

    @Test
    void parsesParenthesizedExpressionBeforeMultiplication() {
        BinaryOpNode multiply = assertInstanceOf(BinaryOpNode.class, parse("(1 + 2) * 3"));
        assertEquals("*", multiply.getOp());

        BinaryOpNode plus = assertInstanceOf(BinaryOpNode.class, multiply.getLeft());
        assertEquals("+", plus.getOp());
        assertEquals(1, assertInstanceOf(NumberNode.class, plus.getLeft()).getValue());
        assertEquals(2, assertInstanceOf(NumberNode.class, plus.getRight()).getValue());

        assertEquals(3, assertInstanceOf(NumberNode.class, multiply.getRight()).getValue());
    }

    private Node parse(String source) {
        return new Parser(new Tokenizer(source).tokenize()).parse();
    }
}