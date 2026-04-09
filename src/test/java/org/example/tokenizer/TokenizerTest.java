package org.example.tokenizer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class TokenizerTest {

	@Test
	void testAssignmentAndConstant() {
		List<Token> tokens = new Tokenizer("x = 2").tokenize();

		assertTokenTypes(tokens,
				TokenType.IDENTIFIER,
				TokenType.ASSIGN,
				TokenType.NUMBER,
				TokenType.EOF);

		assertLexemes(tokens, "x", "=", "2", "");
	}

	@Test
	void testBinaryExpressionWithParentheses() {
		List<Token> tokens = new Tokenizer("y = (x + 2) * 2").tokenize();

		assertTokenTypes(tokens,
				TokenType.IDENTIFIER,
				TokenType.ASSIGN,
				TokenType.LEFT_PAREN,
				TokenType.IDENTIFIER,
				TokenType.PLUS,
				TokenType.NUMBER,
				TokenType.RIGHT_PAREN,
				TokenType.STAR,
				TokenType.NUMBER,
				TokenType.EOF);

		assertLexemes(tokens, "y", "=", "(", "x", "+", "2", ")", "*", "2", "");
	}

	@Test
	void testMultipleLines() {
		String input = "x = 2\n" +
				"y = x + 1\n";

		List<Token> tokens = new Tokenizer(input).tokenize();

		assertTokenTypes(tokens,
				TokenType.IDENTIFIER,
				TokenType.ASSIGN,
				TokenType.NUMBER,
				TokenType.NEWLINE,
				TokenType.IDENTIFIER,
				TokenType.ASSIGN,
				TokenType.IDENTIFIER,
				TokenType.PLUS,
				TokenType.NUMBER,
				TokenType.NEWLINE,
				TokenType.EOF);
	}

	private static void assertTokenTypes(List<Token> actualTokens, TokenType... expectedTypes) {
		org.junit.jupiter.api.Assertions.assertEquals(expectedTypes.length, actualTokens.size(),
				"Expected " + expectedTypes.length + " tokens but got " + actualTokens.size() + ": " + types(actualTokens));

		for (int i = 0; i < expectedTypes.length; i++) {
			TokenType actual = actualTokens.get(i).type();
			org.junit.jupiter.api.Assertions.assertEquals(expectedTypes[i], actual,
					"Token type mismatch at index " + i + ". Expected " + expectedTypes[i] + " but got " + actual + ".");
		}
	}

	private static void assertLexemes(List<Token> actualTokens, String... expectedLexemes) {
		org.junit.jupiter.api.Assertions.assertEquals(expectedLexemes.length, actualTokens.size(),
				"Expected " + expectedLexemes.length + " lexemes but got " + actualTokens.size());

		for (int i = 0; i < expectedLexemes.length; i++) {
			String actual = actualTokens.get(i).lexeme();
			org.junit.jupiter.api.Assertions.assertEquals(expectedLexemes[i], actual,
					"Lexeme mismatch at index " + i + ". Expected '" + expectedLexemes[i] + "' but got '" + actual + "'.");
		}
	}

	private static List<TokenType> types(List<Token> tokens) {
		List<TokenType> result = new ArrayList<>();
		for (Token token : tokens) {
			result.add(token.type());
		}
		return result;
	}

}