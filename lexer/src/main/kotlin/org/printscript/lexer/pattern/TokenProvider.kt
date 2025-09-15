package org.printscript.lexer.pattern

import org.printscript.token.TokenType

sealed interface TokenProvider {
    fun getTokenFor(line: String, position: Int): Pair<String, TokenType>?
    operator fun plus(other: TokenProvider): TokenProvider
    fun iterator(): Iterator<Pair<String, TokenType>>

    companion object {
        infix fun builder(tokenMap: Map<String, TokenType>): TokenProvider {
            val linked = java.util.LinkedHashMap<String, TokenType>()
            tokenMap.forEach { (k, v) -> linked[k] = v }
            return TokenProviderImplementation(linked)
        }
    }
}
