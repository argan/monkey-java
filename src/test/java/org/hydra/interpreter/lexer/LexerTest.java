package org.hydra.interpreter.lexer;


import org.hydra.interpreter.token.Token;
import org.hydra.interpreter.token.TokenType;
import org.junit.Test;

import static org.junit.Assert.*;

public class LexerTest {

    @Test
    public void testNextToken() {
        String input = "+={}()[]-,;:" +
                "let five = 5;" +
                "let ten = 10;" +
                "let add = fn(x,y) {" +
                "    x + y ; " +
                "}" +
                ";" +
                "let result = add(five,ten);";

        Lexer lexer = new Lexer(input);

        Object[][] expected = new Object[][]{
                new Object[]{TokenType.PLUS, "+"},
                new Object[]{TokenType.ASSIGN, "="},
                new Object[]{TokenType.LBRACE, "{"},
                new Object[]{TokenType.RBRACE, "}"},
                new Object[]{TokenType.LPAREN, "("},
                new Object[]{TokenType.RPAREN, ")"},
                new Object[]{TokenType.LBRACKT, "["},
                new Object[]{TokenType.RBRACKET, "]"},
                new Object[]{TokenType.MINUS, "-"},
                new Object[]{TokenType.COMMA, ","},
                new Object[]{TokenType.SEMICOLON, ";"},
                new Object[]{TokenType.COLON, ":"},
                new Object[]{TokenType.LET, "let"},
                new Object[]{TokenType.IDENTIFIER, "five"},
                new Object[]{TokenType.ASSIGN, "="},
                new Object[]{TokenType.INTEGER, "5"},
                new Object[]{TokenType.SEMICOLON, ";"},
                new Object[]{TokenType.LET, "let"},
                new Object[]{TokenType.IDENTIFIER, "ten"},
                new Object[]{TokenType.ASSIGN, "="},
                new Object[]{TokenType.INTEGER, "10"},
                new Object[]{TokenType.SEMICOLON, ";"},
                new Object[]{TokenType.LET, "let"},
                new Object[]{TokenType.IDENTIFIER, "add"},
                new Object[]{TokenType.ASSIGN, "="},
                new Object[]{TokenType.FUNCTION, "fn"},
                new Object[]{TokenType.LPAREN, "("},
                new Object[]{TokenType.IDENTIFIER, "x"},
                new Object[]{TokenType.COMMA, ","},
                new Object[]{TokenType.IDENTIFIER, "y"},
                new Object[]{TokenType.RPAREN, ")"},
                new Object[]{TokenType.LBRACE, "{"},
                new Object[]{TokenType.IDENTIFIER, "x"},
                new Object[]{TokenType.PLUS, "+"},
                new Object[]{TokenType.IDENTIFIER, "y"},
                new Object[]{TokenType.SEMICOLON, ";"},
                new Object[]{TokenType.RBRACE, "}"},
                new Object[]{TokenType.SEMICOLON, ";"},
                new Object[]{TokenType.LET, "let"},
                new Object[]{TokenType.IDENTIFIER, "result"},
                new Object[]{TokenType.ASSIGN, "="},
                new Object[]{TokenType.IDENTIFIER, "add"},
                new Object[]{TokenType.LPAREN, "("},
                new Object[]{TokenType.IDENTIFIER, "five"},
                new Object[]{TokenType.COMMA, ","},
                new Object[]{TokenType.IDENTIFIER, "ten"},
                new Object[]{TokenType.RPAREN, ")"},
                new Object[]{TokenType.SEMICOLON, ";"},

        };

        for (Object[] arr : expected) {
            Token token = lexer.nextToken();
            assertNotNull(token);
            assertEquals("token type wrong", arr[0], token.getType());
            assertEquals("literal wrong", arr[1], token.getLiteral());
        }

    }


}
