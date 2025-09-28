package org.printscript.parser.builders

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.printscript.parser.builder.AssignationBuilder
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.ReadInputNode
import org.printscript.parser.testutil.TestUtils
import org.printscript.token.TokenType

class AssignationBuilderTest {
    @Test
    fun build_with_parenthesized_expression_keeps_grouping() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "value"),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.NUMBER, "1"),
                TestUtils.token(TokenType.PLUS, "+"),
                TestUtils.token(TokenType.NUMBER, "2"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
            )

        val node = AssignationBuilder(tokens).build()

        Assertions.assertTrue(node is AssignationNode)
        val expr = (node as AssignationNode).expression
        Assertions.assertTrue(expr is DoubleExpressionNode)
        val doubleExpr = expr as DoubleExpressionNode
        Assertions.assertTrue(doubleExpr.left is LiteralNode<*>)
        Assertions.assertTrue(doubleExpr.right is LiteralNode<*>)
    }

    @Test
    fun build_with_read_input_initializer_collects_call_tokens() {
        val tokens =
            listOf(
                TestUtils.token(TokenType.IDENTIFIER, "name"),
                TestUtils.token(TokenType.EQUAL),
                TestUtils.token(TokenType.READ_INPUT, "readInput"),
                TestUtils.syntax("("),
                TestUtils.token(TokenType.STRING, "Name?"),
                TestUtils.syntax(")"),
                TestUtils.syntax(";"),
            )

        val node = AssignationBuilder(tokens).build()

        Assertions.assertTrue(node is AssignationNode)
        val expr = (node as AssignationNode).expression
        Assertions.assertTrue(expr is ReadInputNode)
        val readInput = expr as ReadInputNode
        Assertions.assertTrue(readInput.expression is LiteralNode<*>)
    }
}
