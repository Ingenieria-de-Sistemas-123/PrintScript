package org.printscript.token

enum class TokenType {
    LET,
    PRINTLN,
    IDENTIFIER,
    NUMBER,
    STRING,
    EQUAL,
    STRING_TYPE,
    NUMBER_TYPE,
    PLUS,
    MINUS,
    STAR,
    SLASH,
    SEMICOLON,
    COLON,
    OPEN_PAREN,
    CLOSE_PAREN,
    EOF //esto nos indica que llegamos al final del archivo
}
