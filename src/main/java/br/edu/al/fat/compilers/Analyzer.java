package br.edu.al.fat.compilers;

import br.edu.al.fat.compilers.CST.CSTNode;
import br.edu.al.fat.compilers.Tokenizer.Token;
import br.edu.al.fat.compilers.Tokenizer.TokenType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import static br.edu.al.fat.compilers.Tokenizer.TokenType.*;

public class Analyzer {

    public static final Token FUNCTION = new Token(KEYWORD, "function");
    public static final Token METHOD = new Token(KEYWORD, "method");
    public static final Token LPAR = new Token(SYMBOL, "(");
    public static final Token RPAR = new Token(SYMBOL, ")");
    public static final Token RBRACES = new Token(SYMBOL, "}");
    public static final Token VAR = new Token(KEYWORD, "var");
    public static final Token SEMICOLON = new Token(SYMBOL, ";");
    public static final Token RETURN = new Token(KEYWORD, "return");
    public static final Token LET = new Token(KEYWORD, "let");
    public static final Token IF = new Token(KEYWORD, "if");
    public static final Token WHILE = new Token(KEYWORD, "while");
    public static final Token DO = new Token(KEYWORD, "do");
    public static final Token ASSIGNMENT = new Token(SYMBOL, "=");
    public static final Token LBRACKETS = new Token(SYMBOL, "[");
    public static final Token RBRACKETS = new Token(SYMBOL, "]");
    public static final Token ELSE = new Token(KEYWORD, "else");
    public static final Token COMMA = new Token(SYMBOL, ",");
    public static final Token INT = new Token(KEYWORD, "int");
    public static final Token CHAR = new Token(KEYWORD, "char");
    public static final Token BOOLEAN = new Token(KEYWORD, "boolean");
    public static final Token STATIC = new Token(KEYWORD, "static");
    public static final Token FIELD = new Token(KEYWORD, "field");
    public static final Token CONST = new Token(KEYWORD, "const");
    public static final Token CONSTRUCTOR = new Token(KEYWORD, "constructor");
    public static final Token VOID = new Token(KEYWORD, "void");
    public static final Token CLASS = new Token(KEYWORD, "class");
    public static final Token LBRACES = new Token(SYMBOL, "{");
    public static final Token DOT = new Token(SYMBOL, ".");
    public static final Token PLUS = new Token(SYMBOL, "+");
    public static final Token MINUS = new Token(SYMBOL, "-");
    public static final Token TIMES = new Token(SYMBOL, "*");
    public static final Token DIV = new Token(SYMBOL, "/");
    public static final Token EQUALS = new Token(SYMBOL, "=");
    public static final Token GT = new Token(SYMBOL, ">");
    public static final Token LT = new Token(SYMBOL, "<");
    public static final Token OR = new Token(SYMBOL, "|");
    public static final Token AND = new Token(SYMBOL, "&");
    public static final Token TRUE = new Token(KEYWORD, "true");
    public static final Token FALSE = new Token(KEYWORD, "false");
    public static final Token NULL = new Token(KEYWORD, "null");
    public static final Token THIS = new Token(KEYWORD, "this");
    public static final Token NOT = new Token(SYMBOL, "~");
    private final LinkedList<Token> tokens;

    public Analyzer(List<Token> tokens) {
        this.tokens = new LinkedList<>(tokens);
    }

    public CSTNode parse() {
        CSTNode root = compileClass();
        if (!tokens.isEmpty()) {
            throw new RuntimeException("Erro: tokens remancescentes após a compilação da classe: " + tokens.toString());
        }
        return root;
    }

    public Token consume(Token expected) {
        Token nextToken = tokens.removeFirst();
        if (!nextToken.equals(expected)) {
            throw new RuntimeException("Erro: era esperado o token " + expected + " mas encontrei o token " + nextToken);
        }
        return nextToken;
    }

    public Token consume(TokenType expected) {
        Token nextToken = tokens.removeFirst();
        if (nextToken.type != expected) {
            throw new RuntimeException("Erro: era esperado o tipo de token " + expected + " mas encontrei o tipo de token " + nextToken.type);
        }
        return nextToken;
    }

    private CSTNode consumeIntoCSTNode(TokenType expected) {
        return CST.makeTerminalNode(consume(expected));
    }

    private CSTNode consumeIntoCSTNode(Token expected) {
        return CST.makeTerminalNode(consume(expected));
    }

    public Token lookAhead(int i) {
        return tokens.get(i);
    }

    private CSTNode compileClass() {
        List<CSTNode> children = new ArrayList<>();
        // 'class'
        children.add(consumeIntoCSTNode(CLASS));
        // className
        children.add(consumeIntoCSTNode(IDENTIFIER));
        // '{'
        children.add(consumeIntoCSTNode(LBRACES));
        // classVarDec*
        while (true) {
            if (tokens.peek().equals(STATIC) ||
                tokens.peek().equals(FIELD)) {
                children.add(compileClassVarDec());
            } else {
                break;
            }
        }
        // subroutineDec*
        while (true) {
            if (tokens.peek().equals(CONSTRUCTOR) ||
                tokens.peek().equals(FUNCTION) ||
                tokens.peek().equals(METHOD)) {
                children.add(compileSubroutineDec());
            } else {
                break;
            }
        }
        // '}'
        children.add(consumeIntoCSTNode(RBRACES));

        return CST.classNode(children);
    }

    private CSTNode compileSubroutineDec() {
        List<CSTNode> children = new ArrayList<>();
        // 'constructor' | 'function' | 'method'
        if (tokens.peek().equals(CONSTRUCTOR)) {
            children.add(consumeIntoCSTNode(CONSTRUCTOR));
        } else if (tokens.peek().equals(FUNCTION)) {
            children.add(consumeIntoCSTNode(FUNCTION));
        } else if (tokens.peek().equals(METHOD)) {
            children.add(consumeIntoCSTNode(METHOD));
        } else {
            throw new RuntimeException("Parse error: expected constructor, function or method but got " + tokens.peek().lexeme);
        }
        // 'void' | type
        if (tokens.peek().equals(VOID)) {
            children.add(consumeIntoCSTNode(VOID));
        } else {
            children.add(compileType());
        }
        // subroutineName
        children.add(consumeIntoCSTNode(IDENTIFIER));
        // '('
        children.add(consumeIntoCSTNode(LPAR));
        // parameterList
        children.add(compileParameterList());
        // ')'
        children.add(consumeIntoCSTNode(RPAR));
        // subroutineBody
        children.add(compileSubroutineBody());

        return CST.subroutineDecNode(children);
    }

    private CSTNode compileSubroutineBody() {
        List<CSTNode> children = new ArrayList<>();

        // '{'
        children.add(consumeIntoCSTNode(LBRACES));
        // varDec*
        while (tokens.peek().equals(VAR)) {
            children.add(compileVarDec());
        }
        // statements
        children.add(compileStatements());
        // '}'
        children.add(consumeIntoCSTNode(RBRACES));

        return CST.subroutineDecNode(children);
    }

    private CSTNode compileStatements() {
        List<CSTNode> children = new ArrayList<>();

        // letStatement | ifStatement | whileStatement | doStatement | returnStatement
        while (true) {
            if (tokens.peek().equals(LET)) {
                children.add(compileLetStatement());
            } else if (tokens.peek().equals(IF)) {
                children.add(compileIfStatement());
            } else if (tokens.peek().equals(WHILE)) {
                children.add(compileWhileStatement());
            } else if (tokens.peek().equals(DO)) {
                children.add(compileDoStatement());
            } else if (tokens.peek().equals(RETURN)) {
                children.add(compileReturnStatement());
            } else {
                break;
            }
        }

        return CST.statementsNode(children);
    }

    private CSTNode compileIfStatement() {
        List<CSTNode> children = new ArrayList<>();

        children.add(consumeIntoCSTNode(IF));
        children.add(consumeIntoCSTNode(LPAR));
        children.add(compileExpression());
        children.add(consumeIntoCSTNode(RPAR));
        children.add(consumeIntoCSTNode(LBRACES));
        children.add(compileStatements());
        children.add(consumeIntoCSTNode(RBRACES));
        if (tokens.peek().equals(ELSE)) {
            children.add(consumeIntoCSTNode(ELSE));
            children.add(consumeIntoCSTNode(LBRACES));
            children.add(compileStatements());
            children.add(consumeIntoCSTNode(RBRACES));
        }

        return CST.ifStatementNode(children);
    }

    private CSTNode compileLetStatement() {
        List<CSTNode> children = new ArrayList<>();

        // 'let'
        children.add(consumeIntoCSTNode(LET));
        // varName
        children.add(consumeIntoCSTNode(IDENTIFIER));

        // ('[' expression ']')?
        if (tokens.peek().equals(LBRACKETS)) {
            children.add(consumeIntoCSTNode(LBRACKETS));
            children.add(compileExpression());
            children.add(consumeIntoCSTNode(RBRACKETS));
        }
        // '='
        children.add(consumeIntoCSTNode(EQUALS));
        // expression
        children.add(compileExpression());
        // ';'
        children.add(consumeIntoCSTNode(SEMICOLON));

        return CST.letStatementNode(children);
    }

    private CSTNode compileReturnStatement() {
        List<CSTNode> children = new ArrayList<>();

        // return expresssion? ';'
        children.add(consumeIntoCSTNode(RETURN));
        if (!tokens.peek().equals(SEMICOLON)) {
            children.add(compileExpression());
        }
        children.add(consumeIntoCSTNode(SEMICOLON));

        return CST.returnStatementNode(children);
    }

    private CSTNode compileDoStatement() {
        List<CSTNode> children = new ArrayList<>();

        children.add(consumeIntoCSTNode(DO));
        children.addAll(compileSubroutineCall());
        children.add(consumeIntoCSTNode(SEMICOLON));

        return CST.doStatementNode(children);
    }

    private List<CSTNode> compileSubroutineCall() {
        List<CSTNode> children = new ArrayList<>();

        // subroutineName '(' expressionList ')'
        if (lookAhead(1).equals(LPAR)) {
            children.add(consumeIntoCSTNode(IDENTIFIER));
            children.add(consumeIntoCSTNode(LPAR));
            children.add(compileExpressionList());
            children.add(consumeIntoCSTNode(RPAR));
        }
        // ( className | varName ) '.' subroutineName '(' expressionList ')'
        else if (lookAhead(1).equals(DOT)) {
            children.add(consumeIntoCSTNode(IDENTIFIER));
            children.add(consumeIntoCSTNode(DOT));
            children.add(consumeIntoCSTNode(IDENTIFIER));
            children.add(consumeIntoCSTNode(LPAR));
            children.add(compileExpressionList());
            children.add(consumeIntoCSTNode(RPAR));
        } else {
            throw new RuntimeException("Erro: não foi possível compilar a chamada de subrotina");
        }

        return children;
    }

    private CSTNode compileExpressionList() {
        List<CSTNode> children = new ArrayList<>();

        // ( expression ( ',' expression )* )?
        while (!tokens.peek().equals(RPAR)) {
            children.add(compileExpression());
            if (tokens.peek().equals(COMMA)) {
                children.add(consumeIntoCSTNode(COMMA));
            } else {
                break;
            }
        }

        return CST.expressionListNode(children);
    }

    private CSTNode compileWhileStatement() {
        List<CSTNode> children = new ArrayList<>();

        children.add(consumeIntoCSTNode(WHILE));
        children.add(consumeIntoCSTNode(LPAR));
        children.add(compileExpression());
        children.add(consumeIntoCSTNode(RPAR));
        children.add(consumeIntoCSTNode(LBRACES));
        children.add(compileStatements());
        children.add(consumeIntoCSTNode(RBRACES));

        return CST.whileStatementNode(children);
    }

    private CSTNode compileExpression() {
        List<CSTNode> children = new ArrayList<>();

        // term
        children.add(compileTerm());
        // (term op term)*
        while (Set.of(PLUS, MINUS, TIMES, DIV, AND, OR, LT, GT, EQUALS).contains(tokens.peek())) {
            children.add(consumeIntoCSTNode(SYMBOL));
            children.add(compileTerm());
        }

        return CST.expressionNode(children);
    }

    private CSTNode compileTerm() {
        List<CSTNode> children = new ArrayList<>();

        // integerConstant
        if (tokens.peek().type.equals(INTEGER_CONSTANT)) {
            children.add(consumeIntoCSTNode(INTEGER_CONSTANT));
        }
        // stringConstant
        else if (tokens.peek().type.equals(STRING_CONSTANT)) {
            children.add(consumeIntoCSTNode(STRING_CONSTANT));
        }
        // keywordConstant
        else if (Set.of(TRUE, FALSE, NULL, THIS).contains(tokens.peek())) {
            children.add(consumeIntoCSTNode(KEYWORD));
        }
        // '(' expression ')'
        else if (tokens.peek().equals(LPAR)) {
            children.add(consumeIntoCSTNode(LPAR));
            children.add(compileExpression());
            children.add(consumeIntoCSTNode(RPAR));
        }
        // unaryOp term
        else if (Set.of(PLUS, NOT).contains(tokens.peek())) {
            children.add(consumeIntoCSTNode(SYMBOL));
            children.add(compileTerm());
        }
        // lookahead needed
        else if (tokens.peek().type.equals(IDENTIFIER)) {
            Token lookAhead = lookAhead(1);
            // varName '[' expression ']'
            if (lookAhead.equals(LBRACKETS)) {
                children.add(consumeIntoCSTNode(IDENTIFIER));
                children.add(consumeIntoCSTNode(LBRACKETS));
                children.add(compileExpression());
                children.add(consumeIntoCSTNode(RBRACKETS));
            }
            // subroutineCall
            else if (lookAhead.equals(LPAR) || lookAhead.equals(DOT)) {
                children.addAll(compileSubroutineCall());
            }
            // varName
            else {
                children.add(consumeIntoCSTNode(IDENTIFIER));
            }
        }

        return CST.termNode(children);
    }

    private CSTNode compileVarDec() {
        List<CSTNode> children = new ArrayList<>();

        // 'var'
        children.add(consumeIntoCSTNode(VAR));
        // type
        children.add(compileType());
        // varName
        children.add(consumeIntoCSTNode(IDENTIFIER));
        // (',' varName)*
        while (tokens.peek().equals(COMMA)) {
            children.add(consumeIntoCSTNode(COMMA));
            children.add(consumeIntoCSTNode(IDENTIFIER));
        }
        // ';'
        children.add(consumeIntoCSTNode(SEMICOLON));

        return CST.varDecNode(children);
    }

    private CSTNode compileParameterList() {
        List<CSTNode> children = new ArrayList<>();
        // ( (type varName) (',' type varName)* )?
        while (Set.of(CHAR, INT, BOOLEAN).contains(tokens.peek()) ||
               tokens.peek().type.equals(IDENTIFIER)) {
            children.add(compileType());
            children.add(consumeIntoCSTNode(IDENTIFIER));
            if (tokens.peek().equals(COMMA)) {
                children.add(consumeIntoCSTNode(COMMA));
            } else {
                break;
            }
        }

        return CST.parameterListNode(children);
    }

    private CSTNode compileClassVarDec() {
        List<CSTNode> children = new ArrayList<>();
        // 'static' | 'field'
        if (tokens.peek().equals(STATIC)) {
            children.add(consumeIntoCSTNode(STATIC));
        } else if (tokens.peek().equals(FIELD)) {
            children.add(consumeIntoCSTNode(FIELD));
        } else {
            throw new RuntimeException("Parse error: expected static or final but got " + tokens.peek().lexeme);
        }
        // const ?
        if (tokens.peek().equals(CONST)) {
            children.add(consumeIntoCSTNode(CONST));
        }
        // type
        children.add(compileType());
        // varName
        children.add(consumeIntoCSTNode(IDENTIFIER));
        // (',' varName)*
        while (tokens.peek().equals(COMMA)) {
            children.add(consumeIntoCSTNode(SYMBOL));
            children.add(consumeIntoCSTNode(IDENTIFIER));
        }
        // ';'
        children.add(consumeIntoCSTNode(SEMICOLON));
        return CST.classVarDecNode(children);
    }

    private CSTNode compileType() {
        // 'int' | 'char' | 'boolean' | className
        if (tokens.peek().equals(INT)) {
            return consumeIntoCSTNode(INT);
        } else if (tokens.peek().equals(CHAR)) {
            return consumeIntoCSTNode(CHAR);
        } else if (tokens.peek().equals(BOOLEAN)) {
            return consumeIntoCSTNode(BOOLEAN);
        } else if (tokens.peek().type.equals(IDENTIFIER)) {
            return consumeIntoCSTNode(IDENTIFIER);
        } else {
            throw new RuntimeException("Parse error: expected int, char, boolean or IDENTIFIER but got " + tokens.peek());
        }
    }

}
