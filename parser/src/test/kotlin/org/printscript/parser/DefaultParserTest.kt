package org.printscript.parser

import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.IfElseNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.VariableDeclarationNode
import org.printscript.parser.testutil.TestUtils
import org.printscript.token.TokenType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DefaultParserTest {
    private val parser: Parser = DefaultParser()

    @Test
    fun parseLetWithoutInit() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.LET),
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.NUMBER_TYPE),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertEquals(1, ast.size)
        assertTrue(ast[0] is VariableDeclarationNode)
    }

    @Test
    fun parseAssignation() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.NUMBER, "5"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is AssignationNode)
    }

    @Test
    fun invalidStartTokenThrows() {
        val tokens = listOf(TestUtils.token(TokenType.PLUS, "+"))
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun parseLetWithArithmeticInit() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.LET),
                TestUtils.token(TokenType.IDENTIFIER, "n"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.NUMBER_TYPE),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.token(TokenType.STAR, "*"),
                TestUtils.token(TokenType.NUMBER, "3"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is VariableDeclarationNode)
    }

    @Test
    fun parse_const_with_init_boolean() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.CONST),
                TestUtils.token(TokenType.IDENTIFIER, "flag"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.BOOLEAN_TYPE),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.TRUE, "true"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is VariableDeclarationNode || ast[0] is ConstantDeclarationNode)
    }

    @Test
    fun parse_assignation() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.NUMBER, "5"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is AssignationNode)
    }

    @Test
    fun parse_print_with_string() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "hello"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is PrintStatementNode)
    }

    @Test
    fun parse_print_with_readEnv_inside() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.READ_ENV),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "USER"),
                TestUtils.syntax(")"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is PrintStatementNode)
    }

    @Test
    fun parse_if_then_else_blocks() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IF),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.IDENTIFIER, "cond"),
                TestUtils.syntax(")"),
                TestUtils.syntax("{"),
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "T"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.syntax("}"),
                TestUtils.token(TokenType.ELSE),
                TestUtils.syntax("{"),
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "F"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.syntax("}"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is IfElseNode)
        val ifn = ast[0] as IfElseNode
        assertEquals(1, ifn.ifBranch.size)
        assertEquals(1, ifn.elseBranch.size)
    }

    @Test
    fun parse_if_then_only() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IF),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.IDENTIFIER, "ok"),
                TestUtils.syntax(")"),
                TestUtils.syntax("{"),
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "OK"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.syntax("}"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is IfElseNode)
        val ifn = ast[0] as IfElseNode
        assertEquals(1, ifn.ifBranch.size)
        assertEquals(0, ifn.elseBranch.size)
    }

    @Test
    fun error_token_unexpected_at_start() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ex = assertFailsWith<ParseException> { parser.parse(tokens) }
        assertTrue(ex.message!!.contains("Token inesperado") || ex.message!!.contains("Inicio de sentencia inesperado"))
    }

    @Test
    fun error_if_condition_literal_true() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IF),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.TRUE, "true"),
                TestUtils.syntax(")"),
                TestUtils.syntax("{"),
                TestUtils.syntax("}"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_const_without_initializer() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.CONST),
                TestUtils.token(TokenType.IDENTIFIER, "a"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.NUMBER_TYPE),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun parse_print_with_number_expression() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.token(TokenType.STAR, "*"),
                TestUtils.token(TokenType.NUMBER, "3"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is PrintStatementNode)
    }

    @Test
    fun parse_let_with_unary_minus_init() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.LET),
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.NUMBER_TYPE),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.MINUS, "-"),
                TestUtils.token(TokenType.NUMBER, "5"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is VariableDeclarationNode)
    }

    @Test
    fun parse_print_with_readInput_inside() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.READ_INPUT),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "n?"),
                TestUtils.syntax(")"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is PrintStatementNode)
    }

    @Test
    fun parse_print_with_readEnv_in() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.READ_ENV),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "USER"),
                TestUtils.syntax(")"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is PrintStatementNode)
    }

    @Test
    fun parse_if_then_else_with_multiple_statements_in_blocks() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IF),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.IDENTIFIER, "flag"),
                TestUtils.syntax(")"),
                TestUtils.syntax("{"),
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "A"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "B"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.syntax("}"),
                TestUtils.token(TokenType.ELSE),
                TestUtils.syntax("{"),
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "C"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "D"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
                TestUtils.syntax("}"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is IfElseNode)
        val ifn = ast[0] as IfElseNode
        assertEquals(2, ifn.ifBranch.size)
        assertEquals(2, ifn.elseBranch.size)
    }

    @Test
    fun parse_assignation_with_expression() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is AssignationNode)
    }

    @Test
    fun error_missing_semicolon_after_let() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.LET),
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.NUMBER_TYPE),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_print_missing_right_paren() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "oops"),
                TestUtils.syntax(";"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_assignment_missing_semicolon() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.NUMBER, "5"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_if_condition_number_literal() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IF),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.syntax(")"),
                TestUtils.syntax("{"),
                TestUtils.syntax("}"),
                TestUtils.eof(),
            )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_else_without_if_at_start() {
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
