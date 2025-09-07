package org.printscript.parser.helpers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.printscript.parser.ParseException
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.PrintNode
import org.printscript.parser.testutil.TokenFactory.eof
import org.printscript.parser.testutil.TokenFactory.t
import org.printscript.token.Token
import org.printscript.token.TokenType

class ASTGeneratorTest {
    private fun gen(vararg toks: Token) = ASTGenerator().createAST(toks.toList())

    @Test
    fun `let enruta a VariableDeclarationBuilder`() {
        val ast =
            gen(
                t(TokenType.LET, "let"),
                t(TokenType.IDENTIFIER, "x"),
                t(TokenType.COLON, ":"),
                t(TokenType.NUMBER_TYPE, "number"),
                t(TokenType.SEMICOLON, ";"),
                eof(),
            )
        assertTrue(ast is DeclarationNode)
    }

    @Test
    fun `println enruta a PrintBuilder`() {
        val ast =
            gen(
                t(TokenType.PRINTLN, "println"),
                t(TokenType.OPEN_PAREN, "("),
                t(TokenType.NUMBER, "1"),
                t(TokenType.CLOSE_PAREN, ")"),
                t(TokenType.SEMICOLON, ";"),
                eof(),
            )
        assertTrue(ast is PrintNode)
    }

    @Test
    fun `identifier con '=' enruta a AssignationBuilder`() {
        val ast =
            gen(
                t(TokenType.IDENTIFIER, "x"),
                t(TokenType.EQUAL, "="),
                t(TokenType.NUMBER, "1"),
                t(TokenType.SEMICOLON, ";"),
                eof(),
            )
        assertTrue(ast is AssignationNode)
    }

    @Test
    fun `identifier sin '=' lanza error claro`() {
        val ex =
            assertThrows<ParseException> {
                gen(
                    t(TokenType.IDENTIFIER, "x", 5, 7),
                    t(TokenType.SEMICOLON, ";"),
                    eof(),
                )
            }
        assertTrue(ex.message!!.contains("Se esperaba '=' después del identificador"))
        assertEquals(5, ex.line)
        assertEquals(7, ex.column)
    }

    @Test
    fun `inicio inesperado lanza ParseException`() {
        val ex =
            assertThrows<ParseException> {
                gen(t(TokenType.SEMICOLON, ";", 1, 1), eof())
            }
        assertTrue(ex.message!!.contains("Inicio de sentencia inesperado: SEMICOLON"))
    }
}
