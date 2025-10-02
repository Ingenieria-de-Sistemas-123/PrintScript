package org.printscript.formatter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.formatter.config.BraceStyle
import org.printscript.formatter.config.FormatterConfig
import org.printscript.formatter.emit.ASTEmitter
import org.printscript.formatter.rules.FormatToken
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.EmptyExpressionNode
import org.printscript.parser.node.ErrorNode
import org.printscript.parser.node.IfElseNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.ReadEnvNode
import org.printscript.parser.node.ReadInputNode
import org.printscript.parser.node.VariableDeclarationNode
import org.printscript.token.TokenType

class ASTEmitterAdditionalTest {
    private fun pos() = Position(1, 1)

    @Test
    fun `emite readInput y readEnv`() {
        val readInput = ReadInputNode(LiteralNode("\"Ingresa numero\"", TokenType.STRING), pos())
        val readEnv = ReadEnvNode(LiteralNode("\"HOME\"", TokenType.STRING), pos())
        val ast =
            listOf<ASTNode>(
                PrintStatementNode(readInput, pos()),
                PrintStatementNode(readEnv, pos()),
            )
        val tokens = ASTEmitter(FormatterConfig(braceStyle = BraceStyle.SAME_LINE)).emitProgram(ast)
        val keywordCount = tokens.filterIsInstance<FormatToken.Keyword>().count()
        assertTrue(keywordCount >= 3) // println, readInput, readEnv
        assertTrue(tokens.any { it is FormatToken.StringLit && it.raw == "Ingresa numero" })
        assertTrue(tokens.any { it is FormatToken.StringLit && it.raw == "HOME" })
    }

    @Test
    fun `if con then vacio y else con contenido`() {
        val ifNode =
            IfElseNode(
                ifBranch = emptyList(),
                elseBranch = listOf(PrintStatementNode(LiteralNode("else", TokenType.STRING), pos())),
                condition = LiteralNode("true", TokenType.TRUE),
            )
        val out = ASTEmitter(FormatterConfig(braceStyle = BraceStyle.NEXT_LINE, indentSize = 2)).emitProgram(listOf(ifNode))
        val openBraceCount = out.count { it is FormatToken.OpenBrace }
        assertEquals(2, openBraceCount)
        assertTrue(out.any { it is FormatToken.StringLit && it.raw == "else" })
    }

    @Test
    fun `literal con escapes se normaliza`() {
        val printEscapes = PrintStatementNode(LiteralNode("\"hola\\n\\t\\\"mundo\\\\\"", TokenType.STRING), pos())
        val tokens = ASTEmitter(FormatterConfig(braceStyle = BraceStyle.SAME_LINE)).emitProgram(listOf(printEscapes))
        val stringTok = tokens.filterIsInstance<FormatToken.StringLit>().first()
        assertTrue(stringTok.raw.contains("\n"))
        assertTrue(stringTok.raw.contains("\t"))
    }

    @Test
    fun `sentencia no soportada lanza error`() {
        val unsupported = ErrorNode("dummy")
        val ex =
            assertThrows(IllegalStateException::class.java) {
                ASTEmitter(FormatterConfig(braceStyle = BraceStyle.SAME_LINE)).emitProgram(listOf(unsupported))
            }
        assertTrue(ex.message!!.contains("no soportada"))
    }

    @Test
    fun `expresion no soportada lanza error`() {
        // Usamos una declaración como expresión dentro de println para forzar rama de error en emitExpr
        val declAsExpr = VariableDeclarationNode("a", "number", EmptyExpressionNode, pos())
        val printBad = PrintStatementNode(declAsExpr, pos())
        val ex =
            assertThrows(IllegalStateException::class.java) {
                ASTEmitter(FormatterConfig(braceStyle = BraceStyle.SAME_LINE)).emitProgram(listOf(printBad))
            }
        assertTrue(ex.message!!.contains("no soportada"))
    }

    @Test
    fun `if anidado en else no agrega newline extra al final del bloque interno`() {
        val inner =
            IfElseNode(
                ifBranch = listOf(PrintStatementNode(LiteralNode("inner", TokenType.STRING), pos())),
                elseBranch = emptyList(),
                condition = LiteralNode("flag", TokenType.IDENTIFIER),
            )
        val outer =
            IfElseNode(
                ifBranch = listOf(PrintStatementNode(LiteralNode("outerThen", TokenType.STRING), pos())),
                elseBranch = listOf(inner),
                condition = LiteralNode("cond", TokenType.IDENTIFIER),
            )
        val tokens = ASTEmitter(FormatterConfig(braceStyle = BraceStyle.SAME_LINE, indentSize = 2)).emitProgram(listOf(outer))
        // Verificamos que solo haya los braces esperados: 1 if + 1 else + inner if = 3 pares -> 3 open, 3 close
        val opens = tokens.count { it is FormatToken.OpenBrace }
        val closes = tokens.count { it is FormatToken.CloseBrace }
        assertEquals(3, opens)
        assertEquals(3, closes)
    }

    @Test
    fun `escape desconocido se preserva como caracter literal`() {
        val printStrange = PrintStatementNode(LiteralNode("\"hola\\qfin\"", TokenType.STRING), pos())
        val tokens = ASTEmitter(FormatterConfig(braceStyle = BraceStyle.SAME_LINE)).emitProgram(listOf(printStrange))
        val str = tokens.filterIsInstance<FormatToken.StringLit>().first().raw
        assertTrue(str.contains("qfin"))
    }
}
