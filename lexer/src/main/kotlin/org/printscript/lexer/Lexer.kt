package org.printscript.lexer

import org.printscript.lexer.pattern.TokenProvider
import org.printscript.token.Token
import org.printscript.token.TokenType
import java.io.BufferedReader
import java.io.Reader

class Lexer(private val tokenProvider: TokenProvider) {

    fun lexLines(reader: Reader): Iterator<List<Token>> = LineIterator(reader)

    fun lex(reader: Reader): List<Token> {
        val flat = mutableListOf<Token>()
        val it = lexLines(reader)
        while (it.hasNext()) flat += it.next()
        val eofPos = if (flat.isEmpty()) 1 else flat.last().column
        val eofLine = if (flat.isEmpty()) 1 else flat.last().line
        flat += Token(TokenType.EOF, "", eofLine, eofPos)
        return flat
    }

    private inner class LineIterator(reader: Reader) : Iterator<List<Token>> {
        private val bufferedReader: BufferedReader = reader.buffered()
        private var lineText: String? = null
        private var lineNumber = 0
        private var column = 1

        init { nextLine() }

        private fun nextLine() {
            lineText = bufferedReader.readLine()
            lineNumber++
            column = 1
        }

        private fun skipBlankLines() {
            while (lineText != null && lineText!!.isBlank()) nextLine()
        }

        override fun hasNext(): Boolean {
            skipBlankLines()
            return lineText != null
        }

        override fun next(): List<Token> {
            if (!hasNext()) throw NoSuchElementException("No more lines to read")
            val tokens = tokenize(lineText!!)
            nextLine()
            return tokens
        }

        private fun tokenize(line: String): List<Token> {
            val tokens = mutableListOf<Token>()
            var pos = 0
            var col = 1

            fun skipSpace() {
                while (pos < line.length && line[pos].isWhitespace()) {
                    pos++; col++
                }
            }

            while (pos < line.length) {
                skipSpace()
                if (pos >= line.length) break

                val match = tokenProvider.getTokenFor(line, pos)
                    ?: error("Unexpected character at line $lineNumber, column $col")

                val (lexeme, type) = match
                val value = lexeme
                tokens += Token(type, value, lineNumber, col)

                pos += lexeme.length
                col += lexeme.length
            }
            return tokens
        }
    }
}
