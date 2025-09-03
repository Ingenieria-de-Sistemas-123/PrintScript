package org.printscript.interpreter

import node.PrintNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("integration")
@DisplayName("Interpreter – Edge Cases")
class InterpreterEdgeCasesIT : BaseInterpreterIT() {

    @Test
    fun `render de enteros sin ,0`() {
        val ast = listOf(
            PrintNode(num(2), pos()),
            PrintNode(num(4), pos())
        )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("2", "4"), out.lines)
    }

    @Test
    fun `precedencia via AST anidado`() {
        // println( (1 + 2) * 3 ) => 9  (forzamos el AST a anidar (1+2) como left)
        val ast = listOf(
            PrintNode(
                star(plus(num(1.0), num(2.0)), num(3.0)),
                pos()
            )
        )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("9"), out.lines)
    }
}
