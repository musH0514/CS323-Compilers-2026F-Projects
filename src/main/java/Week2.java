import generated.week2.week2Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

public class Week2 {
    public static void main(String[] args) {
        CharStream charStream =
                CharStreams.fromString("257.016.299.233");   //IP地址识别
//257.016.003.233 257.016.299.233 010.016.124.233
        week2Lexer lexer = new week2Lexer(charStream);

        CommonTokenStream tokens =
                new CommonTokenStream(lexer);

        tokens.fill();

        for (Token token : tokens.getTokens()) {
            // 不输出 EOF
            if (token.getType() == Token.EOF) {
                continue;
            }

            String tokenType =
                    lexer.getVocabulary()
                            .getSymbolicName(token.getType());

            System.out.printf(
                    "TokenType: %s, Lexeme: %s%n",
                    tokenType,
                    token.getText()
            );
        }
    }
}