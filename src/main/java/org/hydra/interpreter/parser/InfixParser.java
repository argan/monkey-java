package org.hydra.interpreter.parser;

import org.hydra.interpreter.ast.Expression;

@FunctionalInterface
public interface PrefixParser {
    Expression parse();
}
