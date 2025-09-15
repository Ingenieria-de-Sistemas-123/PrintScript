package org.printscript.formatter

import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.formatter.config.FormatterConfig
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import kotlin.test.assertEquals

class CodeFormatterIT {
    private fun pos() = Position(1, 1)

    @Test
    fun `declara, imprime, reasigna, imprime (happy path)`() {
        val ast =
            listOf(
                DeclarationNode(
                    name = "x",
                    type = "number",
                    value =
                        DoubleExpressionNode(
                            left = LiteralNode(1.0, "number", pos()),
                            operator = "+",
                            right =
                                DoubleExpressionNode(
                                    left = LiteralNode(2.0, "number", pos()),
                                    operator = "*",
                                    right = LiteralNode(3.0, "number", pos()),
                                    position = pos(),
                                ),
                            position = pos(),
                        ),
                    position = pos(),
                ),
                PrintStatementNode(LiteralNode("x", "identifier", pos()), pos()),
                AssignationNode(
                    "x",
                    DoubleExpressionNode(
                        left = LiteralNode("x", "identifier", pos()),
                        operator = "+",
                        right = LiteralNode(1.0, "number", pos()),
                        position = pos(),
                    ),
                    pos(),
                ),
                PrintStatementNode(LiteralNode("x", "identifier", pos()), pos()),
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
        val ast =
            listOf(
                DeclarationNode("a", "number", LiteralNode(5.0, "number", pos()), pos()),
                PrintStatementNode(LiteralNode("a", "identifier", pos()), pos()),
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
        val ast =
            listOf(
                DeclarationNode("s", "string", LiteralNode("hola", "string", pos()), pos()),
                PrintStatementNode(
                    DoubleExpressionNode(
                        left = LiteralNode("s", "identifier", pos()),
                        operator = "+",
                        right = LiteralNode("2025", "string", pos()),
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
