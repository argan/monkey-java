package org.hydra.interpreter.object;

import org.hydra.interpreter.ast.BlockStatement;
import org.hydra.interpreter.ast.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public class MFunction implements MObject {
    private final List<Identifier> parameters;
    private final BlockStatement body;
    private final Environment env;

    public MFunction(List<Identifier> parameters, BlockStatement body, Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }

    public List<Identifier> getParameters() {
        return parameters;
    }

    public BlockStatement getBody() {
        return body;
    }

    public Environment getEnv() {
        return env;
    }

    @Override
    public ObjectType type() {
        return ObjectType.FUNCTION_OBJ;
    }

    @Override
    public String toString() {
        return "fn(" + parameters.stream().map(p -> p.toString()).collect(Collectors.joining(", ")) +
                ") {\n" + body +
                "\n}";
    }
}
