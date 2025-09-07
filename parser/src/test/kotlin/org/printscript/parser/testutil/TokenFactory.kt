package org.printscript.parser.testutil

import org.printscript.token.Token
import org.printscript.token.TokenType

object TokenFactory {
    fun t(
        type: TokenType,
        value: String =
            when (type) {
                TokenType.IDENTIFIER -> "id"
                TokenType.NUMBER -> "1"
                TokenType.STRING -> "\"s\""
                else -> type.name.lowercase()
            },
        line: Int = 1,
        col: Int = 1,
    ) = Token(type, value, line, col)

    fun eof(
        line: Int = 1,
        col: Int = 1,
    ) = t(TokenType.EOF, "", line, col)
}
