package org.printscript.formatter.emit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.FormatToken
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode

class ASTEmitterTest {
    private fun pos() = Position(1, 1)

    @Test
    fun `declaracion emite tokens esperados`() {
        val decl =
            ConstantDeclarationNode(
                identifier = "v",
                valueType = "number",
                expression = LiteralNode(42.0),
                position = pos(),
            )
        val ast = listOf<ASTNode>(decl)

        val tokens = ASTEmitter(FormatterConfig()).emitProgram(ast)
        val expected =
            listOf(
                FormatToken.Keyword("let"),
                FormatToken.Ident("v"),
                FormatToken.Colon,
                FormatToken.TypeName("number"),
                FormatToken.Equals,
                FormatToken.NumberLit("42.0"),
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
                    expression = LiteralNode(1.0),
                    position = pos(),
                ),
            )
        val tokens = ASTEmitter(FormatterConfig()).emitProgram(ast)
        val expected =
            listOf(
                FormatToken.Ident("x"),
                FormatToken.Equals,
                FormatToken.NumberLit("1.0"),
                FormatToken.Semicolon,
            )
        assertEquals(expected, tokens)
    }

    @Test
    fun `println emite keyword y parentesis con ident adentro`() {
        val ast =
            listOf<ASTNode>(
                PrintStatementNode(LiteralNode("x"), pos()),
            )
        val tokens = ASTEmitter(FormatterConfig(lineJumpBeforePrintln = 1)).emitProgram(ast)
        val expected =
            listOf(
                FormatToken.NewLine(1),
                FormatToken.Keyword("println"),
                FormatToken.OpenParen,
                FormatToken.StringLit("x"),
                FormatToken.CloseParen,
                FormatToken.Semicolon,
            )
        assertEquals(expected, tokens)
    }
}
