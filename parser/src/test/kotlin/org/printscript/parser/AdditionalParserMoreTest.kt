package org.printscript.parser

import org.junit.jupiter.api.Test
import org.printscript.parser.testutil.TestUtils
import org.printscript.token.TokenType
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AdditionalParserMoreTest {
    private val parser: Parser = DefaultParser()

    @Test
    fun parse_readEnv_plus_literal_plus_number() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.READ_ENV),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "USER"),
                TestUtils.syntax(")"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.STRING, "-"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(1, ast.size)
    }

    @Test
    fun parse_unary_nested_parentheses() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.LET), TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.syntax(":"), TestUtils.token(TokenType.NUMBER_TYPE),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.syntax("("), TestUtils.token(TokenType.MINUS, "-"),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.MINUS, "-"), TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"), TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"), TestUtils.syntax(")"),
                TestUtils.syntax(";"), TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(1, ast.size)
    }

    @Test
    fun parse_if_with_multiple_statements_each_branch() {
        // if(flag){ println(1+2); println(readInput("x")); } else { println(readEnv("Y")); println((3+4)*2); }
        val tokens =
            listOf(
                TestUtils.token(
                    TokenType.IF,
                ),
                TestUtils.syntax("("), TestUtils.token(TokenType.IDENTIFIER, "flag"), TestUtils.syntax(")"), TestUtils.syntax("{"),
                // println(1+2);
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"), TestUtils.token(TokenType.PLUS, "+"), TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"), TestUtils.syntax(";"),
                // println(readInput("x"));
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("), TestUtils.token(TokenType.READ_INPUT), TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "x"), TestUtils.syntax(")"), TestUtils.syntax(")"), TestUtils.syntax(";"),
                TestUtils.syntax("}"),
                TestUtils.token(TokenType.ELSE), TestUtils.syntax("{"),
                // println(readEnv("Y"));
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("), TestUtils.token(TokenType.READ_ENV), TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "Y"), TestUtils.syntax(")"), TestUtils.syntax(")"), TestUtils.syntax(";"),
                // println((3+4)*2);
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("), TestUtils.syntax("("),
                TestUtils.token(
                    TokenType.NUMBER,
                    "3",
                ),
                TestUtils.token(TokenType.PLUS, "+"), TestUtils.token(TokenType.NUMBER, "4"), TestUtils.syntax(")"),
                TestUtils.token(TokenType.STAR, "*"), TestUtils.token(TokenType.NUMBER, "2"), TestUtils.syntax(")"), TestUtils.syntax(";"),
                TestUtils.syntax("}"), TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(1, ast.size)
    }

    @Test
    fun error_readInput_missing_closing_paren() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.READ_INPUT),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "X"),
                // falta ')'
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_readEnv_missing_semicolon() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.READ_ENV),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "X"),
                TestUtils.syntax(")"),
                TestUtils.syntax(")"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun parse_readInput_concat_literal() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("),
                TestUtils.token(TokenType.READ_INPUT), TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "Name"), TestUtils.syntax(")"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.STRING, " !"), TestUtils.syntax(")"),
                TestUtils.syntax(";"), TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(1, ast.size)
    }

    @Test
    fun parse_readEnv_assignment_concat() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "user"), TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.READ_ENV), TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "USER"), TestUtils.syntax(")"),
                TestUtils.token(TokenType.PLUS, "+"), TestUtils.token(TokenType.STRING, "!"),
                TestUtils.syntax(";"), TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(1, ast.size)
    }

    @Test
    fun error_unbalanced_parentheses_complex() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"),
                TestUtils.token(TokenType.STAR, "*"),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "3"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "4"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_double_plus_operator() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"), TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.PLUS, "+"), TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"), TestUtils.syntax(";"), TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_trailing_operator() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        // El builder interno detectará que falta operando derecho
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_unary_plus_not_supported() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun parse_if_else_empty_blocks() {
        // if(flag){} else {}
        val tokens =
            listOf(
                TestUtils.token(
                    TokenType.IF,
                ),
                TestUtils.syntax(
                    "(",
                ),
                TestUtils.token(TokenType.IDENTIFIER, "flag"), TestUtils.syntax(")"), TestUtils.syntax("{"), TestUtils.syntax("}"),
                TestUtils.token(TokenType.ELSE), TestUtils.syntax("{"), TestUtils.syntax("}"), TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(1, ast.size)
    }

    @Test
    fun parse_chained_arithmetic_in_assignment() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "a"), TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.NUMBER, "1"), TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.token(TokenType.STAR, "*"), TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "3"), TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "4"), TestUtils.syntax(")"),
                TestUtils.token(TokenType.MINUS, "-"), TestUtils.token(TokenType.NUMBER, "5"),
                TestUtils.token(TokenType.SLASH, "/"), TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "6"), TestUtils.token(TokenType.MINUS, "-"),
                TestUtils.token(TokenType.NUMBER, "1"), TestUtils.syntax(")"),
                TestUtils.syntax(";"), TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(1, ast.size)
    }

    @Test
    fun error_assignment_missing_semicolon_complex() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "a"),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_if_missing_open_brace() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IF), TestUtils.syntax("("), TestUtils.token(TokenType.TRUE),
                TestUtils.syntax(")"),
                TestUtils.token(TokenType.PRINTLN), TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"), TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_else_without_if_block() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.ELSE),
                TestUtils.syntax("{"),
                TestUtils.syntax("}"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }
}
