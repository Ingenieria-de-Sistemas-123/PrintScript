package org.printscript.interpreter

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.PrintNode

@Tag("integration")
@DisplayName("Interpreter – Errores")
class InterpreterErrorsIT : BaseInterpreterIT() {
    @Test
    fun `string + number concatena correctamente`() {
        val ast =
            listOf(
                DeclarationNode("s", "string", str("hola"), pos()),
                PrintNode(plus(id("s"), num(2025.0)), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertTrue { out.lines == listOf("hola2025") }
    }

    @Test
    fun `division por cero`() {
        val ast = listOf(PrintNode(slash(num(1.0), num(0.0)), pos()))
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex = assertThrows(RuntimeError::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("División por cero"))
    }

    @Test
    fun `identificador no declarado`() {
        val ast = listOf(PrintNode(id("x"), pos()))
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
        val ast = listOf(DeclarationNode("s", "string", num(10.0), pos()))
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex = assertThrows(IllegalArgumentException::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("Inicialización incompatible"))
    }

    @Test
    fun `type mismatch en asignacion`() {
        val ast =
            listOf(
                DeclarationNode("x", "number", num(1.0), pos()),
                AssignationNode("x", str("hola"), pos()),
            )
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex = assertThrows(RuntimeError::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("Asignación incompatible"))
    }

    @Test
    fun `declaracion doble de variable`() {
        val ast =
            listOf(
                DeclarationNode("x", "number", num(1.0), pos()),
                DeclarationNode("x", "number", num(2.0), pos()),
            )
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex = assertThrows(RuntimeError::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("Variable 'x' ya definida"))
    }

    @Test
    fun `declaracion con tipo invalido - falla`() {
        val ast = listOf(DeclarationNode("x", "boolean", num(1.0), pos()))
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex = assertThrows(IllegalStateException::class.java) { interpreter.execute(ast) }
        assertTrue(ex.message!!.contains("Tipo de declaración desconocido 'boolean'"))
    }
}
