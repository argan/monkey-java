package org.hydra.interpreter.repl;

import org.hydra.interpreter.ast.Program;
import org.hydra.interpreter.evaluator.Evaluator;
import org.hydra.interpreter.lexer.Lexer;
import org.hydra.interpreter.object.Environment;
import org.hydra.interpreter.object.MObject;
import org.hydra.interpreter.parser.Parser;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class REPL {
    private final InputStream in;
    private final PrintStream out;

    public REPL(InputStream in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    public void start() {
        Scanner scanner = new Scanner(in);
        Environment env = new Environment();
        do {
            out.print("> ");
            String line = scanner.nextLine();
            if (".quit".equals(line) || ".exit".equals(line)) {
                out.println("Bye...");
                System.exit(0);
            } else {
                Parser parser = new Parser(new Lexer(line));
                Program program = parser.parseProgram();
                if (parser.errors().size()>0) {
                    for(String msg: parser.errors()) {
                        out.println("ERROR:" + msg);
                    }
                    continue;
                }
                MObject result = Evaluator.eval(program, env);
                if(result != null) {
                    out.println(result);
                }
            }
        } while (true);
    }
}
