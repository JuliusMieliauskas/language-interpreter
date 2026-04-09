package org.example.parser;

import org.example.ast.*;
import org.example.tokenizer.Token;
import org.example.tokenizer.TokenType;

import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Node parse() {
        Node expr = parseExpression();
        consume(TokenType.EOF, "Expected end of input.");
        return expr;
    }

    private Node parseExpression() {
        return parseAssignment();
    }

    private Node parseAssignment() {
        Node expr = parseAdditive();

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
        Node expr = parsePrimary();

        while (match(TokenType.STAR, TokenType.SLASH)) {
            Token operator = previous();
            Node right = parsePrimary();
            expr = new BinaryOpNode(expr, operator.lexeme(), right);
        }

        return expr;
    }

    private Node parsePrimary() {
        if (match(TokenType.NUMBER)) {
            Token number = previous();
            return new NumberNode(Integer.parseInt(number.lexeme()));
        }

        if (match(TokenType.IDENTIFIER)) {
            Token name = previous();
            return new VariableNode(name.lexeme());
        }

        if (match(TokenType.LEFT_PAREN)) {
            Node expr = parseExpression();
            consume(TokenType.RIGHT_PAREN, "Expected ')' after expression.");
            return expr;
        }

        throw error(peek(), "Expected expression.");
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

    private void consume(TokenType type, String message) {
        if (check(type)) {
            advance();
            return;
        }
        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return type == TokenType.EOF;
        }
        return peek().type() == type;
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
