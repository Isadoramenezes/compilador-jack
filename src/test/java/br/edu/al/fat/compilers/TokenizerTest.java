package br.edu.al.fat.compilers;

import br.edu.al.fat.compilers.Tokenizer.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static br.edu.al.fat.compilers.Tokenizer.TokenType.*;

class TokenizerTest {

    @Test
    public void testeDeClasseVazia() {
        final Tokenizer tokenizer = new Tokenizer();
        final String code = "class Main { }";
        final List<Token> tokens = tokenizer.tokenize(code);

        assert tokens.size() == 4;
        assert tokens.get(0).equals(new Token(KEYWORD, "class"));
        assert tokens.get(1).equals(new Token(IDENTIFIER, "Main"));
        assert tokens.get(2).equals(new Token(SYMBOL, "{"));
        assert tokens.get(3).equals(new Token(SYMBOL, "}"));
    }

    @Test
    public void testeDeStringConstant() {
        final Tokenizer tokenizer = new Tokenizer();
        final String code = "\"Hello World!\"";
        final List<Token> tokens = tokenizer.tokenize(code);

        assert tokens.size() == 1;
        assert tokens.get(0).type == STRING_CONSTANT;
    }

    @Test
    public void testeDeUmIdentificadorQuePareceUmaPalavraChave() {
        final Tokenizer tokenizer = new Tokenizer();
        final String code = "class1 { }";
        final List<Token> tokens = tokenizer.tokenize(code);

        assert tokens.size() == 3;
        assert tokens.get(0).type == IDENTIFIER;
        assert tokens.get(1).type == SYMBOL;
        assert tokens.get(2).type == SYMBOL;
    }

    @Test
    public void testeIdentificadorComUnderscore() {
        final Tokenizer tokenizer = new Tokenizer();
        final String code = "class uma_nova_classe_muito_bonitinha {" +
        "\n}";
        final List<Token> tokens = tokenizer.tokenize(code);

        assert tokens.size() == 4;
    }

    @Test
    public void testeComentarioSimples() {
        final Tokenizer tokenizer = new Tokenizer();
        final String code = "class Minha_Classe {" +
        "// eu sou um comentario simples que acaba aqui \n" +
        "}";
        final List<Token> tokens = tokenizer.tokenize(code);
        assert tokens.size() == 4;

    }
    @Test
    public void testeComentarioSimplesQuebrado() {
        final Tokenizer tokenizer = new Tokenizer();
        final String code = "class Minha_Classe {" +
        "// eu sou um comentario simples que tenta acabar \n no lugar errado \n" +
        "}";
        final List<Token> tokens = tokenizer.tokenize(code);
        assert tokens.size() > 4;
    }
    @Test
    public void testeComentarioComQuebra() {
        final Tokenizer tokenizer = new Tokenizer();
        final String code = "class Minha_Classe {" +
        "/* eu sou um comentario com quebra de linha \n"+
        " que acaba aqui  teste \n"+
        "*/" +
        "}";
        final List<Token> tokens = tokenizer.tokenize(code);
        assert tokens.size() == 4;

    }
}
