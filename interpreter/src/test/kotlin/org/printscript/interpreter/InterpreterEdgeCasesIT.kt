package org.printscript.interpreter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.printscript.interpreter.io.OutputProvider
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.util.CollectingErrorHandler
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.ConstantDeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.token.TokenType

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

    @Test
    fun `println de booleano produce error`() {
        val ast = listOf(PrintStatementNode(LiteralNode(true, TokenType.TRUE), pos()))
        val (interpreter, out) = newInterpreterWithBuffer()
        val handler = CollectingErrorHandler()

        interpreter.execute(ast, handler)

        assertEquals(emptyList<String>(), out.lines)
        assertEquals(listOf("println solo acepta valores de tipo number o string"), handler.errors)
    }

    @Test
    fun `println de string vacio imprime linea vacia`() {
        val ast = listOf(PrintStatementNode(str(""), pos()))
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf(""), out.lines)
    }

    @Test
    fun `asignacion a constante falla`() {
        val ast =
            listOf(
                ConstantDeclarationNode("c", "number", num(1.0), pos()),
                AssignationNode("c", num(2.0), pos()),
            )
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex =
            assertThrows<RuntimeError> {
                interpreter.execute(ast)
            }
        assert(ex.message!!.contains("No se puede reasignar la constante"))
    }

    @Test
    fun `operacion invalida entre booleanos`() {
        val ast =
            listOf(
                PrintStatementNode(
                    DoubleExpressionNode(
                        LiteralNode(true, TokenType.TRUE),
                        "+",
                        LiteralNode(false, TokenType.FALSE),
                        pos(),
                    ),
                    pos(),
                ),
            )
        val (interpreter, _) = newInterpreterWithBuffer()
        val ex =
            assertThrows<org.printscript.interpreter.runtime.RuntimeError> {
                interpreter.execute(ast)
            }
        assert(ex.message!!.contains("Operador '+' no definido"))
    }

    @Test
    fun `numeros negativos y grandes`() {
        val ast =
            listOf(
                PrintStatementNode(num(-1000000.0), pos()),
                PrintStatementNode(num(1e10), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()
        interpreter.execute(ast)
        assertEquals(listOf("-1000000", "10000000000"), out.lines)
    }

    @Test
    fun `out of memory se reporta en handler`() {
        val ast = listOf(PrintStatementNode(str("boom"), pos()))
        val failingOutput = OutputProvider { throw OutOfMemoryError("Java heap space") }
        val interpreter = Interpreter(output = failingOutput)
        val handler = CollectingErrorHandler()

        interpreter.execute(ast, handler)

        assertEquals(listOf("Java heap space"), handler.errors)
    }

    @Test
    fun `acepta secuencia perezosa sin materializar todo`() {
        val astSequence =
            sequenceOf(
                PrintStatementNode(str("a"), pos()),
                PrintStatementNode(str("b"), pos()),
                PrintStatementNode(str("c"), pos()),
            )
        val (interpreter, out) = newInterpreterWithBuffer()

        interpreter.execute(astSequence)

        assertEquals(listOf("a", "b", "c"), out.lines)
    }
}
