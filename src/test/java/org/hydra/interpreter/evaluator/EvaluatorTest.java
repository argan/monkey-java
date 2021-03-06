package org.hydra.interpreter.evaluator;


import com.sun.org.apache.xpath.internal.operations.Bool;
import org.hydra.interpreter.ast.Program;
import org.hydra.interpreter.lexer.Lexer;
import org.hydra.interpreter.object.*;
import org.hydra.interpreter.parser.Parser;
import org.junit.Test;

import static org.junit.Assert.*;

public class EvaluatorTest {

    @Test
    public void testEvalExpression() {
        Object[][] expectes = new Object[][]{
                {"5", 5},
                {"10;", 10},
                {"-10;", -10},
                {"-5;", -5},
                {"true;", true},
                {"false;", false},
                {"!false;", true},
                {"!!false;", false},
                {"!true;", false},
                {"!!true;", true},
                {"!5;", false},
                {"5 + 5 + 5 + 5 - 10", 10},
                {"2 * 2 * 2 * 2 * 2", 32},
                {"-50 + 100 + -50", 0},
                {"5 * 2 + 10", 20},
                {"5 + 2 * 10", 25},
                {"20 + 2 * -10", 0},
                {"50 / 2 * 2 + 10", 60},
                {"2 * (5 + 10)", 30},
                {"3 * 3 * 3 + 10", 37},
                {"3 * (3 * 3) + 10", 37},
                {"(5 + 10 * 2 + 15 / 3) * 2 + -10", 50},
                {"1 < 2", true},
                {"1 > 2", false},
                {"1 < 1", false},
                {"1 > 1", false},
                {"1 == 1", true},
                {"1 != 1", false},
                {"1 == 2", false},
                {"1 != 2", true},
                {"true == true", true},
                {"false == false", true},
                {"true == false", false},
                {"true != false", true},
                {"false != true", true},
                {"(1 < 2) == true", true},
                {"(1 < 2) == false", false},
                {"(1 > 2) == true", false},
                {"(1 > 2) == false", true},
                {"if (true) { 10 }", 10},
                {"if (false) { 10 }", null},
                {"if (1) { 10 }", 10},
                {"if (1 < 2) { 10 }", 10},
                {"if (1 > 2) { 10 }", null},
                {"if (1 > 2) { 10 } else { 20 }", 20},
                {"if (1 < 2) { 10 } else { 20 }", 10},
                {"return 10;", 10},
                {"return 10; 9;", 10},
                {"return 2 * 5; 9;", 10},
                {"9; return 2 * 5; 9;", 10},
                {"if(10>1) { if (10>1) { return 10;} return 1;}", 10},
                {"let a = 5; a;", 5},
                {"let a = 5 * 5; a;", 25},
                {"let a = 5; let b = a; b;", 5},
                {"let a = 5; let b = a; let c = a + b + 5; c;", 15},
                {"let identity = fn(x) { x; }; identity(5);", 5},
                {"let identity = fn(x) { return x; }; identity(5);", 5},
                {"let double = fn(x) { x * 2; }; double(5);", 10},
                {"let add = fn(x, y) { x + y; }; add(5, 5);", 10},
                {"let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20},
                {"fn(x) { x; }(5)", 5},
                {"\"hello\" + \" world\"", "hello world"},
                {"[1, 2, 3][0]", 1,},
                {"[1, 2, 3][1]", 2,},
                {"[1, 2, 3][2]", 3,},
                {"let i = 0; [1][i];", 1,},
                {"[1, 2, 3][1 + 1];", 3,},
                {"let myArray = [1, 2, 3]; myArray[2];", 3,},
                {"let myArray = [1, 2, 3]; myArray[0] + myArray[1] + myArray[2];", 6,},
                {"let myArray = [1, 2, 3]; let i = myArray[0]; myArray[i]", 2,},
                {"[1, 2, 3][3]", null,},
                {"[1, 2, 3][-1]", null,},
                {"{\"foo\": 5}[\"foo\"]", 5,},
                {"{\"foo\": 5}[\"bar\"]", null,},
                {"let key = \"foo\"; {\"foo\": 5}[key]", 5,},
                {"{}[\"foo\"]", null,},
                {"{5: 5}[5]", 5,},
                {"{true: 5}[true]", 5,},
                {"{false: 5}[false]", 5,},
        };

        for (Object[] arr : expectes) {
            MObject obj = testEval((String) arr[0]);
            testLiteralObject((String) arr[0], obj, arr[1]);
        }
    }

    @Test
    public void testErrorHandling() {
        String[][] expectes = new String[][]{
                {"5 + true;", "type mismatch: INTEGER_OBJ + BOOLEAN_OBJ",},
                {"5 + true; 5;", "type mismatch: INTEGER_OBJ + BOOLEAN_OBJ",},
                {"-true", "unknown operator: -BOOLEAN_OBJ",},
                {"true + false;", "unknown operator: BOOLEAN_OBJ + BOOLEAN_OBJ",},
                {"5; true + false; 5", "unknown operator: BOOLEAN_OBJ + BOOLEAN_OBJ",},
                {"if (10 > 1) { true + false; }",
                        "unknown operator: BOOLEAN_OBJ + BOOLEAN_OBJ",},
                {"if (10 > 1) {  if (10 > 1) {    return true + false;} return 1;}",
                        "unknown operator: BOOLEAN_OBJ + BOOLEAN_OBJ",},
                {"foobar", "identifier not found: foobar",},
                {"{\"name\": \"Monkey\"}[fn(x) { x }];",
                        "unusable as hash key: FUNCTION_OBJ",},

        };

        for (String[] arr : expectes) {
            MObject obj = testEval(arr[0]);
            assertTrue(arr[0], obj instanceof MError);
            MError err = (MError) obj;
            assertEquals(arr[0], arr[1], err.getMessage());
        }
    }

    private void testLiteralObject(String msg, MObject obj, Object i) {
        assertNotNull(msg, obj);
        if (i == null) {
            assertTrue(msg, obj instanceof MNull);
        } else if (i instanceof Integer) {
            assertEquals(msg, ObjectType.INTEGER_OBJ, obj.type());
            assertEquals(i, ((MInteger) obj).getValue());
        } else if (i instanceof Boolean) {
            assertTrue(msg, ObjectType.BOOLEAN_OBJ == obj.type());
            assertEquals(msg, i, ((MBoolean) obj).getValue());
        } else if (i instanceof String) {
            assertTrue(msg, ObjectType.STRING_OBJ == obj.type());
            assertEquals(msg, i, ((MString) obj).getValue());
        }

    }

    private MObject testEval(String input) {
        Parser parser = new Parser(new Lexer(input));
        Program program = parser.parseProgram();

        return Evaluator.eval(program, new Environment());
    }
}
