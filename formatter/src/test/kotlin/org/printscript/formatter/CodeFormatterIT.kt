package org.printscript.formatter

import org.printscript.formatter.config.FormatterConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import node.ASTNode
import node.AssignationNode
import node.DeclarationNode
import node.DoubleExpressionNode
import node.LiteralNode
import node.PrintNode
import org.printscript.common.Position

class CodeFormatterIT {

    private fun pos() = Position(1,1)

    @Test
    fun `declara, imprime, reasigna, imprime (happy path)`() {
        val ast = listOf<ASTNode>(
            DeclarationNode(
                name = "x", type = "number",
                value = DoubleExpressionNode(
                    left = LiteralNode(1.0, "number", pos()),
                    operator = "+",
                    right = DoubleExpressionNode(
                        left = LiteralNode(2.0, "number", pos()),
                        operator = "*",
                        right = LiteralNode(3.0, "number", pos()),
                        position = pos()
                    ),
                    position = pos()
                ),
                position = pos()
            ),
            PrintNode(LiteralNode("x","identifier",pos()), pos()),
            AssignationNode("x", DoubleExpressionNode(
                left = LiteralNode("x","identifier",pos()),
                operator = "+",
                right = LiteralNode(1.0,"number",pos()),
                position = pos()
            ), pos()),
            PrintNode(LiteralNode("x","identifier",pos()), pos())
        )

        val cfg = FormatterConfig(
            spaceBeforeColon = false,
            spaceAfterColon = true,
            spaceAroundEquals = true,
            spaceAroundOperators = true,
            lineJumpBeforePrintln = 0,
            lineJumpAfterSemicolon = true,
            indentSize = 4
        )

        val pretty = CodeFormatter().format(ast, cfg)
        val expected =
            "let x: number = 1.0 + 2.0 * 3.0;\n" +
                    "println(x);\n" +
                    "x = x + 1.0;\n" +
                    "println(x);\n"
        assertEquals(expected, pretty)
    }

    @Test
    fun `config sin salto post-semicolon (pero newline final)`() {
        val ast = listOf<ASTNode>(
            DeclarationNode("a","number", LiteralNode(5.0,"number",pos()), pos()),
            PrintNode(LiteralNode("a","identifier",pos()), pos())
        )
        val cfg = FormatterConfig(
            lineJumpAfterSemicolon = false, // no agrega '\n' tras cada ';'
            spaceAroundEquals = true,
            spaceAfterColon = true
        )
        val pretty = CodeFormatter().format(ast, cfg)
        assertEquals("let a: number = 5.0;println(a);\n", pretty)
    }

    @Test
    fun `idempotencia (mismo AST, mismo output)`() {
        val ast = listOf<ASTNode>(
            DeclarationNode("s","string", LiteralNode("hola","string",pos()), pos()),
            PrintNode(DoubleExpressionNode(
                left = LiteralNode("s","identifier",pos()),
                operator = "+",
                right = LiteralNode("2025","string",pos()),
                position = pos()
            ), pos())
        )
        val cfg = FormatterConfig()

        val f = CodeFormatter()
        val first  = f.format(ast, cfg)
        val second = f.format(ast, cfg)
        assertEquals(first, second)
    }
}
