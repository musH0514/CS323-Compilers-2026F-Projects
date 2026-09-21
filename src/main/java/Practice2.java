import generated.Practice2.Practice2Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import java.io.IOException;

public class Practice2 {
    public static void main(String[] args) throws IOException {
        CharStream charStream = CharStreams.fromFileName("src/main/java/practice2.in");
        Practice2Lexer lexer = new Practice2Lexer(charStream);

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