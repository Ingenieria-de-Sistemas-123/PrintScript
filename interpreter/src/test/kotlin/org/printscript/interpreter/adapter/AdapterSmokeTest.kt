package org.printscript.interpreter.adapter

import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.interpreter.eval.Executor
import org.printscript.interpreter.runtime.Environment
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.VariableDeclarationNode
import org.printscript.token.TokenType
import kotlin.test.assertEquals

class AdapterSmokeTest {
    @Test
    fun `declara, asigna y print`() {
        val pos = Position(1, 1)
        val progAst =
            listOf(
                VariableDeclarationNode(
                    identifier = "x",
                    valueType = "number",
                    expression = LiteralNode(1.0, TokenType.NUMBER_TYPE),
                    position = pos,
                ),
                AssignationNode(
                    variable = "x",
                    expression =
                        DoubleExpressionNode(
                            left = LiteralNode("x", TokenType.IDENTIFIER),
                            operator = "+",
                            right = LiteralNode(2.0, TokenType.NUMBER),
                            position = pos,
                        ),
                    position = pos,
                ),
                PrintStatementNode(
                    expression = LiteralNode("x", TokenType.IDENTIFIER),
                    position = pos,
                ),
            )

        val ir = ASTtoIR().transform(progAst)

        val out = StringBuilder()
        val exec = Executor(Environment()) { s -> out.appendLine(s) }
        ir.forEach { it.accept(exec) }

        assertEquals("3\n", out.toString())
    }
}
