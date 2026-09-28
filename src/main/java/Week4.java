import generated.week4.week4Lexer;
import generated.week4.week4Parser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Week4 {
    public static void main(String[] args) {
        // 从字符串获取字符流。若要从文件读取，可使用 CharStreams.fromFileName("文件路径")
        CharStream charStream = CharStreams.fromString("(1 + 2 * 2 / 3)");
        week4Lexer lexer = new week4Lexer(charStream);

        // 基于词法分析器实例，创建 Token 流
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // 语法分析器
        week4Parser parser = new week4Parser(tokens);
        // 从规则 'expression' 开始解析
        ParseTree tree = parser.expr();
        // 把树打印出来看看
        System.out.println(tree.toStringTree(parser));
    }
}