package br.edu.al.fat.compilers;

import br.edu.al.fat.compilers.Tokenizer.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CST {

    static public CSTNode makeNonTerminalNode(String name, Collection<CSTNode> children) {
        return new NonTerminalNode(name, children);
    }

    static public CSTNode makeTerminalNode(Token token) {
        return new TerminalNode(token);
    }

    static public CSTNode classNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("class", children);
    }

    static public CSTNode classVarDecNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("classVarDec", children);
    }

    static public CSTNode subroutineDecNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("subroutineDec", children);
    }

    static public CSTNode parameterListNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("parameterList", children);
    }

    static public CSTNode varDecNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("varDec", children);
    }

    static public CSTNode statementsNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("statements", children);
    }

    static public CSTNode letStatementNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("letStatement", children);
    }

    static public CSTNode ifStatementNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("ifStatement", children);
    }

    static public CSTNode whileStatementNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("whileStatement", children);
    }

    static public CSTNode doStatementNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("doStatement", children);
    }

    static public CSTNode returnStatementNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("returnStatement", children);
    }

    static public CSTNode expressionNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("expression", children);
    }

    static public CSTNode termNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("term", children);
    }

    static public CSTNode expressionListNode(Collection<CSTNode> children) {
        return makeNonTerminalNode("expressionList", children);
    }

    interface CSTNode {
        String toXML();
    }

    //  <expression>
//   <term>
//     <identifier> x </identifier>
//   </term>
//</expression>
    static class NonTerminalNode implements CSTNode {
        private String name;
        private List<CSTNode> children;

        public NonTerminalNode(String name, Collection<CSTNode> children) {
            this.name = name;
            this.children = new ArrayList<>(children);
        }

        public String toXML() {
            String result = "<" + name + ">";
            for (CSTNode node : children) {
                result += node.toXML();
            }
            result += "</" + name + ">";
            return result;
        }
    }

    // <keyword> class </keyword>
//  -------          -------
//    nome             nome
    static class TerminalNode implements CSTNode {
        private final Token token;

        TerminalNode(Token token) {
            this.token = token;
        }

        public String toXML() {
            return "<" + token.type.tag + ">" + token.lexeme + "</" + token.type.tag + ">";
        }
    }

}
