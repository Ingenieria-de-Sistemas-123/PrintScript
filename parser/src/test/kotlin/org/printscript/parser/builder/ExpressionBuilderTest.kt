package org.printscript.parser.builder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.printscript.common.Position
import org.printscript.parser.ParseException
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.testutil.TokenFactory.eof
import org.printscript.parser.testutil.TokenFactory.t
import org.printscript.token.TokenType

class ExpressionBuilderTest {
    private fun build(vararg toks: org.printscript.token.Token): ASTNode = ExpressionBuilder(TokenHandler(toks.toList())).build()

    @Test
    fun `literal number`() {
        val ast = build(t(TokenType.NUMBER, "123", 2, 4), eof())
        val lit = ast as LiteralNode<*>
        assertEquals("123", lit.value)
        assertEquals("number", lit.type)
        assertEquals(Position(2, 4), lit.position)
    }

    @Test
    fun `literal string e identifier`() {
        val s = build(t(TokenType.STRING, "\"hola\"", 1, 2), eof()) as LiteralNode<*>
        assertEquals("string", s.type)
        val id = build(t(TokenType.IDENTIFIER, "x", 3, 1), eof()) as LiteralNode<*>
        assertEquals("identifier", id.type)
        assertEquals("x", id.value)
    }

    // el operador * tiene mayor precedencia que + si hay parentesis
    @Test
    fun `parenthesis y precedencia`() {
        // (1 + 2) * 3 -> fuerza que * tenga mayor precedencia que +
        val ast =
            build(
                t(TokenType.OPEN_PAREN, "(", 1, 1),
                t(TokenType.NUMBER, "1", 1, 2),
                t(TokenType.PLUS, "+", 1, 3),
                t(TokenType.NUMBER, "2", 1, 5),
                t(TokenType.CLOSE_PAREN, ")", 1, 6),
                t(TokenType.STAR, "*", 1, 8),
                t(TokenType.NUMBER, "3", 1, 10),
                eof(),
            ) as DoubleExpressionNode
        assertEquals("*", ast.operator)
        val left = ast.left as DoubleExpressionNode
        assertEquals("+", left.operator)
    }

    @Test
    fun `asociatividad de operadores binarios por precedencia`() {
        // 1 + 2 * 3 -> + con rhs (2*3)
        val ast =
            build(
                t(TokenType.NUMBER, "1"),
                t(TokenType.PLUS, "+"),
                t(TokenType.NUMBER, "2"),
                t(TokenType.STAR, "*"),
                t(TokenType.NUMBER, "3"),
                eof(),
            ) as DoubleExpressionNode
        assertEquals("+", ast.operator)
        val rhs = ast.right as DoubleExpressionNode
        assertEquals("*", rhs.operator)
    }

    @Test
    fun `unario menos se reescribe como 0 - expr`() {
        val ast =
            build(
                t(TokenType.MINUS, "-", 4, 7),
                t(TokenType.NUMBER, "5", 4, 8),
                eof(),
            ) as DoubleExpressionNode
        assertEquals("-", ast.operator)
        val left = ast.left as LiteralNode<*>
        assertEquals("0", left.value)
        assertEquals("number", left.type)
        // usa pos del op unario
        assertEquals(Position(4, 7), left.position)
    }

    @Test
    fun `error si token primario invalido`() {
        val ex =
            assertThrows<ParseException> {
                build(t(TokenType.SEMICOLON, ";", 9, 2), eof())
            }
        assertTrue(ex.message!!.contains("Expresión inválida"))
        assertEquals(9, ex.line)
        assertEquals(2, ex.column)
    }

    // simulamos que parsearíamos hasta ';' si se usara dentro de otra builder
    @Test
    fun `cierra en parenthesis, punto y coma o EOF (stopHere)`() {
        // acá chequeamos que no truena: "1 ; EOF"
        val handler = TokenHandler(listOf(t(TokenType.NUMBER, "1"), t(TokenType.SEMICOLON, ";"), eof()))
        val ast = ExpressionBuilder(handler).build()
        assertTrue(ast is LiteralNode<*>)
        // el handler queda parado en ';' para el siguiente consumidor
        assertEquals(TokenType.SEMICOLON, handler.current().type)
    }
}
