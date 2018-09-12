package org.hydra.interpreter.token;

import java.util.HashMap;
import java.util.Map;

public class Keywords {
    private static Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("fn", TokenType.FUNCTION);
        keywords.put("let", TokenType.LET);
        keywords.put("true", TokenType.TRUE);
        keywords.put("false", TokenType.FALSE);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("return", TokenType.RETURN);
    }

    public static TokenType tokenType(String k) {
        TokenType type = keywords.get(k);
        return type == null ? TokenType.IDENTIFIER : type;
    }
}
