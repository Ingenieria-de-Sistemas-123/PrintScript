package org.printscript.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.util.CollectingErrorHandler
import org.printscript.interpreter.util.MapEnvProvider
import org.printscript.interpreter.util.QueueInputProvider
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.VariableDeclarationNode

@Tag("integration")
@DisplayName("Interpreter – Errores")
class InterpreterErrorsIT : BaseInterpreterIT() {
    @Test
    fun `division por cero`() {
        val ast = listOf(PrintStatementNode(slash(num(1.0), num(0.0)), pos()))
        val (interpreter, out) = newInterpreterWithBuffer()
        val handler = CollectingErrorHandler()

        interpreter.execute(ast, handler)

        assertEquals(listOf("División por cero"), handler.errors)
        assertEquals(emptyList<String>(), out.lines)
    }

    @Test
    fun `identificador no declarado`() {
        val ast = listOf(PrintStatementNode(id("x"), pos()))
        val (interpreter, out) = newInterpreterWithBuffer()
        val handler = CollectingErrorHandler()

        interpreter.execute(ast, handler)

        assertEquals(listOf("Variable 'x' no definida"), handler.errors)
        assertEquals(emptyList<String>(), out.lines)
    }

    @Test
    fun `asignar a variable no declarada`() {
        val ast = listOf(AssignationNode("x", num(3.0), pos()))
        val (interpreter, out) = newInterpreterWithBuffer()
        val handler = CollectingErrorHandler()

        interpreter.execute(ast, handler)

        assertEquals(listOf("Variable 'x' no definida"), handler.errors)
        assertEquals(emptyList<String>(), out.lines)
    }

    @Test
    fun `type mismatch en declaracion`() {
        val ast = listOf(ConstantDeclarationNode("s", "string", num(10.0), pos()))
        val (interpreter, out) = newInterpreterWithBuffer()
        val handler = CollectingErrorHandler()

        interpreter.execute(ast, handler)

        assertEquals(1, handler.errors.size)
        assertTrue(handler.errors.first().contains("Inicialización incompatible"))
        assertEquals(emptyList<String>(), out.lines)
    }

    @Test
    fun `type mismatch en asignacion`() {
        val ast =
            listOf(
                ConstantDeclarationNode("x", "number", num(1.0), pos()),
                AssignationNode("x", str("hola!"), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        val handler = CollectingErrorHandler()

        interpreter.execute(ast, handler)

        assertEquals(listOf("No se puede reasignar la constante 'x'"), handler.errors)
        assertEquals(emptyList<String>(), out.lines)
    }

    @Test
    fun `readInput sin datos devuelve cadena vacia`() {
        val ctx = IOContext(QueueInputProvider(emptyList()), MapEnvProvider(emptyMap()))
        val ast = listOf(VariableDeclarationNode("name", "string", readInput(str("Prompt")), pos()))
        val (interpreter, _) = newInterpreterWithBuffer(ctx)

        interpreter.execute(ast)

        assertEquals("", interpreter.environmentSnapshot()["name"])
    }

    @Test
    fun `readEnv inexistente devuelve cadena vacia`() {
        val ctx = IOContext(QueueInputProvider(emptyList()), MapEnvProvider(emptyMap()))
        val ast = listOf(VariableDeclarationNode("user", "string", readEnv(str("MISSING")), pos()))
        val (interpreter, _) = newInterpreterWithBuffer(ctx)

        interpreter.execute(ast)

        assertEquals("", interpreter.environmentSnapshot()["user"])
    }
}
