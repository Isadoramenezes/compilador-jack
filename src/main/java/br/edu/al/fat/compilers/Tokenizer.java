package br.edu.al.fat.compilers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tokenizer {

    public static final List<String> keywords = List.of("class", "constructor", "function", "method", "field", "static", "var", "int", "char", "boolean", "void", "true", "false", "null", "this", "let", "do", "if", "else", "while", "return", "const");

    public static final List<Character> symbols = List.of('{', '}', '(', ')', ',', '[', ']', '.', ',', ';', '+', '-', '*', '/', '&', '~', '|', '<', '>', '=');

    public Tokenizer() {
    }

    public List<Token> tokenize(String code) {
        code += " ";
        int length = code.length();
        List<Token> tokens = new ArrayList<>();
        for (int current = 0; current < length; current++) {
            // StringConstant
            if (code.charAt(current) == '"') {
                int stringStart = current++;
                int stringEnd = -1;
                while (current < length) {
                    if (code.charAt(current) == '"') {
                        stringEnd = current;
                        break;
                    } else if (code.charAt(stringStart) == '\n') {
                        throw new RuntimeException("Quebra de linha encontrada dentro de uma StringConstant");
                    } else {
                        current++;
                    }
                }
                tokens.add(new Token(TokenType.STRING_CONSTANT, code.substring(stringStart + 1, stringEnd)));
                continue;
            }
            // Symbol
            if (symbols.contains(code.charAt(current))) {
                tokens.add(new Token(TokenType.SYMBOL, code.charAt(current) + ""));
                continue;
            }
            // Keyword
            for (String keyword : keywords) {
                if (code.startsWith(keyword, current)) {
                    int keywordLength = keyword.length();
                    if (!Character.isLetterOrDigit(code.charAt(current + keywordLength))) {
                        tokens.add(new Token(TokenType.KEYWORD, keyword));
                        current += keywordLength;
                        continue;
                    }
                }
            }
            // Identifier
            if (Character.isLetter(code.charAt(current)) ) {
                int identifierStart = current;
                int identifierEnd = identifierStart + 1;
                 while (Character.isLetterOrDigit(code.charAt(current++))){
                    if (code.charAt(current) == '_'){ 
                        identifierEnd ++;
                        while (Character.isLetterOrDigit(code.charAt(current++))){
                            identifierEnd++;
                        }
                    }
                    identifierEnd++;
                } 
                
                tokens.add(new Token(TokenType.IDENTIFIER, code.substring(identifierStart, identifierEnd-1)));
                current = identifierEnd-2;
                continue;
            }
            // IntegerConstant
            if (Character.isDigit(code.charAt(current))) {
                int intStart = current;
                int intEnd = current + 1;

                while (Character.isDigit(code.charAt(intEnd++))) ;
                tokens.add(new Token(TokenType.INTEGER_CONSTANT, code.substring(intStart, intEnd - 1)));
                current = intEnd - 2;
                continue;
            }
        }

        return tokens;
    }

    public String toXML(List<Token> tokens) {
        String xml = "<tokens>\n";
        for (Token token : tokens) {
            xml += token.toXML();
            xml += "\n";
        }
        xml += "</tokens>";
        return xml;
    }

    public enum TokenType {
        STRING_CONSTANT("stringConst"), SYMBOL("symbol"), KEYWORD("keyword"), IDENTIFIER("identifier"), INTEGER_CONSTANT("intConst");

        public final String tag;

        TokenType(String type) {
            this.tag = type;
        }
    }

    public static class Token {
        public final TokenType type;
        public final String lexeme;

        public Token(TokenType type, String lexeme) {
            this.type = type;
            this.lexeme = lexeme;
        }

        public String toXML() {
            return "<" + type.tag + ">" + lexeme + "</" + type.tag + ">";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Token token = (Token) o;
            return type == token.type &&
                   Objects.equals(lexeme, token.lexeme);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, lexeme);
        }

        @Override
        public String toString() {
            return "Token{" +
                   "type=" + type +
                   ", lexeme='" + lexeme + '\'' +
                   '}';
        }
    }
}
