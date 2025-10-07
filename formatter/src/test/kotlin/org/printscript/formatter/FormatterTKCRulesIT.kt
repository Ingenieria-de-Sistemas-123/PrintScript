package org.printscript.formatter

import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.formatter.config.BraceStyle
import org.printscript.formatter.config.FormatterConfig
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.IfElseNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.token.TokenType
import kotlin.test.assertEquals

class FormatterTKCRulesIT {
    private fun pos() = Position(1, 1)

    @Test
    fun `no spacing around equals`() {
        val decl = ConstantDeclarationNode("a", "number", LiteralNode(5.0, TokenType.NUMBER), pos())
        val cfg =
            FormatterConfig(
                spaceBeforeColon = false,
                spaceAfterColon = false,
                spaceAroundEquals = false,
                spaceAroundOperators = true,
                lineJumpAfterSemicolon = false,
                braceStyle = BraceStyle.SAME_LINE,
            )
        val out = CodeFormatter().format(listOf(decl), cfg)
        assertEquals("let a:number=5;", out)
    }

    @Test
    fun `spacing around equals`() {
        val decl = ConstantDeclarationNode("a", "number", LiteralNode(5.0, TokenType.NUMBER), pos())
        val cfg =
            FormatterConfig(
                spaceBeforeColon = false,
                spaceAfterColon = true,
                spaceAroundEquals = true,
                lineJumpAfterSemicolon = false,
                braceStyle = BraceStyle.SAME_LINE,
            )
        val out = CodeFormatter().format(listOf(decl), cfg)
        assertEquals("let a: number = 5;", out)
    }

    @Test
    fun `single-space separation around parens and semicolon`() {
        val ast = listOf(PrintStatementNode(LiteralNode("s", TokenType.IDENTIFIER), pos()))
        val cfg =
            FormatterConfig(
                singleSpaceSeparation = true,
                lineJumpAfterSemicolon = false,
                braceStyle = BraceStyle.SAME_LINE,
            )
        val out = CodeFormatter().format(ast, cfg)
        // One space before '(', between tokens, before ')'; semicolon stays attached
        assertEquals("println ( s );", out, "Actual='$out'")
    }

    @Test
    fun `println inserts extra line breaks after`() {
        val ast =
            listOf(
                PrintStatementNode(LiteralNode("a", TokenType.STRING), pos()),
                PrintStatementNode(LiteralNode("b", TokenType.STRING), pos()),
            )
        val cfg =
            FormatterConfig(
                printLineBreaksAfter = 2,
                lineJumpAfterSemicolon = true,
                braceStyle = BraceStyle.SAME_LINE,
            )
        val out = CodeFormatter().format(ast, cfg)
        // Between prints there should be two blank lines => three total newlines after the first
        val expected = "println(\"a\");\n\n\nprintln(\"b\");"
        assertEquals(expected, out)
    }

    @Test
    fun `println without extra line breaks keeps single separator`() {
        val ast =
            listOf(
                PrintStatementNode(LiteralNode("a", TokenType.STRING), pos()),
                PrintStatementNode(LiteralNode("b", TokenType.STRING), pos()),
            )
        val cfg = FormatterConfig(lineJumpAfterSemicolon = true, braceStyle = BraceStyle.SAME_LINE)
        val out = CodeFormatter().format(ast, cfg)
        assertEquals("println(\"a\");\nprintln(\"b\");", out)
    }

    @Test
    fun `println with one extra line break`() {
        val ast =
            listOf(
                PrintStatementNode(LiteralNode("a", TokenType.STRING), pos()),
                PrintStatementNode(LiteralNode("b", TokenType.STRING), pos()),
            )
        val cfg =
            FormatterConfig(
                printLineBreaksAfter = 1,
                lineJumpAfterSemicolon = true,
                braceStyle = BraceStyle.SAME_LINE,
            )
        val out = CodeFormatter().format(ast, cfg)
        assertEquals("println(\"a\");\n\nprintln(\"b\");", out)
    }

    @Test
    fun `operators spacing on and off`() {
        val assign =
            AssignationNode(
                variable = "x",
                expression =
                    DoubleExpressionNode(
                        left = LiteralNode(5.0, TokenType.NUMBER),
                        operator = "+",
                        right =
                            DoubleExpressionNode(
                                left = LiteralNode(4.0, TokenType.NUMBER),
                                operator = "*",
                                right =
                                    DoubleExpressionNode(
                                        left = LiteralNode(3.0, TokenType.NUMBER),
                                        operator = "/",
                                        right = LiteralNode(2.0, TokenType.NUMBER),
                                        position = pos(),
                                    ),
                                position = pos(),
                            ),
                        position = pos(),
                    ),
                position = pos(),
            )
        val on =
            CodeFormatter().format(
                listOf(assign),
                FormatterConfig(
                    spaceAroundOperators = true,
                    spaceAroundEquals = true,
                    lineJumpAfterSemicolon = false,
                ),
            )
        val off =
            CodeFormatter().format(
                listOf(assign),
                FormatterConfig(
                    spaceAroundOperators = false,
                    spaceAroundEquals = true,
                    lineJumpAfterSemicolon = false,
                ),
            )
        assertEquals("x = 5 + 4 * 3 / 2;", on)
        assertEquals("x = 5+4*3/2;", off)
    }

    @Test
    fun `if braces below line`() {
        val node =
            IfElseNode(
                ifBranch = listOf(PrintStatementNode(LiteralNode("then", TokenType.STRING), pos())),
                elseBranch = listOf(PrintStatementNode(LiteralNode("else", TokenType.STRING), pos())),
                condition = LiteralNode(true, TokenType.TRUE),
            )
        val cfg = FormatterConfig(braceStyle = BraceStyle.NEXT_LINE, indentSize = 2)
        val out = CodeFormatter().format(listOf(node), cfg)
        val expected = "if (true)\n{\n  println(\"then\");\n}\nelse\n{\n  println(\"else\");\n}"
        assertEquals(expected, out)
    }
}
