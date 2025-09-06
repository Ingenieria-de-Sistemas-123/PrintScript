package org.printscript.interpreter.adapter

import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.interpreter.eval.Executor
import org.printscript.interpreter.runtime.Environment
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DeclarationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintNode
import kotlin.test.assertEquals

class AdapterSmokeTest {
    @Test
    fun `declara, asigna y print`() {
        val pos = Position(1, 1)
        val progAst =
            listOf<ASTNode>(
                DeclarationNode(
                    name = "x",
                    type = "number",
                    value = LiteralNode(1.0, "number", pos),
                    position = pos,
                ),
                AssignationNode(
                    name = "x",
                    type =
                        DoubleExpressionNode(
                            left = LiteralNode("x", "identifier", pos),
                            operator = "+",
                            right = LiteralNode(2.0, "number", pos),
                            position = pos,
                        ),
                    position = pos,
                ),
                PrintNode(
                    expression = LiteralNode("x", "identifier", pos),
                    position = pos,
                ),
            )

        val ir = AstToIr().transform(progAst)

        val out = StringBuilder()
        val exec = Executor(Environment()) { s -> out.appendLine(s) }
        ir.forEach { it.accept(exec) }

        assertEquals("3\n", out.toString())
    }
}
