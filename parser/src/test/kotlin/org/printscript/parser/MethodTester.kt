package org.printscript.parser

import org.junit.jupiter.api.Test
import org.printscript.parser.testutil.TestUtils
import org.printscript.token.Token
import org.printscript.token.TokenType
import kotlin.test.assertEquals

class MethodTester {

    @Test
    fun testMethodPrintLn() {
        val parser = DefaultParser()
        val tokens = listOf(
            TestUtils.token(TokenType.LET),
            TestUtils.token(TokenType.IDENTIFIER, "a"),
            TestUtils.syntax(":"),
            TestUtils.token(TokenType.NUMBER_TYPE),
            TestUtils.syntax(";"),
            TestUtils.token(TokenType.IDENTIFIER, "a"),
            TestUtils.token(TokenType.EQUAL),
            TestUtils.token(TokenType.NUMBER, "5"),
            TestUtils.syntax(";"),
            TestUtils.token(TokenType.PRINTLN),
            TestUtils.syntax("("),
            TestUtils.token(TokenType.IDENTIFIER, "a"),
            TestUtils.syntax(")"),
            TestUtils.syntax(";"),
            TestUtils.eof(),
        )
        val ast = parser.parse(
            tokens
        )
        assertEquals(ast.size , 3)
    }
}