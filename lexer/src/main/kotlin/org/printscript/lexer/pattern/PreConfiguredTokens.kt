package org.printscript.lexer.pattern

import org.printscript.token.TokenType

object PreConfiguredTokens {
    val TOKENS_1_0: TokenProvider =
        TokenProvider builder (
            mapOf(
                "\\blet\\b" to TokenType.LET,
                "\\bprintln\\b" to TokenType.PRINTLN,
                "\\bnumber\\b" to TokenType.NUMBER_TYPE,
                "\\bstring\\b" to TokenType.STRING_TYPE,
                "\"(?:\\\\.|[^\"\\\\\\n])*\"" to TokenType.STRING,
                "\\d+(?:\\.\\d+)?" to TokenType.NUMBER,
                "[A-Za-z_][A-Za-z0-9_]*" to TokenType.IDENTIFIER,
                "=" to TokenType.EQUAL,
                "\\+" to TokenType.PLUS,
                "-" to TokenType.MINUS,
                "\\*" to TokenType.STAR,
                "/" to TokenType.SLASH,
                "[(){}:;]" to TokenType.SYNTAX,
            )
        )

    val TOKENS_1_1: TokenProvider =
        TokenProvider.builder(
            mapOf(
                "\\bconst\\b" to TokenType.CONST,
                "\\bif\\b" to TokenType.IF,
                "\\belse\\b" to TokenType.ELSE,
                "\\breadInput\\b" to TokenType.READ_INPUT,
                "\\breadEnv\\b" to TokenType.READ_ENV,
                "\\bboolean\\b" to TokenType.BOOLEAN_TYPE,
                "\\btrue\\b" to TokenType.TRUE,
                "\\bfalse\\b" to TokenType.FALSE,
            ),
        ) + TOKENS_1_0
}
