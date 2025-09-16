package org.printscript.lexer.pattern

import org.printscript.token.TokenType

class TokenProviderImplementation(
    private val tokens: LinkedHashMap<String, TokenType>,
) : TokenProvider {
    override fun getTokenFor(
        line: String,
        position: Int,
    ): Pair<String, TokenType>? {
        for ((pattern, type) in tokens) {
            val m = Regex(pattern).find(line, position)
            if (m != null && m.range.first == position) {
                return m.value to type
            }
        }
        return null
    }

    override fun plus(other: TokenProvider): TokenProvider {
        val merged = LinkedHashMap(tokens)
        other.iterator().forEach { (k, v) -> merged[k] = v }
        return TokenProviderImplementation(merged)
    }

    override fun iterator(): Iterator<Pair<String, TokenType>> = tokens.entries.map { it.key to it.value }.iterator()
}
