import generated.week12.week12Lexer;
import generated.week12.week12Parser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class Week12 {
    public static void main(String[] args) {
        CharStream input = null;
        try {
            input = CharStreams.fromFileName("testcases/llvm1/base2.splc");
            week12Lexer lexer = new week12Lexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            week12Parser parser = new week12Parser(tokens);
            week12Parser.ProgramContext program = parser.program();
            LLVMVisitor visitor = new LLVMVisitor();
            visitor.visit(program);
            String llvm = visitor.getLLVM();
            Files.writeString(Path.of("testcases/llvm1/result/base.ll"), llvm);
            System.out.println("written to testcases/llvm1/result/base.ll");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}