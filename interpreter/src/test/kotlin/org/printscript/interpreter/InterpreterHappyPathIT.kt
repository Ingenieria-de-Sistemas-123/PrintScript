package org.printscript.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.util.MapEnvProvider
import org.printscript.interpreter.util.QueueInputProvider
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.IfElseNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.VariableDeclarationNode

@Tag("integration")
@DisplayName("Interpreter – Happy Path")
class InterpreterHappyPathIT : BaseInterpreterIT() {
    @Test
    fun `declara number con precedencia, imprime, concatena string, reasigna y vuelve a imprimir`() {
        val ast =
            listOf(
                VariableDeclarationNode("x", "number", plus(num(1.0), star(num(2.0), num(3.0))), pos()),
                PrintStatementNode(id("x"), pos()),
                VariableDeclarationNode("s", "string", str("hola!"), pos()),
                PrintStatementNode(plus(id("s"), str("2025")), pos()),
                AssignationNode("x", plus(id("x"), num(1.0)), pos()),
                PrintStatementNode(id("x"), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("7", "hola!2025", "8"), out.lines)
    }

    @Test
    fun `aritmetica basica y formateo de enteros sin coma`() {
        val ast =
            listOf(
                PrintStatementNode(minus(num(3.0), num(1.0)), pos()),
                PrintStatementNode(star(num(2.0), num(5.0)), pos()),
                PrintStatementNode(slash(num(8.0), num(2.0)), pos()),
                PrintStatementNode(plus(num(2.0), num(0.0)), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("2", "10", "4", "2"), out.lines)
    }

    @Test
    fun `concatena string+string`() {
        val ast =
            listOf(
                ConstantDeclarationNode("a", "string", str("ab!"), pos()),
                ConstantDeclarationNode("b", "string", str("cd!"), pos()),
                PrintStatementNode(plus(id("a"), id("b")), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("ab!cd!"), out.lines)
    }

    @Test
    fun `if con variable booleana como condicion ejecuta rama verdadera`() {
        val ast =
            listOf(
                VariableDeclarationNode("flag", "boolean", boolTrue(), pos()),
                IfElseNode(
                    ifBranch = listOf(PrintStatementNode(str("then"), pos())),
                    elseBranch = listOf(PrintStatementNode(str("else"), pos())),
                    condition = id("flag"),
                ),
            )

        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)

        assertEquals(listOf("then"), out.lines)
    }

    @Test
    fun `if con variable booleana como condicion ejecuta rama falsa`() {
        val ast =
            listOf(
                VariableDeclarationNode("flag", "boolean", boolFalse(), pos()),
                IfElseNode(
                    ifBranch = listOf(PrintStatementNode(str("then"), pos())),
                    elseBranch = listOf(PrintStatementNode(str("else"), pos())),
                    condition = id("flag"),
                ),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("else"), out.lines)
    }

    @Test
    fun `readInput inicializa variable string`() {
        val ctx = IOContext(QueueInputProvider(listOf("Ada")), MapEnvProvider(emptyMap()))
        val ast =
            listOf(
                VariableDeclarationNode("name", "string", readInput(str("Nombre:")), pos()),
                PrintStatementNode(id("name"), pos()),
            )

        val (interpreter, out) = newInterpreterWithBuffer(ctx)
        interpreter.execute(ast)

        assertEquals(listOf("Ada"), out.lines)
        assertEquals("Ada", interpreter.environmentSnapshot()["name"])
    }

    @Test
    fun `readEnv obtiene valores del provider`() {
        val ctx = IOContext(QueueInputProvider(emptyList()), MapEnvProvider(mapOf("USER" to "octavia")))
        val ast =
            listOf(
                VariableDeclarationNode("user", "string", readEnv(str("USER")), pos()),
                PrintStatementNode(id("user"), pos()),
            )

        val (interpreter, out) = newInterpreterWithBuffer(ctx)
        interpreter.execute(ast)

        assertEquals(listOf("octavia"), out.lines)
        assertEquals("octavia", interpreter.environmentSnapshot()["user"])
    }

    @Test
    fun `concatena string + number`() {
        val ast =
            listOf(
                ConstantDeclarationNode("s", "string", str("hola!"), pos()),
                PrintStatementNode(plus(id("s"), num(2025.0)), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("hola!2025"), out.lines)
    }

    @Test
    fun `print de variable declarada imprime su valor`() {
        val ast =
            listOf(
                VariableDeclarationNode("x", "number", num(42.0), pos()),
                PrintStatementNode(id("x"), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("42"), out.lines)
        assertEquals("42", interpreter.environmentSnapshot()["x"])
    }
}
