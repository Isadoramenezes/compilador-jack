package br.edu.al.fat.compilers;

import br.edu.al.fat.compilers.Tokenizer.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

class AnalyzerTest {

    @Test
    public void classeVazia() {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize("class MinhaClasse { }");

        Analyzer analyzer = new Analyzer(tokens);
        analyzer.parse();
    }

    @Test
    public void classeSemOSimboloDeFechaChaves() {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize("class MinhaClasse { ");

        Analyzer analyzer = new Analyzer(tokens);
        try {
            analyzer.parse();
            assert false;
        } catch (RuntimeException ex) {
            // Ok!
        }
    }

    @Test
    public void classeComUmaVariavelIntEstatica() {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(
                "class MinhaClasse { static int x; }"
        );

        Analyzer analyzer = new Analyzer(tokens);
        analyzer.parse();
    }

    @Test
    public void classeComUmaVariavelIntEstaticaCujoNomeEInvalido() {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(
                "class MinhaClasse { static int char; }"
        );

        Analyzer analyzer = new Analyzer(tokens);
        try {
            analyzer.parse();
            assert false;
        } catch (RuntimeException ex) {
            // Ok!
        }
    }

    @Test
    public void classeComUmaDeclaracaoDeTresVariaveisEstaticasInteiras() {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(
                "class MinhaClasse { static int x, y, z; }"
        );

        Analyzer analyzer = new Analyzer(tokens);
        analyzer.parse();
    }

    @Test
    public void classeComVariasDeclaracoesDeVariaveisEstaticasEDeInstancia() {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(
                "class MinhaClasse {" +
                "static int x, y, z;" +
                "static char z;" +
                "field boolean w, a;" +
                "}"
        );

        Analyzer analyzer = new Analyzer(tokens);
        analyzer.parse();
    }

    @Test
    public void classeComDeclaracaoDeFuncaoComDoisArgumentos() {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(
                "class MinhaClasse {" +
                "function int f(char x1, char x2) {" +
                "} " +
                "}"
        );

        Analyzer analyzer = new Analyzer(tokens);
        analyzer.parse();
    }

    @Test
    public void classeComDeclaracaoDeFuncaoComVirgulaFaltandoEntreOsArgmentos() {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(
                "class MinhaClasse {" +
                "function int f(char x1 char x2) {" +
                "} " +
                "}"
        );

        Analyzer analyzer = new Analyzer(tokens);
        try {
            analyzer.parse();
            assert false;
        } catch (RuntimeException ex) {
            // Ok!
        }
    }

    @Test
    public void classeComDeclaracaoDeFuncaoComMultiplasDeclaracoesDeVariaveisLocais() {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(
                "class MinhaClasse {" +
                "function int f() {" +
                "var int x, y;" +
                "var char c;" +
                "} " +
                "}"
        );

        Analyzer analyzer = new Analyzer(tokens);
        analyzer.parse();
    }

    @Test
    public void f() {
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(
                "class MinhaClasse {" +
                "field int a;" +
                "field int b;" +
                "function int soma (int x, int y){" +
                "return x + y;" +
                "}" +
                "}"
        );
        Analyzer analyzer = new Analyzer(tokens);
        analyzer.parse();
    }
}
