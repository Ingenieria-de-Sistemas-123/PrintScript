package org.printscript.parser


import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith
import org.printscript.token.TokenType
import org.printscript.parser.node.*
import org.printscript.parser.testutil.TestUtils

class DefaultParserTest {
    private val parser: Parser = DefaultParser()

    @Test
    fun parseLetWithoutInit() {
        val tokens = listOf(
            TestUtils.token(TokenType.LET),
            TestUtils.token(TokenType.IDENTIFIER, "x"),
            TestUtils.syntax(":"),
            TestUtils.token(TokenType.NUMBER_TYPE),
            TestUtils.syntax(";"),
            TestUtils.eof()
        )
        val ast = parser.parse(tokens)
        assertEquals(1, ast.size)
        assertTrue(ast[0] is VariableDeclarationNode)
    }

    @Test
    fun parseAssignation() {
        val tokens = listOf(
            TestUtils.token(TokenType.IDENTIFIER, "x"),
            TestUtils.token(TokenType.EQUAL),
            TestUtils.token(TokenType.NUMBER, "5"),
            TestUtils.syntax(";"),
            TestUtils.eof()
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
        val tokens = listOf(
            TestUtils.token(TokenType.LET),
            TestUtils.token(TokenType.IDENTIFIER,"n"),
            TestUtils.syntax(":"),
            TestUtils.token(TokenType.NUMBER_TYPE),
            TestUtils.token(TokenType.EQUAL),
            TestUtils.token(TokenType.NUMBER,"1"),
            TestUtils.token(TokenType.PLUS, "+"),
            TestUtils.token(TokenType.NUMBER,"2"),
            TestUtils.token(TokenType.STAR, "*"),
            TestUtils.token(TokenType.NUMBER,"3"),
            TestUtils.syntax(";"),
            TestUtils.eof()
        )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is VariableDeclarationNode)
    }

    @Test
    fun parse_const_with_init_boolean() {
        val tokens = listOf(
            TestUtils.token(TokenType.CONST),
            TestUtils.token(TokenType.IDENTIFIER,"flag"),
            TestUtils.syntax(":"),
            TestUtils.token(TokenType.BOOLEAN_TYPE),
            TestUtils.token(TokenType.EQUAL),
            TestUtils.token(TokenType.TRUE,"true"),
            TestUtils.syntax(";"),
            TestUtils.eof()
        )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is VariableDeclarationNode || ast[0] is ConstantDeclarationNode)
    }

    @Test
    fun parse_assignation() {
        val tokens = listOf(
            TestUtils.token(TokenType.IDENTIFIER,"x"),
            TestUtils.token(TokenType.EQUAL),
            TestUtils.token(TokenType.NUMBER,"5"),
            TestUtils.syntax(";"),
            TestUtils.eof()
        )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is AssignationNode)
    }

    @Test
    fun parse_print_with_string() {
        val tokens = listOf(
            TestUtils.token(TokenType.PRINTLN),
            TestUtils.syntax("("),
            TestUtils.token(TokenType.STRING,"hello"),
            TestUtils.syntax(")"),
            TestUtils.syntax(";"),
            TestUtils.eof()
        )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is PrintStatementNode)
    }

    @Test
    fun parse_print_with_readEnv_inside() {
        val tokens = listOf(
            TestUtils.token(TokenType.PRINTLN),
            TestUtils.syntax("("),
            TestUtils.token(TokenType.READ_ENV),
            TestUtils.syntax("("),
            TestUtils.token(TokenType.STRING,"USER"),
            TestUtils.syntax(")"),
            TestUtils.syntax(")"),
            TestUtils.syntax(";"),
            TestUtils.eof()
        )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is PrintStatementNode)
    }

    @Test
    fun parse_if_then_else_blocks() {
        val tokens = listOf(
            TestUtils.token(TokenType.IF),
            TestUtils.syntax("("),
            TestUtils.token(TokenType.IDENTIFIER,"cond"),
            TestUtils.syntax(")"),
            TestUtils.syntax("{"),
            TestUtils.token(TokenType.PRINTLN),
            TestUtils.syntax("("),
            TestUtils.token(TokenType.STRING,"T"),
            TestUtils.syntax(")"),
            TestUtils.syntax(";"),
            TestUtils.syntax("}"),
            TestUtils.token(TokenType.ELSE),
            TestUtils.syntax("{"),
            TestUtils.token(TokenType.PRINTLN),
            TestUtils.syntax("("),
            TestUtils.token(TokenType.STRING,"F"),
            TestUtils.syntax(")"),
            TestUtils.syntax(";"),
            TestUtils.syntax("}"),
            TestUtils.eof()
        )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is IfElseNode)
        val ifn = ast[0] as IfElseNode
        assertEquals(1, ifn.ifBranch.size)
        assertEquals(1, ifn.elseBranch.size)
    }

    @Test
    fun parse_if_then_only() {
        val tokens = listOf(
            TestUtils.token(TokenType.IF),
            TestUtils.syntax("("),
            TestUtils.token(TokenType.IDENTIFIER,"ok"),
            TestUtils.syntax(")"),
            TestUtils.syntax("{"),
            TestUtils.token(TokenType.PRINTLN),
            TestUtils.syntax("("),
            TestUtils.token(TokenType.STRING,"OK"),
            TestUtils.syntax(")"),
            TestUtils.syntax(";"),
            TestUtils.syntax("}"),
            TestUtils.eof()
        )
        val ast = parser.parse(tokens)
        assertTrue(ast[0] is IfElseNode)
        val ifn = ast[0] as IfElseNode
        assertEquals(1, ifn.ifBranch.size)
        assertEquals(0, ifn.elseBranch.size)
    }

    @Test
    fun error_token_unexpected_at_start() {
        val tokens = listOf(
            TestUtils.token(TokenType.PLUS,"+"),
            TestUtils.syntax(";"),
            TestUtils.eof()
        )
        val ex = assertFailsWith<ParseException> { parser.parse(tokens) }
        assertTrue(ex.message!!.contains("Token inesperado") || ex.message!!.contains("Inicio de sentencia inesperado"))
    }

    @Test
    fun error_if_condition_literal_true() {
        val tokens = listOf(
            TestUtils.token(TokenType.IF),
            TestUtils.syntax("("),
            TestUtils.token(TokenType.TRUE,"true"),
            TestUtils.syntax(")"),
            TestUtils.syntax("{"),
            TestUtils.syntax("}"),
            TestUtils.eof()
        )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }

    @Test
    fun error_const_without_initializer() {
        val tokens = listOf(
            TestUtils.token(TokenType.CONST),
            TestUtils.token(TokenType.IDENTIFIER,"a"),
            TestUtils.syntax(":"),
            TestUtils.token(TokenType.NUMBER_TYPE),
            TestUtils.syntax(";"),
            TestUtils.eof()
        )
        assertFailsWith<ParseException> { parser.parse(tokens) }
    }
}