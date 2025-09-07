package org.printscript.parser.builder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.printscript.common.Position
import org.printscript.parser.ParseException
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintNode
import org.printscript.parser.testutil.TokenFactory.eof
import org.printscript.parser.testutil.TokenFactory.t
import org.printscript.token.TokenType

class PrintBuilderTest {
    private fun build(vararg toks: org.printscript.token.Token) = PrintBuilder(TokenHandler(toks.toList())).build() as PrintNode

    @Test
    fun `println con literal`() {
        val node =
            build(
                t(TokenType.PRINTLN, "println", 2, 3),
                t(TokenType.OPEN_PAREN, "("),
                t(TokenType.NUMBER, "9"),
                t(TokenType.CLOSE_PAREN, ")"),
                t(TokenType.SEMICOLON, ";"),
                eof(),
            )
        assertEquals(Position(2, 3), node.position)
        val lit = node.expression as LiteralNode<*>
        assertEquals("9", lit.value)
    }

    @Test
    fun `error si falta )`() {
        val ex =
            assertThrows<ParseException> {
                build(
                    t(TokenType.PRINTLN, "println"),
                    t(TokenType.OPEN_PAREN, "("),
                    t(TokenType.NUMBER, "1"),
                    t(TokenType.SEMICOLON, ";"),
                    eof(),
                )
            }
        assertTrue(ex.message!!.contains("Se esperaba ')'"))
    }

    @Test
    fun `error si falta punto y coma`() {
        val ex =
            assertThrows<ParseException> {
                build(
                    t(TokenType.PRINTLN, "println"),
                    t(TokenType.OPEN_PAREN, "("),
                    t(TokenType.NUMBER, "1"),
                    t(TokenType.CLOSE_PAREN, ")"),
                    eof(),
                )
            }
        assertTrue(ex.message!!.contains("Se esperaba ';'"))
    }
}
