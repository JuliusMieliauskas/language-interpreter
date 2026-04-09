package org.example.tokenizer;

public enum TokenType {
    NUMBER,
    IDENTIFIER,

    // Keywords
    IF,
    THEN,
    ELSE,
    WHILE,
    DO,
    FUN,
    RETURN,
    TRUE,
    FALSE,

    // Operators
    ASSIGN,      // =
    EQUAL_EQUAL, // ==
    BANG_EQUAL,  // !=
    LESS,        // <
    LESS_EQUAL,  // <=
    GREATER,     // >
    GREATER_EQUAL, // >=
    PLUS,
    MINUS,
    STAR,
    SLASH,

    // Delimiters
    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    COMMA,
    NEWLINE,

    EOF
}

