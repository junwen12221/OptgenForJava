package cn.lightfish.optgen;

public enum Token {
    ILLEGAL,
    EOF,
    STRING,
    WHITESPACE,
    COMMENT,
    LPAREN,
    RPAREN,
    LBRACKET,
    LBRACE,
    DOLLAR,
    ASTERISK,
    ARROW,
    AMPERSAND,
    COMMA,
    ELLIPSES,
    PIPE,
    RBRACKET,
    RBRACE, COLON, CARET,
    EQUALS, IDENT, NUMBER, ERROR;
}
