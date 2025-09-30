package org.printscript.lexer.pattern

import org.printscript.token.TokenType

private data class TokenPattern(
    val pattern: String,
    val regex: Regex,
    val type: TokenType,
)

class TokenProviderImplementation private constructor(
    private val tokens: LinkedHashMap<String, TokenPattern>,
) : TokenProvider {
    override fun getTokenFor(
        line: String,
        position: Int,
    ): Pair<String, TokenType>? {
        for (token in tokens.values) {
            val m = token.regex.find(line, position)
            if (m != null && m.range.first == position) {
                return m.value to token.type
            }
        }
        return null
    }

    override fun plus(other: TokenProvider): TokenProvider {
        val merged = LinkedHashMap(tokens)
        when (other) {
            is TokenProviderImplementation -> {
                other.tokens.values.forEach { token ->
                    merged[token.pattern] = token
                }
            }
            else -> {
                other.iterator().forEach { (pattern, type) ->
                    merged[pattern] = TokenPattern(pattern, Regex(pattern), type)
                }
            }
        }
        return fromTokens(merged)
    }

    override fun iterator(): Iterator<Pair<String, TokenType>> = tokens.values.map { it.pattern to it.type }.iterator()

    companion object {
        fun from(tokenMap: Map<String, TokenType>): TokenProviderImplementation {
            val linked = LinkedHashMap<String, TokenPattern>()
            tokenMap.forEach { (pattern, type) ->
                linked[pattern] = TokenPattern(pattern, Regex(pattern), type)
            }
            return TokenProviderImplementation(linked)
        }

        private fun fromTokens(tokens: LinkedHashMap<String, TokenPattern>): TokenProviderImplementation =
            TokenProviderImplementation(tokens)
    }
}
