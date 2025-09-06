package org.printscript.formatter.emit

import node.ASTNode
import node.AssignationNode
import node.DeclarationNode
import node.LiteralNode
import node.PrintNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.rules.FormatToken

class AstEmitterTest {
    private fun pos() = Position(1, 1)

    @Test
    fun `declaracion emite tokens esperados`() {
        val ast =
            listOf<ASTNode>(
                DeclarationNode(
                    name = "v",
                    type = "number",
                    value = LiteralNode(42.0, "number", pos()),
                    position = pos(),
                ),
            )
        val tokens = AstEmitter(FormatterConfig()).emitProgram(ast)
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
    fun `asignacion usa AssignationNode_type_como_valor`() {
        val ast =
            listOf<ASTNode>(
                AssignationNode(
                    name = "x",
                    // ojo: el parser llama 'type' al VALOR de la asignación
                    type = LiteralNode(1.0, "number", pos()),
                    position = pos(),
                ),
            )
        val tokens = AstEmitter(FormatterConfig()).emitProgram(ast)
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
    fun `println emite keyword y paréntesis`() {
        val ast =
            listOf<ASTNode>(
                PrintNode(LiteralNode("x", "identifier", pos()), pos()),
            )
        val tokens = AstEmitter(FormatterConfig(lineJumpBeforePrintln = 1)).emitProgram(ast)
        val expected =
            listOf(
                FormatToken.NewLine(1),
                FormatToken.Keyword("println"),
                FormatToken.OpenParen,
                FormatToken.Ident("x"),
                FormatToken.CloseParen,
                FormatToken.Semicolon,
            )
        assertEquals(expected, tokens)
    }
}
