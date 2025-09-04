package org.printscript.interpreter

import node.AssignationNode
import node.DeclarationNode
import node.PrintNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("integration")
@DisplayName("Interpreter – Happy Path")
class InterpreterHappyPathIT : BaseInterpreterIT() {

    @Test
    fun `declara number con precedencia, imprime, concatena string, reasigna y vuelve a imprimir`() {
        val ast = listOf(
            DeclarationNode("x", "number", plus(num(1.0), star(num(2.0), num(3.0))), pos()), // 7
            PrintNode(id("x"), pos()),
            DeclarationNode("s", "string", str("hola"), pos()),
            PrintNode(plus(id("s"), str("2025")), pos()), // hola2025
            AssignationNode("x", plus(id("x"), num(1.0)), pos()), // 8
            PrintNode(id("x"), pos())
        )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("7","hola2025","8"), out.lines)
    }

    @Test
    fun `aritmetica basica y formateo de enteros sin coma`() {
        val ast = listOf(
            PrintNode(minus(num(3.0), num(1.0)), pos()), // 2
            PrintNode(star(num(2.0), num(5.0)),  pos()), // 10
            PrintNode(slash(num(8.0), num(2.0)), pos()), // 4
            PrintNode(plus(num(2.0), num(0.0)),  pos())  // 2
        )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("2","10","4","2"), out.lines)
    }

    @Test
    fun `concatena string+string`() {
        val ast = listOf(
            DeclarationNode("a", "string", str("ab"), pos()),
            DeclarationNode("b", "string", str("cd"), pos()),
            PrintNode(plus(id("a"), id("b")), pos()) // abcd
        )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("abcd"), out.lines)
    }
}
