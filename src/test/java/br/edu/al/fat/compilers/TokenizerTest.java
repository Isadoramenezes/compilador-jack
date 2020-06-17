package br.edu.al.fat.compilers;

import br.edu.al.fat.compilers.Tokenizer.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.edu.al.fat.compilers.Tokenizer.TokenType.*;

class TokenizerTest {

    @Test
    public void testeDeClasseVazia() {
        Tokenizer tokenizer = new Tokenizer();
        String code = "class Main { }";
        List<Token> tokens = tokenizer.tokenize(code);

        assert tokens.size() == 4;
        assert tokens.get(0).equals(new Token(KEYWORD, "class"));
        assert tokens.get(1).equals(new Token(IDENTIFIER, "Main"));
        assert tokens.get(2).equals(new Token(SYMBOL, "{"));
        assert tokens.get(3).equals(new Token(SYMBOL, "}"));
    }

    @Test
    public void testeDeStringConstant() {
        Tokenizer tokenizer = new Tokenizer();
        String code = "\"Hello World!\"";
        List<Token> tokens = tokenizer.tokenize(code);

        assert tokens.size() == 1;
        assert tokens.get(0).type == STRING_CONSTANT;
    }

    @Test
    public void testeDeUmIdentificadorQuePareceUmaPalavraChave() {
        Tokenizer tokenizer = new Tokenizer();
        String code = "class1 { }";
        List<Token> tokens = tokenizer.tokenize(code);

        assert tokens.size() == 3;
        assert tokens.get(0).type == IDENTIFIER;
        assert tokens.get(1).type == SYMBOL;
        assert tokens.get(2).type == SYMBOL;
    }

}
