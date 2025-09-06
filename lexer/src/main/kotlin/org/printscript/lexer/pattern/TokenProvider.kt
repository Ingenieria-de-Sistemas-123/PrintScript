package org.printscript.lexer.pattern

import org.printscript.token.TokenType

sealed interface TokenProvider {
    fun matchAt(
        input: CharSequence,
        pos: Int,
    ): Pair<String, TokenType>?

    operator fun plus(other: TokenProvider): TokenProvider

    companion object {
        infix fun builder(tokenMap: Map<String, TokenType>): TokenProvider =
            Impl(tokenMap.entries.map { (pat, type) -> type to Regex(pat) })
    }

    private class Impl(
        private val specs: List<Pair<TokenType, Regex>>,
    ) : TokenProvider {
        override fun matchAt(
            input: CharSequence,
            pos: Int,
        ): Pair<String, TokenType>? {
            var best: Pair<TokenType, MatchResult>? = null
            for ((type, rx) in specs) {
                val m = rx.find(input, pos)
                if (m != null && m.range.first == pos) {
                    if (best == null || m.value.length > best.second.value.length) {
                        best = type to m
                    }
                }
            }
            return best?.let { it.second.value to it.first }
        }

        override fun plus(other: TokenProvider): TokenProvider = Impl(this.specs + (other as Impl).specs)
    }
}
