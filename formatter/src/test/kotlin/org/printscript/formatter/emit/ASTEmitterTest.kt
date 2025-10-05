package org.printscript.formatter.emit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.FormatToken
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.EmptyExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.VariableDeclarationNode
import org.printscript.token.TokenType

class ASTEmitterTest {
    private fun pos() = Position(1, 1)

    @Test
    fun `declaracion emite tokens esperados`() {
        val decl =
            ConstantDeclarationNode(
                identifier = "v",
                valueType = "number",
                expression = LiteralNode(42.0, TokenType.NUMBER),
                position = pos(),
            )
        val ast = listOf<ASTNode>(decl)

        val tokens = ASTEmitter(FormatterConfig(braceStyle = org.printscript.formatter.config.BraceStyle.SAME_LINE)).emitProgram(ast)
        val expected =
            listOf(
                FormatToken.Keyword("let"),
                FormatToken.Ident("v"),
                FormatToken.Colon,
                FormatToken.TypeName("number"),
                FormatToken.Equals,
                FormatToken.NumberLit("42"),
                FormatToken.Semicolon,
            )
        assertEquals(expected, tokens)
    }

    @Test
    fun `asignacion emite tokens esperados`() {
        val ast =
            listOf<ASTNode>(
                AssignationNode(
                    variable = "x",
                    expression = LiteralNode(1.0, TokenType.NUMBER),
                    position = pos(),
                ),
            )
        val tokens = ASTEmitter(FormatterConfig(braceStyle = org.printscript.formatter.config.BraceStyle.SAME_LINE)).emitProgram(ast)
        val expected =
            listOf(
                FormatToken.Ident("x"),
                FormatToken.Equals,
                FormatToken.NumberLit("1"),
                FormatToken.Semicolon,
            )
        assertEquals(expected, tokens)
    }

    @Test
    fun `println emite keyword y parentesis con ident adentro`() {
        val ast =
            listOf<ASTNode>(
                PrintStatementNode(LiteralNode("x", TokenType.STRING), pos()),
            )
        val tokens =
            ASTEmitter(
                FormatterConfig(printLineBreaksAfter = 1, braceStyle = org.printscript.formatter.config.BraceStyle.SAME_LINE),
            ).emitProgram(ast)
        val expected =
            listOf(
                FormatToken.Keyword("println"),
                FormatToken.OpenParen,
                FormatToken.StringLit("x"),
                FormatToken.CloseParen,
                FormatToken.Semicolon,
                FormatToken.NewLine(2),
            )
        assertEquals(expected, tokens)
    }

    @Test
    fun `declaracion sin inicializador no emite igual`() {
        val decl = VariableDeclarationNode("name", "string", EmptyExpressionNode, pos())
        val tokens =
            ASTEmitter(
                FormatterConfig(braceStyle = org.printscript.formatter.config.BraceStyle.SAME_LINE),
            ).emitProgram(listOf(decl))
        val expected =
            listOf(
                FormatToken.Keyword("let"),
                FormatToken.Ident("name"),
                FormatToken.Colon,
                FormatToken.TypeName("string"),
                FormatToken.Semicolon,
            )
        assertEquals(expected, tokens)
    }
}
