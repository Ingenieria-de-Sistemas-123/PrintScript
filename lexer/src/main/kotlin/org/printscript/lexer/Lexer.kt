package org.printscript.lexer

import org.printscript.token.Token
import org.printscript.token.TokenType
import org.printscript.lexer.exception.LexicalException
import org.printscript.lexer.pattern.TokenPattern

class Lexer {
    /*No son tokens del lenguaje (no quiero un token WHITESPACE ni
    * un token NEWLINE, por ejemplo.
    * Solo sirven a mi motor (Lexer) para saltear caracteres que no nos importan*/

    private val wsRegex = Regex("""[ \t\r]+""")
    private val nlRegex = Regex("""\n""")

    fun lex(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        var line = 1
        var column = 1

        fun advance(by: Int) {
            for (idx in 0 until by) {
                val ch = input[i + idx]
                if (ch == '\n') {
                    line++
                    column = 1
                } else {
                    column++
                }
            }
            i += by
        }

        while (i < input.length) {
            // 1) saltos de línea
            val nlMatch = nlRegex.matchAt(input, i)
            if (nlMatch != null) {
                advance(nlMatch.value.length)
                continue
            }

            // 2) espacios/tabs/\r
            val wsMatch = wsRegex.matchAt(input, i)
            if (wsMatch != null) {
                advance(wsMatch.value.length)
                continue
            }

            // 3) probar contra regex (match + largo)
            var matched: Pair<TokenType, MatchResult>? = null
            for ((type, rx) in TokenPattern.tokenSpecs) {
                val m = rx.matchAt(input, i)
                if (m != null) {
                    if (matched == null || m.value.length > matched!!.second.value.length) {
                        matched = type to m
                    }
                }
            }

            if (matched != null) {
                val (type, match) = matched
                val lexeme = match.value
                val cleanValue =
                    if (type == TokenType.STRING &&
                        lexeme.length >= 2 &&
                        (lexeme.first() == '"' || lexeme.first() == '\'') &&
                        lexeme.last() == lexeme.first()
                    ) lexeme.substring(1, lexeme.length - 1) else lexeme

                tokens.add(Token(type, cleanValue, line, column))
                advance(lexeme.length)
                continue
            }

            throw LexicalException(
                "Caracter inesperado '${input[i]}' en línea $line, columna $column",
                line, column
            )
        }

        //EOF para saber que terminó el código
        tokens.add(Token(TokenType.EOF, "", line, column))

        return tokens
    }
}
