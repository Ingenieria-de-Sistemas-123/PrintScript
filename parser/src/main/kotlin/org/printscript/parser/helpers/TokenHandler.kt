package org.printscript.parser.helpers

import org.printscript.parser.ParseException
import org.printscript.token.Token
import org.printscript.token.TokenType

data class TokenHandler(val line: List<Token>) {
    private var currentTokenIndex = 0

    fun collectExpressionTokens(with: Boolean): List<Token> {
        val tokens = mutableListOf<Token>()
        while (!isAtEnd() && peek().type != TokenType.SYNTAX) {
            tokens.add(advance())
        }
        if (with) tokens.add(consume(TokenType.SYNTAX, "Se esperaba ';'"))
        return tokens
    }

    fun collectExpressionTokensInParenthesis(): List<Token> {
        val tokens = mutableListOf<Token>()
        var depth = 1

        while (!isAtEnd()) {
            val t = peek()
            advance()
            if (t.type == TokenType.SYNTAX && t.value == "(") {
                depth++
            } else if (t.type == TokenType.SYNTAX && t.value == ")") {
                depth--
                if (depth == 0) break
            }
            tokens += t
        }

        if (depth != 0) throw ParseException("Paréntesis desbalanceados", peek().line, peek().column)
        return tokens
    }

    fun advance(): Token {
        if (!isAtEnd()) currentTokenIndex++
        return previous()
    }

    fun isAtEnd(): Boolean = currentTokenIndex >= line.size

    fun peek(): Token {
        check(currentTokenIndex < line.size) {
            "Expected token but reached end of line. At line ${line.last().line} column ${line.last().column}"
        }
        return line[currentTokenIndex]
    }

    fun consume(
        type: TokenType,
        message: String,
    ): Token {
        if (check(type)) return advance()
        val p = peek()
        throw IllegalArgumentException("$message At line ${p.line} column ${p.column}")
    }

    fun expect(
        type: TokenType,
        message: String,
    ): Token = consume(type, message)

    private fun check(type: TokenType): Boolean = !isAtEnd() && peek().type == type

    private fun previous(): Token {
        check(currentTokenIndex > 0) { "No previous token." }
        return line[currentTokenIndex - 1]
    }
}
