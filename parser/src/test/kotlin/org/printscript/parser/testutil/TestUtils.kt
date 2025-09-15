package org.printscript.parser.testutil

import org.printscript.token.Token
import org.printscript.token.TokenType

object TestUtils {
    fun token(type: TokenType, value: String = "", line: Int = 1, column: Int = 1) =
        Token(type, value, line, column)

    fun syntax(symbol: String, line: Int = 1, column: Int = 1) =
        token(TokenType.SYNTAX, symbol, line, column)

    fun eof(line: Int = 1, column: Int = 1) =
        token(TokenType.EOF, "", line, column)
}