package org.printscript.token

enum class TokenType {
    LET,
    CONST,
    IF,
    ELSE,
    PRINTLN,
    READ_INPUT,
    READ_ENV,

    IDENTIFIER,
    NUMBER,
    STRING,
    TRUE,
    FALSE,

    STRING_TYPE,
    NUMBER_TYPE,
    BOOLEAN_TYPE,

    EQUAL,
    PLUS,
    MINUS,
    STAR,
    SLASH,

    SYNTAX,

    EOF,
}
