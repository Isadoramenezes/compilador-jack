package br.edu.al.fat.compilers;

import br.edu.al.fat.compilers.CST.CSTNode;
import br.edu.al.fat.compilers.CST.NonTerminalNode;
import br.edu.al.fat.compilers.CST.TerminalNode;
import br.edu.al.fat.compilers.Tokenizer.Token;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static br.edu.al.fat.compilers.Tokenizer.TokenType.IDENTIFIER;
import static br.edu.al.fat.compilers.Tokenizer.TokenType.KEYWORD;

class CSTTest {

    @Test
    public void constroiUmNoTerminalDeKeywordClass() {
        // <keyword>class</keyword>
        Token token = new Token(KEYWORD, "class");
        TerminalNode terminalNode = new TerminalNode(token);
        assert terminalNode.toXML().equals("<keyword>class</keyword>");
    }

    @Test
    public void constroiUmaExpressionQueContemUmTermComUmIdentificador() {
        //  <expression>
        //    <term>
        //      <identifier> x </identifier>
        //    </term>
        // </expression>
        TerminalNode x = new TerminalNode(new Token(IDENTIFIER, "x"));

        ArrayList<CSTNode> filhosDeTerm = new ArrayList<>();
        filhosDeTerm.add(x);
        NonTerminalNode term = new NonTerminalNode("term", filhosDeTerm);

        List<CSTNode> filhosDeExpression = new ArrayList<>();
        filhosDeExpression.add(term);
        NonTerminalNode expression = new NonTerminalNode("expression", filhosDeExpression);

        assert expression.toXML().equals("<expression><term><identifier>x</identifier></term></expression>");
    }

    @Test
    void constroiArvoreSintaticaDeUmClasseVazia() {
        String code = "class NomeDaClasse { }";
        Tokenizer tokenizer = new Tokenizer();
        List<Token> tokens = tokenizer.tokenize(code);

        Analyzer analyzer = new Analyzer(tokens);
        CSTNode node = analyzer.parse();

        String xml = node.toXML();
        System.out.println(xml);
    }

}
