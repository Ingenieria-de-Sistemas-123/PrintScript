package org.example.lexer

import org.example.lexer.token.Token
import org.example.lexer.token.TokenType

class Lexer {
    // OJO: el orden importa (keywords antes que IDENTIFIER)
    private val tokenSpecs: List<Pair<TokenType, Regex>> = listOf(
        TokenType.LET           to Regex("""\blet\b"""),
        TokenType.PRINTLN       to Regex("""\bprintln\b"""),
        TokenType.NUMBER_TYPE   to Regex("""\bnumber\b"""),
        TokenType.STRING_TYPE   to Regex("""\bstring\b"""),

        // literales
        TokenType.STRING        to Regex(""""(?:\\.|[^"\\])*""""),
        TokenType.STRING        to Regex("""'(?:\\.|[^'\\])*'"""),
        TokenType.NUMBER        to Regex("""\d+(?:\.\d+)?"""),

        // operadores/símbolos
        TokenType.EQUAL         to Regex("""="""),
        TokenType.PLUS          to Regex("""\+"""),
        TokenType.MINUS         to Regex("""-"""),
        TokenType.STAR          to Regex("""\*"""),
        TokenType.SLASH         to Regex("""/"""),
        TokenType.SEMICOLON     to Regex(""";"""),
        TokenType.SEPARATOR     to Regex(""":"""),
        TokenType.OPEN_PAREN    to Regex("""\("""),
        TokenType.CLOSE_PAREN   to Regex("""\)"""),

        // identificadores (al final)
        TokenType.IDENTIFIER    to Regex("""[A-Za-z_][A-Za-z0-9_]*""")
    )

    private val wsRegex = Regex("""[ \t\r]+""")
    private val nlRegex = Regex("""\n""")

    fun lex(input: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0
        var line = 1
        var column = 1

        fun advance(by: Int, chunk: CharSequence) {
            for (idx in 0 until by) {
                if (chunk[idx] == '\n') {
                    line += 1
                    column = 1
                } else {
                    column += 1
                }
            }
            i += by
        }

        while (i < input.length) {
            val remaining = input.substring(i)

            // 1) salto de línea
            val nlMatch = nlRegex.find(remaining, 0)
            if (nlMatch != null && nlMatch.range.first == 0) {
                advance(nlMatch.value.length, remaining)
                continue
            }

            // 2) espacios/tabs/\r
            val wsMatch = wsRegex.find(remaining, 0)
            if (wsMatch != null && wsMatch.range.first == 0) {
                advance(wsMatch.value.length, remaining)
                continue
            }

            // 3) probar contra regex (match más largo)
            var matched: Pair<TokenType, MatchResult>? = null
            for ((type, rx) in tokenSpecs) {
                val m = rx.find(remaining, 0)
                if (m != null && m.range.first == 0) {
                    if (matched == null || m.value.length > matched!!.second.value.length) {
                        matched = type to m
                    }
                }
            }

            if (matched != null) {
                val (type, match) = matched!!
                val lexeme = match.value
                val cleanValue =
                    if (type == TokenType.STRING &&
                        lexeme.length >= 2 &&
                        (lexeme.first() == '"' || lexeme.first() == '\'') &&
                        lexeme.last() == lexeme.first()
                    ) lexeme.substring(1, lexeme.length - 1) else lexeme

                tokens.add(Token(type, cleanValue, line, column))
                advance(lexeme.length, remaining)
                continue
            }

            throw IllegalArgumentException(
                "Caracter inesperado '${remaining.first()}' en línea $line, columna $column"
            )
        }

        return tokens
    }
}
