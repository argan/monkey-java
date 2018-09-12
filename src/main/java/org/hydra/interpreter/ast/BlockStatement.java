package org.hydra.interpreter.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlockStatement implements Statement {
    private final List<Statement> statements = new ArrayList<>();

    @Override
    public String tokenLiteral() {
        return "Block Statement";
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        return statements.stream().map(stmt -> stmt.toString()).collect(Collectors.joining("{", "\n", "}"));
    }
}
