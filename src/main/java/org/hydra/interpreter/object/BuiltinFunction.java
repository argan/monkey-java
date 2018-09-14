package org.hydra.interpreter.object;

@FunctionalInterface
public interface BuiltinFunction {
    MObject apply(MObject... args);
}
