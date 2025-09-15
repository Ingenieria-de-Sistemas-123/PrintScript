package org.printscript.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.PrintStatementNode

@Tag("integration")
@DisplayName("Interpreter – Happy Path")
class InterpreterHappyPathIT : BaseInterpreterIT() {
    @Test
    fun `declara number con precedencia, imprime, concatena string, reasigna y vuelve a imprimir`() {
        val ast =
            listOf(
                ConstantDeclarationNode("x", "number", plus(num(1.0), star(num(2.0), num(3.0))), pos()),
                PrintStatementNode(id("x"), pos()),
                ConstantDeclarationNode("s", "string", str("hola!"), pos()),                 // usar "hola!" para StrLit
                PrintStatementNode(plus(id("s"), str("2025")), pos()),                       // "2025" ya es StrLit
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
                ConstantDeclarationNode("a", "string", str("ab!"), pos()),  // "ab!" fuerza StrLit
                ConstantDeclarationNode("b", "string", str("cd!"), pos()),
                PrintStatementNode(plus(id("a"), id("b")), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("ab!cd!"), out.lines)
    }
}
