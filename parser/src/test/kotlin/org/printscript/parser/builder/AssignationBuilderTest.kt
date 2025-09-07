package org.printscript.parser.builder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.printscript.common.Position
import org.printscript.parser.ParseException
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.testutil.TokenFactory.eof
import org.printscript.parser.testutil.TokenFactory.t
import org.printscript.token.TokenType

class AssignationBuilderTest {
    private fun build(vararg toks: org.printscript.token.Token) = AssignationBuilder(TokenHandler(toks.toList())).build() as AssignationNode

    @Test
    fun `asignacion simple con precedencia`() {
        val node =
            build(
                t(TokenType.IDENTIFIER, "x", 3, 1),
                t(TokenType.EQUAL, "="),
                t(TokenType.NUMBER, "1"),
                t(TokenType.PLUS, "+"),
                t(TokenType.NUMBER, "2"),
                t(TokenType.STAR, "*"),
                t(TokenType.NUMBER, "3"),
                t(TokenType.SEMICOLON, ";"),
                eof(),
            )
        assertEquals("x", node.name)
        assertEquals(Position(3, 1), node.position)
        val expr = node.type as DoubleExpressionNode
        assertEquals("+", expr.operator)
    }

    @Test
    fun `falta punto y coma lanza error descriptivo`() {
        val ex =
            assertThrows<ParseException> {
                build(
                    t(TokenType.IDENTIFIER, "x", 1, 1),
                    t(TokenType.EQUAL, "="),
                    t(TokenType.NUMBER, "1"),
                    eof(),
                )
            }
        assertTrue(ex.message!!.contains("Se esperaba ';'"))
    }
}
