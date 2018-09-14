package org.hydra.interpreter.evaluator;

import org.hydra.interpreter.ast.*;
import org.hydra.interpreter.object.*;

import java.util.ArrayList;
import java.util.List;

import static org.hydra.interpreter.object.MNull.NULL;

public class Evaluator {
    public static MObject eval(Node node, Environment env) {
        if (node instanceof Program) {
            return evalProgram((Program) node, env);
        } else if (node instanceof BlockStatement) {
            return evalBlockStatements(((BlockStatement) node).getStatements(), env);
        } else if (node instanceof ExpressionStatement) {
            return eval(((ExpressionStatement) node).getExpression(), env);
        } else if (node instanceof IntegerLiteral) {
            return new MInteger(((IntegerLiteral) node).getValue());
        } else if (node instanceof StringLiteral) {
            return new MString(((StringLiteral) node).getValue());
        } else if (node instanceof BooleanLiteral) {
            return mbooleanValue(((BooleanLiteral) node).getValue());
        } else if (node instanceof PrefixExpression) {
            PrefixExpression exp = (PrefixExpression) node;
            MObject value = eval(exp.getRight(), env);
            if (isError(value)) {
                return value;
            }
            return evalPrefixExpression(exp.getOperator(), value);
        } else if (node instanceof InfixExpression) {
            InfixExpression infix = (InfixExpression) node;

            MObject left = eval(infix.getLeft(), env);
            if (isError(left)) {
                return left;
            }
            MObject right = eval(infix.getRight(), env);
            if (isError(right)) {
                return right;
            }
            return evalInfixExpression(infix.getOperator(), left, right);
        } else if (node instanceof IfExpression) {
            IfExpression exp = (IfExpression) node;
            MObject cond = eval(exp.getCondition(), env);
            if (isError(cond)) {
                return cond;
            }
            if (isTruthy(cond)) {
                return eval(exp.getConsequence(), env);
            } else if (exp.getAlternative() != null) {
                return eval(exp.getAlternative(), env);
            } else {
                return NULL;
            }
        } else if (node instanceof ReturnStatement) {
            MObject value = eval(((ReturnStatement) node).getValue(), env);
            if (isError(value)) {
                return value;
            }
            return new MReturnValue(value);
        } else if (node instanceof LetStatement) {
            LetStatement let = (LetStatement) node;

            MObject obj = eval(let.getValue(), env);
            if (isError(obj)) {
                return obj;
            }
            env.set(let.getIdentifier().getValue(), obj);
        } else if (node instanceof Identifier) {
            return evalIdentifier((Identifier) node, env);
        } else if (node instanceof FunctionLiteral) {
            FunctionLiteral func = (FunctionLiteral) node;
            return new MFunction(func.getParameters(), func.getBody(), env);
        } else if (node instanceof CallExpression) {
            CallExpression call = (CallExpression) node;
            MObject func = eval(call.getFunction(), env);
            if (isError(func)) {
                return func;
            }
            List<MObject> args = evalExpressions(call.getArguments(), env);
            if (args.size() == 1 && isError(args.get(0))) {
                return args.get(0);
            }

            return applyFunction(func, args, env);
        }
        return null;
    }

    private static MObject applyFunction(MObject obj, List<MObject> args, Environment env) {
        MObject result = null;
        if (obj instanceof MFunction) {
            Environment newEnv = new Environment(env);
            MFunction func = (MFunction) obj;
            for (int i = 0; i < func.getParameters().size(); i++) {
                newEnv.set(func.getParameters().get(i).getValue(), args.get(i));
            }

            result = eval(func.getBody(), newEnv);
        } else if (obj instanceof MBuiltin) {
            result = ((MBuiltin) obj).getFunc().apply(args.toArray(new MObject[args.size()]));
        }

        if (result != null) {
            if (result instanceof MReturnValue) {
                return ((MReturnValue) result).getValue();
            } else {
                return result;
            }
        } else {
            return newError("not a function: %s", obj.type());
        }
    }

    private static List<MObject> evalExpressions(List<Expression> arguments, Environment env) {
        List<MObject> result = new ArrayList<>();
        for (Expression exp : arguments) {
            MObject obj = eval(exp, env);
            if (isError(obj)) {
                result.clear();
                result.add(obj);
                return result;
            }
            result.add(obj);
        }
        return result;
    }

    private static MObject evalIdentifier(Identifier node, Environment env) {
        MObject obj = env.get(node.getValue());
        if (obj == null) {
            obj = Builtins.get(node.getValue());
        }
        if (obj == null) {
            return newError("identifier not found: %s", node.getValue());
        }
        return obj;
    }

    private static boolean isTruthy(MObject cond) {
        if (cond == NULL || cond == MBoolean.FALSE) {
            return false;
        }
        return true;
    }

    private static MObject evalInfixExpression(String operator, MObject left, MObject right) {
        if (left.type() == ObjectType.INTEGER_OBJ && right.type() == ObjectType.INTEGER_OBJ) {
            return evalIntegerInfixExpression(operator, (MInteger) left, (MInteger) right);
        } else if (left.type() == ObjectType.BOOLEAN_OBJ && right.type() == ObjectType.BOOLEAN_OBJ) {
            if (operator.equals("==")) {
                return mbooleanValue(left == right);
            } else if (operator.equals("!=")) {
                return mbooleanValue(left != right);
            } else {
                return newError("unknown operator: %s %s %s", left.type(), operator, right.type());
            }
        } else if (left.type() == ObjectType.STRING_OBJ && right.type() == ObjectType.STRING_OBJ) {
            if (operator.equals("+")) {
                return new MString(((MString) left).getValue() + ((MString) right).getValue());
            }
        }
        return newError("type mismatch: %s %s %s", left.type(), operator, right.type());
    }

    private static MObject evalIntegerInfixExpression(String operator, MInteger left, MInteger right) {
        switch (operator) {
            case "+":
                return new MInteger(left.getValue() + right.getValue());
            case "-":
                return new MInteger(left.getValue() - right.getValue());
            case "*":
                return new MInteger(left.getValue() * right.getValue());
            case "/":
                return new MInteger(left.getValue() / right.getValue());
            case ">":
                return mbooleanValue(left.getValue() > right.getValue());
            case "<":
                return mbooleanValue(left.getValue() < right.getValue());
            case ">=":
                return mbooleanValue(left.getValue() >= right.getValue());
            case "<=":
                return mbooleanValue(left.getValue() <= right.getValue());
            case "!=":
                return mbooleanValue(left.getValue() != right.getValue());
            case "==":
                return mbooleanValue(left.getValue() == right.getValue());
            default:
                return newError("unknown operator: %s%s%s", left.type(), operator, right.type());
        }
    }

    private static MObject evalPrefixExpression(String operator, MObject right) {
        switch (operator) {
            case "!":
                return evalBangOperatorExpression(right);
            case "-":
                return evalMinusOperatorExpression(right);
            default:
                return newError("unknown operator: %s%s", operator, right.type());
        }
    }

    private static MObject evalMinusOperatorExpression(MObject right) {
        if (right.type() != ObjectType.INTEGER_OBJ) {
            return newError("unknown operator: -%s", right.type());
        }
        return new MInteger(-((MInteger) right).getValue());
    }

    private static MObject evalBangOperatorExpression(MObject right) {
        if (MBoolean.FALSE == right || NULL == right) {
            return MBoolean.TRUE;
        }
        return MBoolean.FALSE;
    }


    private static MObject mbooleanValue(boolean value) {
        return value ? MBoolean.TRUE : MBoolean.FALSE;
    }

    private static MObject evalProgram(Program program, Environment env) {
        List<Statement> statements = program.getStatements();
        MObject result = null;

        for (Statement stmt : statements) {
            result = eval(stmt, env);

            if (result instanceof MReturnValue) {
                return ((MReturnValue) result).getValue();
            } else if (result instanceof MError) {
                return result;
            }
        }
        return result;
    }

    private static MObject evalBlockStatements(List<Statement> statements, Environment env) {
        MObject result = null;

        for (Statement stmt : statements) {
            result = eval(stmt, env);

            if (result instanceof MReturnValue || result instanceof MError) {
                return result;
            }
        }
        return result;
    }

    static MError newError(String format, Object... args) {
        return new MError(String.format(format, args));
    }

    private static boolean isError(MObject obj) {
        return obj != null && obj.type() == ObjectType.ERROR_OBJ;
    }
}
