import generated.week7.week7Lexer;
import generated.week7.week7Parser;
import impl.Scope;
import impl.SplcSemanticError;
import impl.symbol.FunctionSymbol;
import impl.symbol.VariableSymbol;
import org.antlr.v4.runtime.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Week7 {
    public static void main(String[] args) {
        Path testcaseDirectory = Paths.get("testcases", "symbol");
        try (Stream<Path> paths = Files.list(testcaseDirectory)) {
            List<Path> testcaseFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".splc"))
                    .sorted()
                    .collect(Collectors.toList());

            for (Path testcaseFile : testcaseFiles) {
                CharStream input = CharStreams.fromPath(testcaseFile);
                week7Lexer lexer = new week7Lexer(input);
                CommonTokenStream tokens = new CommonTokenStream(lexer);
                week7Parser parser = new week7Parser(tokens);
                week7Parser.ProgramContext program = parser.program();
                Week7Visitor visitor = new Week7Visitor();
                visitor.visit(program);
                List<SplcSemanticError> errors = visitor.getErrors();
                if (errors.isEmpty()) {
                    Scope globalScope = visitor.getGlobalScope();
                    printGlobalDeclarations(globalScope);
                } else {
                    for(SplcSemanticError error : errors) {
                        System.err.println(error.printBase());
                    }
                }
                System.out.println();
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