package org.printscript.interpreter.adapter

import org.junit.jupiter.api.Test
import org.printscript.common.Position
import org.printscript.interpreter.eval.Executor
import org.printscript.interpreter.ir.BoolLit
import org.printscript.interpreter.ir.IfIR
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.util.TestIO
import org.printscript.parser.node.AssignationNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.IfElseNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.PrintStatementNode
import org.printscript.parser.node.VariableDeclarationNode
import org.printscript.token.TokenType
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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
        // Evitar doble \n: Executor ya agrega uno
        val exec = Executor(Environment(), { out.append(it).append("\n") }, TestIO.empty)
        ir.forEach { it.accept(exec) }

        assertEquals("3\n", out.toString())
    }

    @Test
    fun `if else se transforma a IfIR y ejecuta ramas correctas`() {
        val pos = Position(1, 1)
        val programAst =
            listOf(
                IfElseNode(
                    ifBranch = listOf(PrintStatementNode(LiteralNode("Si", TokenType.STRING), pos)),
                    elseBranch = listOf(PrintStatementNode(LiteralNode("No", TokenType.STRING), pos)),
                    condition = LiteralNode(true, TokenType.TRUE),
                ),
                IfElseNode(
                    ifBranch = listOf(PrintStatementNode(LiteralNode("Nunca", TokenType.STRING), pos)),
                    elseBranch = listOf(PrintStatementNode(LiteralNode("Siempre", TokenType.STRING), pos)),
                    condition = LiteralNode(false, TokenType.FALSE),
                ),
            )

        val ir = ASTtoIR().transform(programAst)

        val firstIf = assertIs<IfIR>(ir[0])
        assertIs<BoolLit>(firstIf.condition)
        val secondIf = assertIs<IfIR>(ir[1])
        assertIs<BoolLit>(secondIf.condition)

        val out = StringBuilder()
        val exec = Executor(Environment(), { out.append(it).append("\n") }, TestIO.empty)
        ir.forEach { it.accept(exec) }

        assertEquals("Si\nSiempre\n", out.toString())
    }

    @Test
    fun `if sin else genera rama nula`() {
        val pos = Position(1, 1)
        val programAst =
            listOf(
                IfElseNode(
                    ifBranch = listOf(PrintStatementNode(LiteralNode("Hola", TokenType.STRING), pos)),
                    elseBranch = emptyList(),
                    condition = LiteralNode(true, TokenType.TRUE),
                ),
            )

        val ir = ASTtoIR().transform(programAst)

        val ifIr = assertIs<IfIR>(ir.single())
        assertNotNull(ifIr.thenBranch)
        assertNull(ifIr.elseBranch)
    }
}
