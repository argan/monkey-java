package org.hydra.interpreter.evaluator;

import org.hydra.interpreter.object.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hydra.interpreter.object.MNull.NULL;

public class Builtins {
    private static final Map<String, MBuiltin> builtins = new HashMap<>();

    static {
        builtins.put("len", new MBuiltin("len", new Len()));
        builtins.put("first", new MBuiltin("first", new First()));
        builtins.put("last", new MBuiltin("last", new Last()));
        builtins.put("rest", new MBuiltin("rest", new Rest()));
        builtins.put("push", new MBuiltin("push", new Push()));
        builtins.put("puts", new MBuiltin("puts", new Puts()));
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
            } else if (args[0] instanceof MArray) {
                return new MInteger(((MArray) args[0]).getElements().size());
            }
            return Evaluator.newError("argument to 'len' not supported ,got %s", args[0].type());
        }
    }

    private static class First implements BuiltinFunction {

        @Override
        public MObject apply(MObject... args) {
            if (args == null || args.length != 1) {
                return Evaluator.newError("wrong number of arguments, need 1 got %d", args.length);
            }
            if (args[0] instanceof MArray) {
                MArray arr = (MArray) args[0];
                if (arr.getElements().isEmpty()) {
                    return NULL;
                }
                return arr.getElements().get(0);
            }
            return Evaluator.newError("argument to 'first' not supported ,got %s", args[0].type());
        }
    }

    private static class Last implements BuiltinFunction {

        @Override
        public MObject apply(MObject... args) {
            if (args == null || args.length != 1) {
                return Evaluator.newError("wrong number of arguments, need 1 got %d", args.length);
            }
            if (args[0] instanceof MArray) {
                List<MObject> elements = ((MArray) args[0]).getElements();
                if (elements.isEmpty()) {
                    return NULL;
                }
                return elements.get(elements.size() - 1);
            }
            return Evaluator.newError("argument to 'last' not supported ,got %s", args[0].type());
        }
    }

    private static class Rest implements BuiltinFunction {

        @Override
        public MObject apply(MObject... args) {
            if (args == null || args.length != 1) {
                return Evaluator.newError("wrong number of arguments, need 1 got %d", args.length);
            }
            if (args[0] instanceof MArray) {
                List<MObject> elements = ((MArray) args[0]).getElements();
                if (elements.isEmpty()) {
                    return NULL;
                }
                List<MObject> newArr = new ArrayList<>();
                newArr.addAll(elements);
                newArr.remove(0);
                return new MArray(newArr);
            }
            return Evaluator.newError("argument to 'rest' not supported ,got %s", args[0].type());
        }
    }

    private static class Push implements BuiltinFunction {

        @Override
        public MObject apply(MObject... args) {
            if (args == null || args.length != 2) {
                return Evaluator.newError("wrong number of arguments, need 2 got %d", args.length);
            }
            if (args[0] instanceof MArray) {

                List<MObject> newArr = new ArrayList<>();
                newArr.addAll(((MArray) args[0]).getElements());
                newArr.add(args[1]);
                return new MArray(newArr);
            }
            return Evaluator.newError("argument to 'push' not supported ,got %s", args[0].type());
        }
    }

    private static class Puts implements BuiltinFunction {

        @Override
        public MObject apply(MObject... args) {
            if (args == null) {
                return Evaluator.newError("wrong number of arguments, need at least 1 but null");
            }
            for (MObject arg : args) {
                System.out.println(arg.toString());
            }
            return NULL;
        }
    }
}
