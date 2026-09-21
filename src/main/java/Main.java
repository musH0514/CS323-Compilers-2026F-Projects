import generated.Calc1.Calc1Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

public class Main {
    public static void main(String[] args) {
        // 从字符串获取字符流。若要从文件读取，可使用 CharStreams.fromFileName("文件路径")
        CharStream charStream = CharStreams.fromString("1 + 2");
        Calc1Lexer lexer = new Calc1Lexer(charStream);

        // 基于词法分析器实例，创建 Token 流
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 调用 fill() 方法，让词法分析器开始工作，填充 Token 流
        tokens.fill();

        // 打印 Token 流信息
        for (Token token : tokens.getTokens()) {
            System.out.println(token.toString());
        }
    }
}