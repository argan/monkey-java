package org.hydra.interpreter.parser;

import org.hydra.interpreter.ast.*;
import org.hydra.interpreter.lexer.Lexer;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void testParseProgram() {
        Object[][] expected = new Object[][]{
                new Object[]{"let x = 5", "x", 5},
                new Object[]{"let y = false;", "y", false},
                new Object[]{"let foo = bar;", "foo", "bar"},
        };
        for (Object[] arr : expected) {
            String input = (String) arr[0];

            Program program = parseProgram(input);

            assertEquals(1, program.getStatements().size());

            testLetStatement(program.getStatements().get(0), (String) arr[1]);
        }

    }

    @Test
    public void testParseReturnStatments() {
        String input = "return 123;return x;";

        Program program = parseProgram(input);

        assertEquals(2, program.getStatements().size());

        for (int i = 0; i < 2; i++) {
            testReturnStatement(program.getStatements().get(i));
        }
    }

    @Test
    public void testIdentifierExpression() {
        String input = "x; y;";

        Program program = parseProgram(input);

        assertEquals(2, program.getStatements().size());
        String[] expected = {"x", "y"};

        for (int i = 0; i < expected.length; i++) {
            Statement stmt = program.getStatements().get(i);
            assertTrue(stmt instanceof ExpressionStatement);

            ExpressionStatement letStmt = (ExpressionStatement) stmt;

            testIdentier(letStmt.getExpression(), expected[i]);
        }
    }

    @Test
    public void testIntegerLiteral() {
        String input = "5; 10;";

        Program program = parseProgram(input);

        assertEquals(2, program.getStatements().size());
        int[] expected = {5, 10};

        for (int i = 0; i < expected.length; i++) {
            assertTrue(program.getStatements().get(i) instanceof ExpressionStatement);

            ExpressionStatement letStmt = (ExpressionStatement) program.getStatements().get(i);
            testInteger(letStmt.getExpression(), expected[i]);
        }
    }

    @Test
    public void testPrefixExpression() {
        Object[][] expected = new Object[][]{
                new Object[]{"!5", "!", 5},
                new Object[]{"-5", "-", 5},
                new Object[]{"!true", "!", true},
                new Object[]{"!false", "!", false},
        };
        for (Object[] arr : expected) {
            Program program = parseProgram((String) arr[0]);
            assertEquals(1, program.getStatements().size());
            assertTrue(program.getStatements().get(0) instanceof ExpressionStatement);

            ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);

            assertTrue(stmt.getExpression() instanceof PrefixExpression);

            PrefixExpression exp = (PrefixExpression) stmt.getExpression();

            assertEquals(arr[1], exp.getOperator());
            testLiteral(exp.getRight(), arr[2]);
        }
    }

    @Test
    public void testInfixExpression() {
        Object[][] expected = new Object[][]{
                new Object[]{"5 + 5", 5, "+", 5},
                new Object[]{"5 - 5", 5, "-", 5},
                new Object[]{"5 * 5", 5, "*", 5},
                new Object[]{"5 / 5", 5, "/", 5},
                new Object[]{"5 > 5", 5, ">", 5},
                new Object[]{"5 < 5", 5, "<", 5},
                new Object[]{"5 == 5", 5, "==", 5},
                new Object[]{"5 != 5", 5, "!=", 5},
                new Object[]{"5 >= 5", 5, ">=", 5},
                new Object[]{"5 <= 5", 5, "<=", 5},
                new Object[]{"true != false", true, "!=", false},
        };
        for (Object[] arr : expected) {
            Program program = parseProgram((String) arr[0]);
            assertEquals(1, program.getStatements().size());
            assertTrue(program.getStatements().get(0) instanceof ExpressionStatement);

            ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);

            assertTrue(stmt.getExpression() instanceof InfixExpression);

            InfixExpression exp = (InfixExpression) stmt.getExpression();

            testInfixExpression(exp, arr[1], (String) arr[2], arr[3]);
        }
    }

    private void testInfixExpression(Expression exp0, Object left, String operator, Object right) {
        assertTrue(exp0 instanceof InfixExpression);
        InfixExpression exp = (InfixExpression) exp0;
        testLiteral(exp.getLeft(), left);
        assertEquals(operator, exp.getOperator());
        testLiteral(exp.getRight(), right);

    }

    @Test
    public void testOperatorPrecedence() {
        String[][] expected = new String[][]{
                {"true", "true"},
                {"false", "false"},
                {"-a * b", "((-a) * b)"},
                {"!-a", "(!(-a))"},
                {"a+b+c", "((a + b) + c)"},
                {"a*b*c", "((a * b) * c)"},
                {"a*b/c", "((a * b) / c)"},
                {"a+b-c", "((a + b) - c)"},
                {"a+b/c", "(a + (b / c))"},
                {"a + b * c + d / e - f", "(((a + (b * c)) + (d / e)) - f)",},
                {"3 + 4; -5 * 5", "(3 + 4)((-5) * 5)",},
                {"5 > 4 == 3 < 4", "((5 > 4) == (3 < 4))",},
                {"5 < 4 != 3 > 4", "((5 < 4) != (3 > 4))",},
                {"3 + 4 * 5 == 3 * 1 + 4 * 5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))",},
                {"3 + 4 * 5 == 3 * 1 + 4 * 5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))",},
                {"1 + (2 + 3 ) + 4", "((1 + (2 + 3)) + 4)",},
                {"(5+5) * 2", "((5 + 5) * 2)",},
                {"2/(5+5)", "(2 / (5 + 5))",},
                {"!(true == false)", "(!(true == false))",},
                {
                        "a + add(b * c) + d",
                        "((a + add((b * c))) + d)",
                },
                {
                        "add(a, b, 1, 2 * 3, 4 + 5, add(6, 7 * 8))",
                        "add(a, b, 1, (2 * 3), (4 + 5), add(6, (7 * 8)))",
                },
                {
                        "add(a + b + c * d / f + g)",
                        "add((((a + b) + ((c * d) / f)) + g))",
                },

        };
        for (String[] arr : expected) {
            Program program = parseProgram(arr[0]);

            assertEquals(arr[1], program.toString());
        }
    }

    private Program parseProgram(String input) {
        Parser parser = new Parser(new Lexer(input));

        Program program = parser.parseProgram();

        assertNotNull(program);
        checkParseErrors(parser);
        assertNotNull(program.getStatements());
        return program;
    }

    private void checkParseErrors(Parser parser) {
        if (parser.errors().size() == 0) {
            return;
        }
        System.err.printf("parser has %d errors\n", parser.errors().size());

        for (String err : parser.errors()) {
            System.err.printf("parse error : %s\n", err);
        }
        fail();

    }

    private void testLetStatement(Statement stmt, String name) {
        assertTrue(stmt instanceof LetStatement);

        LetStatement letStmt = (LetStatement) stmt;

        assertEquals("let identifier", name, letStmt.getIdentifier().getValue());
        assertEquals("token literal", name, letStmt.getIdentifier().tokenLiteral());

    }

    private void testIdentier(Expression exp, String name) {
        assertTrue(exp instanceof Idenifier);
        Idenifier idenifier = (Idenifier) exp;

        assertEquals("identifier", name, idenifier.getValue());
        assertEquals("token literal", name, idenifier.tokenLiteral());

    }

    private void testInteger(Expression expression, int value) {
        assertTrue(expression instanceof IntegerLiteral);
        IntegerLiteral idenifier = (IntegerLiteral) expression;

        assertEquals("integer", value, idenifier.getValue());
        assertEquals("token literal", String.valueOf(value), idenifier.tokenLiteral());

    }

    private void testBoolean(Expression expression, boolean value) {
        assertTrue(expression instanceof BooleanLiteral);
        BooleanLiteral idenifier = (BooleanLiteral) expression;

        assertEquals("boolean", value, idenifier.getValue());
        assertEquals("token literal", String.valueOf(value), idenifier.tokenLiteral());

    }

    private void testReturnStatement(Statement stmt) {
        assertTrue(stmt instanceof ReturnStatement);
    }

    private boolean testLiteral(Expression exp, Object value) {
        if (value instanceof Integer) {
            testInteger(exp, (Integer) value);
        } else if (value instanceof Boolean) {
            testBoolean(exp, (Boolean) value);
        } else if (value instanceof String) {
            testIdentier(exp, (String) value);
        } else {
            fail("unknown literal type " + value);
        }
        return false;
    }

    @Test
    public void testIfExpression() {
        String input = "if ( x > y ) { return x; } else { y }";
        Program program = parseProgram(input);

        assertEquals(1, program.getStatements().size());

        assertTrue(program.getStatements().get(0) instanceof ExpressionStatement);

        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);

        assertTrue(stmt.getExpression() instanceof IfExpression);

        IfExpression exp = (IfExpression) stmt.getExpression();

        assertNotNull(exp.getCondition());
        testInfixExpression((InfixExpression) exp.getCondition(), "x", ">", "y");

        assertNotNull(exp.getConsequence());

        assertNotNull(exp.getAlternative());
    }


    @Test
    public void testFunctionLiteral() {
        String input = "fn ( x,y ) { return x; } ";
        Program program = parseProgram(input);

        assertEquals(1, program.getStatements().size());

        assertTrue(program.getStatements().get(0) instanceof ExpressionStatement);

        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);

        assertTrue(stmt.getExpression() instanceof FunctionLiteral);

        FunctionLiteral exp = (FunctionLiteral) stmt.getExpression();

        assertEquals(2, exp.getParameters().size());
        assertEquals(1, exp.getBody().getStatements().size());
        assertTrue(exp.getBody().getStatements().get(0) instanceof ReturnStatement);
    }

    @Test
    public void testCallExpression() {
        String input = "add (1, 2+3, 4*5 , foo)";
        Program program = parseProgram(input);

        assertEquals(1, program.getStatements().size());

        assertTrue(program.getStatements().get(0) instanceof ExpressionStatement);

        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);

        assertTrue(stmt.getExpression() instanceof CallExpression);

        CallExpression exp = (CallExpression) stmt.getExpression();

        assertEquals(4, exp.getArguments().size());
        testLiteral(exp.getArguments().get(0), 1);

        testInfixExpression(exp.getArguments().get(1), 2, "+", 3);
        testInfixExpression(exp.getArguments().get(2), 4, "*", 5);
        testLiteral(exp.getArguments().get(3), "foo");
    }
}
