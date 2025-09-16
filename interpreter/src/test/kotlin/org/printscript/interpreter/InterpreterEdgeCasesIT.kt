package org.printscript.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.printscript.parser.node.PrintStatementNode

@Tag("integration")
@DisplayName("Interpreter – Edge Cases")
class InterpreterEdgeCasesIT : BaseInterpreterIT() {
    @Test
    fun `render de enteros sin ,0`() {
        val ast =
            listOf(
                PrintStatementNode(num(2.0), pos()),
                PrintStatementNode(plus(num(2.0), num(2.0)), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("2", "4"), out.lines)
    }

    @Test
    fun `precedencia via AST anidado`() {
        val ast =
            listOf(
                PrintStatementNode(
                    star(plus(num(1.0), num(2.0)), num(3.0)),
                    pos(),
                ),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("9"), out.lines)
    }
}
