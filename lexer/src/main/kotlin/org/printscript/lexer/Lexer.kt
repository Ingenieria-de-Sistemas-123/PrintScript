package org.printscript.lexer

import org.printscript.lexer.exception.LexicalException
import org.printscript.lexer.pattern.DefaultTokenProvider
import org.printscript.lexer.pattern.TokenProvider
import org.printscript.token.Token
import org.printscript.token.TokenType

class Lexer(
    private val provider: TokenProvider = DefaultTokenProvider,
) {
    companion object {
        infix fun builder(block: LexerBuilder.() -> Unit): Lexer {
            val builder = LexerBuilder()
            builder.block()
            return builder.build(Lexer())
        }
    }

    private val ws = Regex("""[ \t\r]+""")
    private val nl = Regex("""\n""")

    fun lex(input: String): List<Token> {
        val out = mutableListOf<Token>()
        var pos = 0
        var line = 1
        var col = 1

        fun advance(by: Int) {
            for (k in 0 until by) {
                if (input[pos + k] == '\n') {
                    line++
                    col = 1
                } else {
                    col++
                }
            }
            pos += by
        }

        while (pos < input.length) {
            val nlMatch = nl.matchAt(input, pos)
            if (nlMatch != null) {
                advance(nlMatch.value.length)
                continue
            }

            val wsMatch = ws.matchAt(input, pos)
            if (wsMatch != null) {
                advance(wsMatch.value.length)
                continue
            }

            val hit =
                provider.matchAt(input, pos)
                    ?: throw LexicalException("Caracter inesperado '${input[pos]}' en línea $line, columna $col", line, col)

            val (lexeme, type) = hit
            val value =
                if (type == TokenType.STRING && lexeme.length >= 2 &&
                    (lexeme.first() == '"' || lexeme.first() == '\'') &&
                    lexeme.last() == lexeme.first()
                ) {
                    lexeme.substring(1, lexeme.length - 1)
                } else {
                    lexeme
                }

            out += Token(type, value, line, col)
            advance(lexeme.length)
        }

        out += Token(TokenType.EOF, "", line, col)
        return out
    }
}
