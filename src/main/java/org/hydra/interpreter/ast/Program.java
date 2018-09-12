package org.hydra.interpreter.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Program implements Node {

    private final List<Statement> statements = new ArrayList<>();

    @Override
    public String tokenLiteral() {
        if (statements != null && statements.size() > 0) {
            return statements.get(0).tokenLiteral();
        }
        return "";
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public String toString() {
        return statements.stream().map(stmt -> stmt.toString()).collect(Collectors.joining());
    }
}
