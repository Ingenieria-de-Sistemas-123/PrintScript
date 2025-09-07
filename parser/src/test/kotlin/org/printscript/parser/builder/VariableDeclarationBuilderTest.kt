package org.printscript.parser.builder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.printscript.common.Position
import org.printscript.parser.ParseException
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.testutil.TokenFactory.eof
import org.printscript.parser.testutil.TokenFactory.t
import org.printscript.token.TokenType

class VariableDeclarationBuilderTest {
    private fun build(vararg toks: org.printscript.token.Token) =
        VariableDeclarationBuilder(TokenHandler(toks.toList())).build() as DeclarationNode

    @Test
    fun `declaracion sin inicializador infiere empty del tipo`() {
        val node =
            build(
                t(TokenType.LET, "let"),
                t(TokenType.IDENTIFIER, "x", 2, 5),
                t(TokenType.COLON, ":"),
                t(TokenType.NUMBER_TYPE, "number"),
                t(TokenType.SEMICOLON, ";"),
                eof(),
            )
        assertEquals("x", node.name)
        assertEquals("number", node.type)
        assertEquals(Position(2, 5), node.position)
        val v = node.value as LiteralNode<*>
        assertEquals("empty", v.value)
        assertEquals("number", v.type)
    }

    @Test
    fun `declaracion con inicializador`() {
        val node =
            build(
                t(TokenType.LET, "let"),
                t(TokenType.IDENTIFIER, "msg"),
                t(TokenType.COLON, ":"),
                t(TokenType.STRING_TYPE, "string"),
                t(TokenType.EQUAL, "="),
                t(TokenType.STRING, "\"hola\""),
                t(TokenType.SEMICOLON, ";"),
                eof(),
            )
        assertEquals("string", node.type)
        val lit = node.value as LiteralNode<*>
        assertEquals("\"hola\"", lit.value)
        assertEquals("string", lit.type)
    }

    @Test
    fun `error si tipo no es number ni string`() {
        val ex =
            assertThrows<ParseException> {
                build(
                    t(TokenType.LET, "let"),
                    t(TokenType.IDENTIFIER, "a", 1, 2),
                    t(TokenType.COLON, ":"),
                    // tipo inválido
                    t(TokenType.IDENTIFIER, "weird", 1, 10),
                    eof(),
                )
            }
        assertTrue(ex.message!!.contains("Se esperaba el tipo"))
        assertEquals(1, ex.line)
        assertEquals(10, ex.column)
    }
}
