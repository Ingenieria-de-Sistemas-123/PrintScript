package org.printscript.lexer

import org.printscript.lexer.pattern.TokenProvider
import org.printscript.token.TokenType

class LexerBuilder {
    private val tokens = mutableListOf<Pair<String, TokenType>>()

    fun with(
        string: String,
        block: () -> TokenType,
    ) {
        tokens += string to block()
    }

    fun build(lexer: Lexer): Lexer {
        return Lexer(
            provider =
                TokenProvider builder (
                    tokens.toMap()
                ),
        )
    }
}
