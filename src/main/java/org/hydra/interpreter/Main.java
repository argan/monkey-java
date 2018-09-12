package org.hydra.interpreter;

import org.hydra.interpreter.repl.REPL;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Monkey Programming Language!");
        new REPL(System.in,System.out).start();
    }
}
