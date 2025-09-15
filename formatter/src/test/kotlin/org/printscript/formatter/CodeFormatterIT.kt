package org.printscript.formatter

import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.formatter.config.FormatterConfig
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import kotlin.test.assertEquals

class CodeFormatterIT {
    private fun pos() = Position(1, 1)

    @Test
    fun `declara, imprime, reasigna, imprime (happy path)`() {
        val decl =
            ConstantDeclarationNode(
                identifier = "x",
                valueType = "number",
                expression =
                    DoubleExpressionNode(
                        left = LiteralNode(1.0),
                        operator = "+",
                        right =
                            DoubleExpressionNode(
                                left = LiteralNode(2.0),
                                operator = "*",
                                right = LiteralNode(3.0),
                                position = pos(),
                            ),
                        position = pos(),
                    ),
                position = pos(),
            )

        val ast =
            listOf(
                decl,
                PrintStatementNode(LiteralNode("x"), pos()),
                AssignationNode(
                    variable = "x",
                    expression =
                        DoubleExpressionNode(
                            left = LiteralNode("x"),
                            operator = "+",
                            right = LiteralNode(1.0),
                            position = pos(),
                        ),
                    position = pos(),
                ),
                PrintStatementNode(LiteralNode("x"), pos()),
            )

        val cfg =
            FormatterConfig(
                spaceBeforeColon = false,
                spaceAfterColon = true,
                spaceAroundEquals = true,
                spaceAroundOperators = true,
                lineJumpBeforePrintln = 0,
                lineJumpAfterSemicolon = true,
                indentSize = 4,
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
        val decl =
            ConstantDeclarationNode(
                identifier = "a",
                valueType = "number",
                expression = LiteralNode(5.0),
                position = pos(),
            )
        val ast =
            listOf(
                decl,
                PrintStatementNode(LiteralNode("a"), pos()),
            )
        val cfg =
            FormatterConfig(
                lineJumpAfterSemicolon = false,
                spaceAroundEquals = true,
                spaceAfterColon = true,
            )
        val pretty = CodeFormatter().format(ast, cfg)
        assertEquals("let a: number = 5.0;println(a);\n", pretty)
    }

    @Test
    fun `idempotencia (mismo AST, mismo output)`() {
        val decl =
            ConstantDeclarationNode(
                identifier = "s",
                valueType = "string",
                expression = LiteralNode("hola"),
                position = pos(),
            )
        val ast =
            listOf(
                decl,
                PrintStatementNode(
                    DoubleExpressionNode(
                        left = LiteralNode("s"),
                        operator = "+",
                        right = LiteralNode("2025"),
                        position = pos(),
                    ),
                    pos(),
                ),
            )
        val cfg = FormatterConfig()

        val f = CodeFormatter()
        val first = f.format(ast, cfg)
        val second = f.format(ast, cfg)
        assertEquals(first, second)
    }
}
