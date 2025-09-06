package org.printscript.lexer

import org.printscript.token.TokenType

fun main() {
    val lexer =
        Lexer builder {
            with("let") { TokenType.LET }
            with("=") { TokenType.EQUAL }
        }
}
