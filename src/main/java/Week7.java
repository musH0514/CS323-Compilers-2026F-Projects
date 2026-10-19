import generated.week7.week7Lexer;
import generated.week7.week7Parser;
import impl.Scope;
import impl.SplcSemanticError;
import impl.symbol.FunctionSymbol;
import impl.symbol.VariableSymbol;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.util.List;

public class Week7 {
    public static void main(String[] args) {
        CharStream input = null;
        try {
            input = CharStreams.fromFileName("testcases/symbol/base3.splc");
            week7Lexer lexer = new week7Lexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            week7Parser parser = new week7Parser(tokens);
            week7Parser.ProgramContext program = parser.program();
            Week7Visitor visitor = new Week7Visitor();
            visitor.visit(program);
            List<SplcSemanticError> errors = visitor.getErrors();
            if(errors.isEmpty()) {
                Scope globalScope = visitor.getGlobalScope();
                printGlobalDeclarations(globalScope);
            }else{
                for(SplcSemanticError error : errors) {
                    System.err.println(error.printBase());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void printGlobalDeclarations(Scope globalScope) {
        System.out.println("Variables:");
        for (VariableSymbol v : globalScope.getVariables().values()) {
            System.out.println(v.getName() + ": " + v.getType());
        }
        System.out.println();
        System.out.println("Functions:");
        for (FunctionSymbol f : globalScope.getFunctions().values()) {
            System.out.println(f);
        }
    }
}