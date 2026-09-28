import generated.week1.week1Lexer;
import generated.week1.week1Parser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Week1 {
    public static void main(String[] args) {
        // 把要解析的字符串喂给 ANTLR
        CharStream input = CharStreams.fromString("1 + 2");
        // 词法分析器
        week1Lexer lexer = new week1Lexer(input);
        // Token 流
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // 语法分析器
        week1Parser parser = new week1Parser(tokens);
        // 从规则 'expression' 开始解析
        ParseTree tree = parser.expr();
        // 把树打印出来看看
        System.out.println(tree.toStringTree(parser));
    }
}