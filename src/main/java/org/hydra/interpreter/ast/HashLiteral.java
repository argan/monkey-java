package org.hydra.interpreter.ast;

import java.util.HashMap;
import java.util.Map;

public class HashLiteral implements Expression {
    private final Map<Expression,Expression> pairs ;

    public HashLiteral(Map<Expression, Expression> pairs) {
        this.pairs = pairs;
    }

    public Map<Expression, Expression> getPairs() {
        return pairs;
    }

    @Override
    public String tokenLiteral() {
        return "Hash Literal";
    }
}
