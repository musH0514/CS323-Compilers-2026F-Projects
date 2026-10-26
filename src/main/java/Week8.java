import generated.week8.week8Lexer;
import generated.week8.week8Parser;
import impl.Scope;
import impl.SplcSemanticError;
import impl.symbol.FunctionSymbol;
import impl.symbol.VariableSymbol;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;
import java.util.List;


public class Week8 {
    public static void main(String[] args) {
        CharStream input = null;
        try {
            input = CharStreams.fromFileName("testcases/semantic1/base2.splc");
            week8Lexer lexer = new week8Lexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            week8Parser parser = new week8Parser(tokens);
            week8Parser.ProgramContext program = parser.program();
//            visitorTraverse(program);
            listenerTraverse(program);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void visitorTraverse(week8Parser.ProgramContext program) {
        SyntaxVisitor visitor = new SyntaxVisitor();
        visitor.visit(program);
        List<SplcSemanticError> errors = visitor.getErrors();
        if (errors.isEmpty()) {
            Scope globalScope = visitor.getGlobalScope();
            printGlobalDeclarations(globalScope);
        } else {
            for (SplcSemanticError error : errors) {
                System.err.println(error.printBase());
            }
        }
    }

    public static void listenerTraverse(week8Parser.ProgramContext program) {
        ParseTreeWalker walker = new ParseTreeWalker();
        SyntaxListener listener = new SyntaxListener();
        walker.walk(listener, program);
        List<SplcSemanticError> errors = listener.getErrors();
        if (errors.isEmpty()) {
            Scope globalScope = listener.getGlobalScope();
            printGlobalDeclarations(globalScope);
        } else {
            for (SplcSemanticError error : errors) {
                System.err.println(error.printBase());
            }
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