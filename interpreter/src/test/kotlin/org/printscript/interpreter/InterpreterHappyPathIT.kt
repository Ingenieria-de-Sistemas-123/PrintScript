package org.printscript.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.PrintNode
import kotlin.text.lines

@Tag("integration")
@DisplayName("Interpreter – Happy Path")
class InterpreterHappyPathIT : BaseInterpreterIT() {
    @Test
    fun `declara number con precedencia, imprime, concatena string, reasigna y vuelve a imprimir`() {
        val ast =
            listOf(
                DeclarationNode("x", "number", plus(num(1.0), star(num(2.0), num(3.0))), pos()),
                PrintNode(id("x"), pos()),
                DeclarationNode("s", "string", str("hola"), pos()),
                PrintNode(plus(id("s"), str("2025")), pos()),
                AssignationNode("x", plus(id("x"), num(1.0)), pos()),
                PrintNode(id("x"), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("7", "hola2025", "8"), out.lines)
    }

    @Test
    fun `aritmetica basica y formateo de enteros sin coma`() {
        val ast =
            listOf(
                PrintNode(minus(num(3.0), num(1.0)), pos()),
                PrintNode(star(num(2.0), num(5.0)), pos()),
                PrintNode(slash(num(8.0), num(2.0)), pos()),
                PrintNode(plus(num(2.0), num(0.0)), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("2", "10", "4", "2"), out.lines)
    }

    @Test
    fun `concatena string+string`() {
        val ast =
            listOf(
                DeclarationNode("a", "string", str("ab"), pos()),
                DeclarationNode("b", "string", str("cd"), pos()),
                PrintNode(plus(id("a"), id("b")), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("abcd"), out.lines)
    }

    @Test
    fun `concatena string+number`() {
        val ast =
            listOf(
                DeclarationNode("s", "string", str("hola"), pos()),
                PrintNode(plus(id("s"), num(2025.0)), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("hola2025"), out.lines)
    }

}
