package org.printscript.interpreter

import org.junit.jupiter.api.Tag
import org.printscript.common.Position
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.util.BufferOutput
import org.printscript.interpreter.util.TestIO
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.EmptyExpressionNode
import org.printscript.parser.node.LiteralNode
import org.printscript.parser.node.ReadEnvNode
import org.printscript.parser.node.ReadInputNode
import org.printscript.token.TokenType

@Tag("integration")
abstract class BaseInterpreterIT {
    protected fun pos() = Position(1, 1)

    protected fun num(n: Double) = LiteralNode(n, TokenType.NUMBER)

    protected fun num(n: Int) = LiteralNode(n.toDouble(), TokenType.NUMBER)

    protected fun str(s: String) = LiteralNode(s, TokenType.STRING)

    protected fun id(name: String) = LiteralNode(name, TokenType.IDENTIFIER)

    protected fun boolTrue() = LiteralNode(true, TokenType.TRUE)

    protected fun boolFalse() = LiteralNode(false, TokenType.FALSE)

    protected fun plus(
        l: ASTNode,
        r: ASTNode,
    ) = DoubleExpressionNode(l, "+", r, pos())

    protected fun minus(
        l: ASTNode,
        r: ASTNode,
    ) = DoubleExpressionNode(l, "-", r, pos())

    protected fun star(
        l: ASTNode,
        r: ASTNode,
    ) = DoubleExpressionNode(l, "*", r, pos())

    protected fun slash(
        l: ASTNode,
        r: ASTNode,
    ) = DoubleExpressionNode(l, "/", r, pos())

    protected fun emptyExpr(): ASTNode = EmptyExpressionNode

    protected fun readInput(arg: ASTNode) = ReadInputNode(arg, pos())

    protected fun readEnv(arg: ASTNode) = ReadEnvNode(arg, pos())

    protected fun newInterpreterWithBuffer(ioContext: IOContext = TestIO.empty): Pair<Interpreter, BufferOutput> {
        val out = BufferOutput()
        val it = Interpreter(output = out, ioContext = ioContext)
        return it to out
    }
}
