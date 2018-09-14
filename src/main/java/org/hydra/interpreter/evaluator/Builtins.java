package org.hydra.interpreter.evaluator;

import org.hydra.interpreter.object.*;

import java.util.HashMap;
import java.util.Map;

public class Builtins {
    private static final Map<String, MBuiltin> builtins = new HashMap<>();

    static {
        builtins.put("len", new MBuiltin("len", new Len()));
    }

    public static MBuiltin get(String name) {
        return builtins.get(name);
    }

    private static class Len implements BuiltinFunction {

        @Override
        public MObject apply(MObject... args) {
            if (args == null || args.length != 1) {
                return Evaluator.newError("wrong number of arguments, need 1 got %d", args.length);
            }
            if (args[0] instanceof MString) {
                return new MInteger(((MString) args[0]).getValue().length());
            }
            return Evaluator.newError("argument to 'len' not supported ,got %s", args[0].type());
        }
    }
}
