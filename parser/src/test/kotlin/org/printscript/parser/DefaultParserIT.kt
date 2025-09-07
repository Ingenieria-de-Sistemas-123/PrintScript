package org.printscript.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.PrintNode
import org.printscript.parser.testutil.TokenFactory.eof
import org.printscript.parser.testutil.TokenFactory.t
import org.printscript.token.Token
import org.printscript.token.TokenType

class DefaultParserIT {
    private fun parse(vararg toks: Token) = DefaultParser().parse(toks.toList())

    @Test
    fun `parsea multiples sentencias en orden`() {
        val ast =
            parse(
                // let a:number;
                t(TokenType.LET, "let"),
                t(TokenType.IDENTIFIER, "a"),
                t(TokenType.COLON, ":"),
                t(TokenType.NUMBER_TYPE, "number"),
                t(TokenType.SEMICOLON, ";"),
                // a = 1 + 2 * 3;
                t(TokenType.IDENTIFIER, "a"),
                t(TokenType.EQUAL, "="),
                t(TokenType.NUMBER, "1"),
                t(TokenType.PLUS, "+"),
                t(TokenType.NUMBER, "2"),
                t(TokenType.STAR, "*"),
                t(TokenType.NUMBER, "3"),
                t(TokenType.SEMICOLON, ";"),
                // println(a);
                t(TokenType.PRINTLN, "println"),
                t(TokenType.OPEN_PAREN, "("),
                t(TokenType.IDENTIFIER, "a"),
                t(TokenType.CLOSE_PAREN, ")"),
                t(TokenType.SEMICOLON, ";"),
                eof(),
            )

        assertEquals(3, ast.size)
        assertTrue(ast[0] is DeclarationNode)
        assertTrue(ast[1] is AssignationNode)
        assertTrue(ast[2] is PrintNode)
    }

    @Test
    fun `error con IDENTIFIER que no es asignacion ni declaracion`() {
        val ex =
            assertThrows<ParseException> {
                parse(
                    t(TokenType.IDENTIFIER, "x", 10, 2),
                    t(TokenType.SEMICOLON, ";"),
                    eof(),
                )
            }
        assertTrue(ex.message!!.contains("Se esperaba una asignación o declaración"))
        assertEquals(10, ex.line)
        assertEquals(2, ex.column)
    }

    @Test
    fun `token inesperado al inicio de sentencia`() {
        val ex =
            assertThrows<ParseException> {
                parse(t(TokenType.SEMICOLON, ";", 1, 1), eof())
            }
        assertTrue(ex.message!!.contains("Token inesperado en inicio de sentencia: SEMICOLON"))
    }
}
