package org.printscript.formatter.layout

import org.printscript.formatter.rules.FormatToken

/**
 * Small helper that walks the original source to capture the whitespace that
 * surrounded relevant symbols. It purposely ignores any semantic knowledge and
 * just keeps the raw sequences of whitespace that appeared before and after a
 * token. The tracker is consumed sequentially while the formatter renders the
 * emitted tokens.
 */
class OriginalLayoutTracker(private val source: String) {
    data class SurroundingWhitespace(val leading: String, val trailing: String)

    private var index = 0

    fun consume(token: FormatToken): SurroundingWhitespace? =
        when (token) {
            is FormatToken.Equals -> consumeChar('=')
            is FormatToken.Colon -> consumeChar(':')
            is FormatToken.Semicolon -> consumeChar(';')
            is FormatToken.Op ->
                when (token.kind) {
                    FormatToken.OpKind.PLUS -> consumeChar('+')
                    FormatToken.OpKind.MINUS -> consumeChar('-')
                    FormatToken.OpKind.STAR -> consumeChar('*')
                    FormatToken.OpKind.SLASH -> consumeChar('/')
                }
            else -> null
        }

    private fun consumeChar(target: Char): SurroundingWhitespace? {
        val pos = findNextChar(target) ?: return null

        val leadingStart = findLeadingStart(pos)
        val trailingEnd = findTrailingEnd(pos)

        index = pos + 1

        val leading = source.substring(leadingStart, pos)
        val trailing = source.substring(pos + 1, trailingEnd)
        return SurroundingWhitespace(leading, trailing)
    }

    private fun findLeadingStart(pos: Int): Int {
        var cursor = pos - 1
        while (cursor >= 0 && source[cursor].isWhitespace()) {
            cursor--
        }
        return cursor + 1
    }

    private fun findTrailingEnd(pos: Int): Int {
        var cursor = pos + 1
        while (cursor < source.length && source[cursor].isWhitespace()) {
            cursor++
        }
        return cursor
    }

    private fun findNextChar(target: Char): Int? {
        while (index < source.length) {
            val current = source[index]
            when {
                current == target -> return index
                current == '\"' -> skipStringLiteral()
                current == '/' && peekNext() == '/' -> skipSingleLineComment()
                current == '/' && peekNext() == '*' -> skipBlockComment()
                else -> index++
            }
        }
        return null
    }

    private fun skipStringLiteral() {
        index++ // skip opening quote
        while (index < source.length) {
            val c = source[index]
            if (c == '\\') {
                index += 2
                continue
            }
            index++
            if (c == '\"') break
        }
    }

    private fun skipSingleLineComment() {
        while (index < source.length && source[index] != '\n') {
            index++
        }
    }

    private fun skipBlockComment() {
        index += 2 // skip opening '/*'
        while (index < source.length) {
            if (source[index] == '*' && peekNext() == '/') {
                index += 2
                break
            }
            index++
        }
    }

    private fun peekNext(): Char? = if (index + 1 < source.length) source[index + 1] else null
}
