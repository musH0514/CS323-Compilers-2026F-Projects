import generated.week3.week3;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import java.io.IOException;

public class Week3 {
    public static void main(String[] args) throws IOException {
        CharStream charStream = CharStreams.fromFileName("src/main/java/week3.in");
        week3 lexer = new week3(charStream);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

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