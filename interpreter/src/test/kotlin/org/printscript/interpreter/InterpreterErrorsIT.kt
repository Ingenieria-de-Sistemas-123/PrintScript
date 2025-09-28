package org.printscript.interpreter

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.util.MapEnvProvider
import org.printscript.interpreter.util.QueueInputProvider
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.PrintStatementNode

@Tag("integration")
@DisplayName("Interpreter – Errores")
class InterpreterErrorsIT : BaseInterpreterIT() {
    @Test
    fun `division por cero`() {
        val ast = listOf(PrintStatementNode(slash(num(1.0), num(0.0)), pos()))
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex = assertThrows(RuntimeError::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("División por cero"))
    }

    @Test
    fun `identificador no declarado`() {
        val ast = listOf(PrintStatementNode(id("x"), pos()))
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex = assertThrows(RuntimeError::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("Variable 'x' no definida"))
    }

    @Test
    fun `asignar a variable no declarada`() {
        val ast = listOf(AssignationNode("x", num(3.0), pos()))
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex = assertThrows(RuntimeError::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("Variable 'x' no definida"))
    }

    @Test
    fun `type mismatch en declaracion`() {
        val ast = listOf(ConstantDeclarationNode("s", "string", num(10.0), pos()))
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex = assertThrows(IllegalArgumentException::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("Inicialización incompatible"))
    }

    @Test
    fun `type mismatch en asignacion`() {
        val ast =
            listOf(
                ConstantDeclarationNode("x", "number", num(1.0), pos()),
                AssignationNode("x", str("hola!"), pos()),
            )
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex = assertThrows(RuntimeError::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("No se puede reasignar la constante"))
    }

    @Test
    fun `readInput sin datos lanza error`() {
        val ctx = IOContext(QueueInputProvider(emptyList()), MapEnvProvider(emptyMap()))
        val ast = listOf(PrintStatementNode(readInput(str("Prompt")), pos()))
        val (interpreter, _) = newInterpreterWithBuffer(ctx)
        val ex = assertThrows(RuntimeError::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("readInput"))
    }

    @Test
    fun `readEnv inexistente lanza error`() {
        val ctx = IOContext(QueueInputProvider(emptyList()), MapEnvProvider(emptyMap()))
        val ast = listOf(PrintStatementNode(readEnv(str("MISSING")), pos()))
        val (interpreter, _) = newInterpreterWithBuffer(ctx)
        val ex = assertThrows(RuntimeError::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("MISSING"))
    }
}
