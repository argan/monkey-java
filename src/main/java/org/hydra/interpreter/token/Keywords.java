package org.hydra.interpreter.token;

import java.util.HashMap;
import java.util.Map;

public class Keywords {
    private static Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("fn", TokenType.FUNCTION);
        keywords.put("let", TokenType.LET);
    }

    public static TokenType tokenType(String k) {
        TokenType type = keywords.get(k);
        return type == null ? TokenType.IDENTIFIER : type;
    }
}
