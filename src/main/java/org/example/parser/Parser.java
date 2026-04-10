package org.example.parser;

import org.example.ast.*;
import org.example.tokenizer.Token;
import org.example.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Node parse() {
        Node expr = parseSequence(TokenType.EOF);
        consume(TokenType.EOF, "Expected end of input.");
        return expr;
    }

    private Node parseExpression() {
        return parseAssignment();
    }

    private Node parseAssignment() {
        Node expr = parseEquality();

        if (match(TokenType.ASSIGN)) {
            Token equals = previous();
            Node value = parseAssignment(); // right-associative

            if (expr instanceof VariableNode variable) {
                return new AssignNode(variable.getName(), value);
            }

            throw error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Node parseEquality() {
        Node expr = parseComparison();

        while (match(TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL)) {
            Token operator = previous();
            Node right = parseComparison();
            expr = new BinaryOpNode(expr, operator.lexeme(), right);
        }

        return expr;
    }

    private Node parseComparison() {
        Node expr = parseAdditive();

        while (match(TokenType.LESS, TokenType.LESS_EQUAL, TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            Token operator = previous();
            Node right = parseAdditive();
            expr = new BinaryOpNode(expr, operator.lexeme(), right);
        }

        return expr;
    }

    private Node parseAdditive() {
        Node expr = parseMultiplicative();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Node right = parseMultiplicative();
            expr = new BinaryOpNode(expr, operator.lexeme(), right);
        }

        return expr;
    }

    private Node parseMultiplicative() {
        Node expr = parseUnary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Node right = parseUnary();
            expr = new BinaryOpNode(expr, operator.lexeme(), right);
        }

        return expr;
    }

    private Node parseUnary() {
        if (match(TokenType.MINUS)) {
            Token operator = previous();
            return new UnaryOpNode(operator.lexeme(), parseUnary());
        }

        return parseCall();
    }

    private Node parseCall() {
        Node expr = parsePrimary();

        while (match(TokenType.LEFT_PAREN)) {
            expr = finishCall(expr);
        }

        return expr;
    }

    private Node parsePrimary() {
        if (match(TokenType.NUMBER)) {
            Token number = previous();
            return new NumberNode(Integer.parseInt(number.lexeme()));
        }

        if (match(TokenType.TRUE)) {
            return new BooleanNode(true);
        }

        if (match(TokenType.FALSE)) {
            return new BooleanNode(false);
        }

        if (match(TokenType.IDENTIFIER)) {
            Token name = previous();
            return new VariableNode(name.lexeme());
        }

        if (match(TokenType.IF)) {
            return parseIf();
        }

        if (match(TokenType.WHILE)) {
            return parseWhile();
        }

        if (match(TokenType.FUN)) {
            return parseFunctionDef();
        }

        if (match(TokenType.RETURN)) {
            return new ReturnNode(parseExpression());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Node expr = parseExpression();
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression.");
            return expr;
        }

        if (match(TokenType.LEFT_BRACE)) {
            return parseBlock();
        }

        throw error(peek(), "Expected expression.");
    }

    private Node parseIf() {
        Node condition = parseExpression();
        consume(TokenType.THEN, "Expected 'then' after if condition.");
        Node thenBranch = parseExpression();
        consume(TokenType.ELSE, "Expected 'else' after then branch.");
        Node elseBranch = parseExpression();
        return new IfNode(condition, thenBranch, elseBranch);
    }

    private Node parseWhile() {
        Node condition = parseExpression();
        consume(TokenType.DO, "Expected 'do' after while condition.");
        Node body = parseSequence(TokenType.NEWLINE, TokenType.RIGHT_BRACE, TokenType.EOF);
        return new WhileNode(condition, body);
    }

    private Node parseFunctionDef() {
        Token name = consume(TokenType.IDENTIFIER, "Expected function name.");
        consume(TokenType.LEFT_PAREN, "Expected '(' after function name.");
        List<String> params = new ArrayList<>();

        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                params.add(consume(TokenType.IDENTIFIER, "Expected parameter name.").lexeme());
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RIGHT_PAREN, "Expected ')' after parameters.");
        consume(TokenType.LEFT_BRACE, "Expected '{' before function body.");
        Node body = parseSequence(TokenType.RIGHT_BRACE);
        consume(TokenType.RIGHT_BRACE, "Expected '}' after function body.");
        return new FunctionDefNode(name.lexeme(), params, body);
    }

    private Node parseBlock() {
        Node body = parseSequence(TokenType.RIGHT_BRACE);
        consume(TokenType.RIGHT_BRACE, "Expected '}' after block.");
        return body;
    }

    private Node finishCall(Node callee) {
        if (!(callee instanceof VariableNode variable)) {
            throw error(previous(), "Can only call named functions.");
        }

        List<Node> arguments = new ArrayList<>();
        if (!check(TokenType.RIGHT_PAREN)) {
            do {
                arguments.add(parseExpression());
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.RIGHT_PAREN, "Expected ')' after arguments.");
        return new FunctionCallNode(variable.getName(), arguments);
    }

    private Node parseSequence(TokenType... terminators) {
        skipSeparators();

        List<Node> expressions = new ArrayList<>();
        while (!checkAny(terminators) && !isAtEnd()) {
            expressions.add(parseExpression());
            if (checkAny(terminators) || isAtEnd()) {
                break;
            }
            if (match(TokenType.COMMA, TokenType.NEWLINE)) {
                skipSeparators();
                continue;
            }
            break;
        }

        if (expressions.isEmpty()) {
            throw error(peek(), "Expected expression.");
        }

        return expressions.size() == 1 ? expressions.getFirst() : new SequenceNode(expressions);
    }

    private void skipSeparators() {
        for (;;) {
            if (!match(TokenType.COMMA, TokenType.NEWLINE)) {
                return;
            }
        }
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return type == TokenType.EOF;
        }
        return peek().type() == type;
    }

    private boolean checkAny(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                return true;
            }
        }
        return false;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private RuntimeException error(Token token, String message) {
        return new IllegalArgumentException(
                message + " at line " + token.line() + ", column " + token.column()
        );
    }
}
