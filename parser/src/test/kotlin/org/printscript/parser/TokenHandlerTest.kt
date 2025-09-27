package org.printscript.parser

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.printscript.parser.helpers.TokenHandler
import org.printscript.parser.testutil.TestUtils
import org.printscript.token.TokenType
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TokenHandlerTest {
    @Test
    fun collectExpressionTokens_stop_at_semicolon_and_include_when_requested() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.syntax(";"),
                TestUtils.token(TokenType.IDENTIFIER, "y"),
                TestUtils.eof(),
            )
        val h1 = TokenHandler(tokens)
        val part1 = h1.collectExpressionTokens(with = false)
        assertEquals(3, part1.size)

        val h2 = TokenHandler(tokens)
        val part2 = h2.collectExpressionTokens(with = true)
        assertEquals(4, part2.size)
        assertEquals(";", part2.last().value)
    }

    @Test
    fun collectExpressionTokensInParenthesis_handles_nested() {
        val tokens =
            listOf(
                TestUtils.syntax("("),
                TestUtils.token(TokenType.IDENTIFIER, "f"),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.syntax(")"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"),
                TestUtils.eof(),
            )
        val h = TokenHandler(tokens)
        h.advance()
        val inside = h.collectExpressionTokensInParenthesis()
        assertTrue(inside.any { it.value == "f" })
        assertTrue(inside.any { it.value == "+" })
        assertTrue(inside.any { it.value == "2" })
    }

    @Test
    fun consume_throws_with_message_and_position() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.eof(),
            )
        val h = TokenHandler(tokens)
        val ex =
            assertFailsWith<IllegalArgumentException> {
                h.consume(TokenType.LET, "Se esperaba 'let'")
            }
        assertTrue(ex.message!!.contains("Se esperaba 'let'"))
    }

    @Test
    fun collectExpressionTokens_with_true_includes_semicolon() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val h = TokenHandler(tokens)
        val expr = h.collectExpressionTokens(with = true)
        assertTrue(expr.last().type == TokenType.SYNTAX && expr.last().value == ";")
    }

    @Test
    fun collectExpressionTokens_with_false_stops_before_semicolon() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val h = TokenHandler(tokens)
        val expr = h.collectExpressionTokens(with = false)
        assertTrue(expr.isNotEmpty())
        assertTrue(expr.all { !(it.type == TokenType.SYNTAX && it.value == ";") })
    }

    @Test
    fun collectExpressionTokens_handles_parenthesized_expression_until_semicolon() {
        val tokens =
            listOf(
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val handler = TokenHandler(tokens)
        val expr = handler.collectExpressionTokens(with = false)

        assertEquals(listOf("(", "1", "+", "2", ")"), expr.map { it.value })
    }

    @Test
    fun collectExpressionTokens_handles_function_call_expression() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.READ_INPUT, "readInput"),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "Name?"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val handler = TokenHandler(tokens)
        val expr = handler.collectExpressionTokens(with = false)

        assertEquals(4, expr.size)
        assertEquals("(", expr[1].value)
        assertEquals(")", expr.last().value)
        assertTrue(expr.none { it.value == ";" })
    }
}
