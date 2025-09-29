package org.printscript.parser

import org.junit.jupiter.api.Test
import org.printscript.parser.testutil.TestUtils
import org.printscript.token.TokenType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AdditionalParserTest {
    private val parser: Parser = DefaultParser()

    @Test
    fun parse_complex_precedence_chain() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.token(TokenType.STAR, "*"),
                TestUtils.token(TokenType.NUMBER, "3"),
                TestUtils.token(TokenType.MINUS, "-"),
                TestUtils.token(TokenType.NUMBER, "4"),
                TestUtils.token(TokenType.SLASH, "/"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"), TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast.isNotEmpty())
    }

    @Test
    fun parse_parenthesized_precedence_change() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("),
                TestUtils.syntax("("), TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"), TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"), TestUtils.token(TokenType.STAR, "*"),
                TestUtils.syntax("("), TestUtils.token(TokenType.NUMBER, "3"),
                TestUtils.token(TokenType.PLUS, "+"), TestUtils.token(TokenType.NUMBER, "4"),
                TestUtils.syntax(")"),
                TestUtils.syntax(")"), TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(ast.size, 1)
    }

    @Test
    fun parse_unary_minus_nested() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.LET),
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.NUMBER_TYPE),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.MINUS, "-"),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast.isNotEmpty())
    }

    @Test
    fun parse_readInput_in_concatenation() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("),
                TestUtils.token(TokenType.READ_INPUT),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "Name"),
                TestUtils.syntax(")"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.STRING, "!"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(ast.size, 1)
    }

    @Test
    fun parse_if_nested() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IF), TestUtils.syntax("("),
                TestUtils.token(TokenType.IDENTIFIER, "flag"), TestUtils.syntax(")"),
                TestUtils.syntax("{"),
                TestUtils.token(TokenType.IF), TestUtils.syntax("("),
                TestUtils.token(TokenType.IDENTIFIER, "flag"), TestUtils.syntax(")"),
                TestUtils.syntax("{"),
                TestUtils.syntax("}"),
                TestUtils.syntax("}"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(ast.size, 1)
    }

    @Test
    fun error_if_missing_paren() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IF),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                // falta ")"
                TestUtils.syntax("{"),
                TestUtils.syntax("}"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_double_operator() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.STAR, "*"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_paren_unbalanced_expression() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                // falta cierre del "(" interno
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_missing_initializer_after_equal() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.LET),
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.NUMBER_TYPE),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun parse_readEnv_in_assignment() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "user"),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.READ_ENV),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "USER"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(ast.size, 1)
    }
}
