package org.printscript.formatter

import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.formatter.config.BraceStyle
import org.printscript.formatter.config.FormatterConfig
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.EmptyExpressionNode
import org.printscript.parser.node.IfElseNode
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
                printLineBreaksAfter = 0,
                lineJumpAfterSemicolon = true,
                indentSize = 4,
                braceStyle = BraceStyle.SAME_LINE,
            )

        val pretty = CodeFormatter().format(ast, cfg)
        val expected =
            "let x: number = 1 + 2 * 3;\n" +
                "println(\"x\");\n" +
                "x = \"x\" + 1;\n" +
                "println(\"x\");"
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
                spaceBeforeColon = false,
                spaceAroundOperators = true,
                printLineBreaksAfter = 0,
                indentSize = 4,
                braceStyle = BraceStyle.SAME_LINE,
            )
        val pretty = CodeFormatter().format(ast, cfg)
        assertEquals("let a: number = 5;println(\"a\");", pretty)
    }

    /*@Test
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
        val cfg = FormatterConfig(braceStyle = BraceStyle.SAME_LINE)

        val f = CodeFormatter()
        val first = f.format(ast, cfg)
        val second = f.format(ast, cfg)
        assertEquals(first, second)
    }

    @Test
    fun `declaracion sin inicializador no imprime igual`() {
        val decl = VariableDeclarationNode("name", "string", EmptyExpressionNode, pos())
        val ast = listOf(decl)
        val cfg = FormatterConfig(spaceAfterColon = true, braceStyle = BraceStyle.SAME_LINE)

        val pretty = CodeFormatter().format(ast, cfg)

        assertEquals("let name: string;", pretty)
    }

    @Test
    fun `space before colon rule does not alter trailing spacing`() {
        val decl =
            ConstantDeclarationNode(
                identifier = "value",
                valueType = "number",
                expression = LiteralNode(5.0, TokenType.NUMBER),
                position = pos(),
            )
        val cfg =
            FormatterConfig(
                spaceBeforeColon = true,
                spaceAfterColon = false,
                spaceAroundEquals = true,
                lineJumpAfterSemicolon = false,
                braceStyle = BraceStyle.SAME_LINE,
            )

        val pretty = CodeFormatter().format(listOf(decl), cfg)

        assertEquals("let value :number = 5;", pretty)
    }

    @Test
    fun `space after colon rule preserves leading spacing`() {
        val decl =
            ConstantDeclarationNode(
                identifier = "value",
                valueType = "number",
                expression = LiteralNode(5.0, TokenType.NUMBER),
                position = pos(),
            )
        val cfg =
            FormatterConfig(
                spaceBeforeColon = false,
                spaceAfterColon = true,
                spaceAroundEquals = true,
                lineJumpAfterSemicolon = false,
                braceStyle = BraceStyle.SAME_LINE,
            )

        val pretty = CodeFormatter().format(listOf(decl), cfg)

        assertEquals("let value: number = 5;", pretty)
    }*/

    @Test
    fun `if con braces en misma linea`() {
        val ifNode =
            IfElseNode(
                ifBranch = listOf(PrintStatementNode(LiteralNode("then", TokenType.STRING), pos())),
                elseBranch = listOf(PrintStatementNode(LiteralNode("else", TokenType.STRING), pos())),
                condition = LiteralNode("flag", TokenType.IDENTIFIER),
            )

        val cfg =
            FormatterConfig(
                braceStyle = BraceStyle.SAME_LINE,
                indentSize = 4,
            )

        val pretty = CodeFormatter().format(listOf(ifNode), cfg)
        val expected =
            "if (flag) {\n" +
                "    println(\"then\");\n" +
                "} else {\n" +
                "    println(\"else\");\n" +
                "}"
        assertEquals(expected, pretty)
    }

    @Test
    fun `if con braces debajo e indent custom`() {
        val ifNode =
            IfElseNode(
                ifBranch = listOf(PrintStatementNode(LiteralNode("body", TokenType.STRING), pos())),
                elseBranch = emptyList(),
                condition = LiteralNode("cond", TokenType.IDENTIFIER),
            )

        val cfg =
            FormatterConfig(
                braceStyle = BraceStyle.NEXT_LINE,
                indentSize = 2,
                lineJumpAfterSemicolon = false,
            )

        val pretty = CodeFormatter().format(listOf(ifNode), cfg)
        val expected =
            "if (cond)\n" +
                "{\n" +
                "  println(\"body\");\n" +
                "}"
        assertEquals(expected, pretty)
    }

    @Test
    fun `disabling equals spacing keeps original layout when source is provided`() {
        val assign = AssignationNode("texto", LiteralNode("valor", TokenType.STRING), pos())
        val cfg =
            FormatterConfig(
                spaceAroundEquals = false,
                lineJumpAfterSemicolon = false,
                braceStyle = BraceStyle.SAME_LINE,
            )

        val original = "texto =\"valor\";"
        val formatted = CodeFormatter().format(listOf(assign), cfg, original)

        assertEquals(original, formatted)
    }

    @Test
    fun `disabling semicolon newline preserves original breaks`() {
        val ast =
            listOf(
                PrintStatementNode(LiteralNode("a", TokenType.STRING), pos()),
                PrintStatementNode(LiteralNode("b", TokenType.STRING), pos()),
            )
        val cfg =
            FormatterConfig(
                lineJumpAfterSemicolon = false,
                braceStyle = BraceStyle.SAME_LINE,
            )
        val original = "println(\"a\");\nprintln(\"b\");"

        val formatted = CodeFormatter().format(ast, cfg, original)

        assertEquals(original, formatted)
    }
}
