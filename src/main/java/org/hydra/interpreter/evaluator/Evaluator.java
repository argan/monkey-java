package org.hydra.interpreter.evaluator;

import org.hydra.interpreter.ast.*;
import org.hydra.interpreter.object.*;

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
        }
        return null;
    }

    private static MObject evalIdentifier(Identifier node, Environment env) {
        MObject obj = env.get(node.getValue());
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

    private static MError newError(String format, Object... args) {
        return new MError(String.format(format, args));
    }

    private static boolean isError(MObject obj) {
        return obj != null && obj.type() == ObjectType.ERROR_OBJ;
    }
}
