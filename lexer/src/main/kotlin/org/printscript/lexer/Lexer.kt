package org.printscript.lexer

import org.printscript.lexer.pattern.TokenProvider
import org.printscript.token.Token
import org.printscript.token.TokenType
import java.io.BufferedReader
import java.io.Reader

class Lexer(private val tokenProvider: TokenProvider) {
    fun lexLines(reader: Reader): Sequence<List<Token>> =
        sequence {
            val bufferedReader: BufferedReader = reader.buffered()
            var lineNumber = 0

            while (true) {
                val rawLine = bufferedReader.readLine() ?: break
                lineNumber++
                if (rawLine.isBlank()) continue

                val tokens = tokenize(rawLine, lineNumber)
                if (tokens.isNotEmpty()) yield(tokens)
            }
        }

    fun lex(reader: Reader): Sequence<Token> =
        sequence {
            var lastToken: Token? = null
            for (lineTokens in lexLines(reader)) {
                for (token in lineTokens) {
                    lastToken = token
                    yield(token)
                }
            }
            val eofLine = lastToken?.line ?: 1
            val eofColumn = lastToken?.column ?: 1
            yield(Token(TokenType.EOF, "", eofLine, eofColumn))
        }

    fun lexToList(reader: Reader): List<Token> = lex(reader).toList()

    private fun tokenize(
        line: String,
        lineNumber: Int,
    ): List<Token> {
        val tokens = mutableListOf<Token>()
        var pos = 0
        var col = 1

        fun skipSpace() {
            while (pos < line.length && line[pos].isWhitespace()) {
                pos++
                col++
            }
        }

        while (pos < line.length) {
            skipSpace()
            if (pos >= line.length) break

            val match =
                tokenProvider.getTokenFor(line, pos)
                    ?: error("Unexpected character at line $lineNumber, column $col")

            val (lexeme, type) = match
            tokens += Token(type, lexeme, lineNumber, col)

            pos += lexeme.length
            col += lexeme.length
        }
        return tokens
    }
}
