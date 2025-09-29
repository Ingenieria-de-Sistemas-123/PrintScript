package org.printscript.formatter

import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.formatter.config.FormatterConfig
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.EmptyExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.VariableDeclarationNode
import org.printscript.token.TokenType
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
                        left = LiteralNode(1.0, TokenType.NUMBER),
                        operator = "+",
                        right =
                            DoubleExpressionNode(
                                left = LiteralNode(2.0, TokenType.NUMBER),
                                operator = "*",
                                right = LiteralNode(3.0, TokenType.NUMBER),
                                position = pos(),
                            ),
                        position = pos(),
                    ),
                position = pos(),
            )

        val ast =
            listOf(
                decl,
                PrintStatementNode(LiteralNode("x", TokenType.STRING), pos()),
                AssignationNode(
                    variable = "x",
                    expression =
                        DoubleExpressionNode(
                            left = LiteralNode("x", TokenType.STRING),
                            operator = "+",
                            right = LiteralNode(1.0, TokenType.NUMBER),
                            position = pos(),
                        ),
                    position = pos(),
                ),
                PrintStatementNode(LiteralNode("x", TokenType.STRING), pos()),
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
                "println(\"x\");\n" +
                "x = \"x\" + 1.0;\n" +
                "println(\"x\");\n"
        assertEquals(expected, pretty)
    }

    @Test
    fun `config sin salto post-semicolon (pero newline final)`() {
        val decl =
            ConstantDeclarationNode(
                identifier = "a",
                valueType = "number",
                expression = LiteralNode(5.0, TokenType.NUMBER),
                position = pos(),
            )
        val ast =
            listOf(
                decl,
                PrintStatementNode(LiteralNode("a", TokenType.STRING), pos()),
            )
        val cfg =
            FormatterConfig(
                lineJumpAfterSemicolon = false,
                spaceAroundEquals = true,
                spaceAfterColon = true,
            )
        val pretty = CodeFormatter().format(ast, cfg)
        assertEquals("let a: number = 5.0;println(\"a\");\n", pretty)
    }

    @Test
    fun `idempotencia (mismo AST, mismo output)`() {
        val decl =
            ConstantDeclarationNode(
                identifier = "s",
                valueType = "string",
                expression = LiteralNode("hola", TokenType.STRING),
                position = pos(),
            )
        val ast =
            listOf(
                decl,
                PrintStatementNode(
                    DoubleExpressionNode(
                        left = LiteralNode("s", TokenType.STRING),
                        operator = "+",
                        right = LiteralNode("2025", TokenType.STRING),
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

    @Test
    fun `declaracion sin inicializador no imprime igual`() {
        val decl = VariableDeclarationNode("name", "string", EmptyExpressionNode, pos())
        val ast = listOf(decl)
        val cfg = FormatterConfig(spaceAfterColon = true)

        val pretty = CodeFormatter().format(ast, cfg)

        assertEquals("let name: string;\n", pretty)
    }
}
