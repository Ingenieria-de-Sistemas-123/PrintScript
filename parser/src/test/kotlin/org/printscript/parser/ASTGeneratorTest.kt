package org.printscript.parser

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.parser.helpers.ASTGenerator
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.IfElseNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.VariableDeclarationNode
import org.printscript.parser.testutil.TestUtils
import org.printscript.token.TokenType
import kotlin.test.assertFailsWith

class ASTGeneratorTest {
    private val generator = ASTGenerator()

    @Test
    fun dispatchLet() {
        val statement =
            listOf(
                TestUtils.token(TokenType.LET),
                TestUtils.token(TokenType.IDENTIFIER, "a"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.NUMBER_TYPE),
                TestUtils.syntax(";"),
            )
        assertTrue(generator.createAST(statement) is VariableDeclarationNode)
    }

    @Test
    fun dispatchConst() {
        val stmt =
            listOf(
                TestUtils.token(TokenType.CONST),
                TestUtils.token(TokenType.IDENTIFIER, "c"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.STRING_TYPE),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.STRING, "hi"),
                TestUtils.syntax(";"),
            )
        val node = generator.createAST(stmt)
        assertTrue(
            node is VariableDeclarationNode || node::class.simpleName?.contains("Constant", ignoreCase = true) == true,
            "Se esperaba VariableDeclarationNode o ConstantDeclarationNode pero fue ${node::class.simpleName}",
        )
    }

    @Test
    fun dispatch_identifier_assign() {
        val stmt =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.syntax(";"),
            )
        assertTrue(generator.createAST(stmt) is AssignationNode)
    }

    @Test
    fun dispatch_println() {
        val stmt =
            listOf(
                TestUtils.token(TokenType.PRINTLN),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "s"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
            )
        assertTrue(generator.createAST(stmt) is PrintStatementNode)
    }

    @Test
    fun dispatch_if() {
        val stmt =
            listOf(
                TestUtils.token(TokenType.IF),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.IDENTIFIER, "b"),
                TestUtils.syntax(")"),
                TestUtils.syntax("{"),
                TestUtils.syntax("}"),
            )
        assertTrue(generator.createAST(stmt) is IfElseNode)
    }

    @Test
    fun error_unknown_start_token() {
        val stmt =
            listOf(
                TestUtils.token(TokenType.PLUS, "+"),
            )
        val ex =
            assertFailsWith<ParseException> {
                generator.createAST(stmt)
            }
        assertTrue(ex.message!!.contains("Token inesperado"))
    }

    @Test
    fun dispatch_unknown_token_throws() {
        val stmt =
            listOf(
                TestUtils.token(TokenType.SLASH, "/"),
            )
        val ex = assertFailsWith<ParseException> { generator.createAST(stmt) }
        assertTrue(ex.message!!.contains("Token inesperado") || ex.message!!.contains("Inicio de sentencia inesperado"))
    }
}
