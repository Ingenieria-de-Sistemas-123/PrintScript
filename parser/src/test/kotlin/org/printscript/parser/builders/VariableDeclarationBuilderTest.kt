package org.printscript.parser.builders

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.parser.builder.VariableDeclarationBuilder
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.ReadInputNode
import org.printscript.parser.node.VariableDeclarationNode
import org.printscript.parser.testutil.TestUtils
import org.printscript.token.TokenType

class VariableDeclarationBuilderTest {
    @Test
    fun build_with_parenthesized_initializer_keeps_expression_tokens() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.LET),
                TestUtils.token(TokenType.IDENTIFIER, "x"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.NUMBER_TYPE),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
            )

        val node = VariableDeclarationBuilder(tokens).build()

        assertTrue(node is VariableDeclarationNode)
        val expr = (node as VariableDeclarationNode).expression
        assertTrue(expr is DoubleExpressionNode)
        val doubleExpr = expr as DoubleExpressionNode
        assertEquals("+", doubleExpr.operator)
        assertTrue(doubleExpr.left is LiteralNode<*>)
        assertTrue(doubleExpr.right is LiteralNode<*>)
    }

    @Test
    fun build_with_read_input_initializer_collects_full_call() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.LET),
                TestUtils.token(TokenType.IDENTIFIER, "name"),
                TestUtils.syntax(":"),
                TestUtils.token(TokenType.STRING_TYPE),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.READ_INPUT, "readInput"),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "Name?"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
            )

        val node = VariableDeclarationBuilder(tokens).build()

        assertTrue(node is VariableDeclarationNode)
        val expr = (node as VariableDeclarationNode).expression
        assertTrue(expr is ReadInputNode)
        val readInput = expr as ReadInputNode
        assertTrue(readInput.expression is LiteralNode<*>)
        val literal = readInput.expression as LiteralNode<*>
        assertEquals("Name?", literal.value)
    }
}