package org.printscript.interpreter

import org.junit.jupiter.api.Tag
import org.printscript.common.Position
import org.printscript.interpreter.util.BufferOutput
import org.printscript.parser.node.ASTNode
import org.printscript.parser.node.DoubleExpressionNode
import org.printscript.parser.node.LiteralNode

@Tag("integration")
abstract class BaseInterpreterIT {

    protected fun pos() = Position(1, 1)

    protected fun num(n: Double) = LiteralNode(n)
    protected fun num(n: Int) = LiteralNode(n.toDouble())
    protected fun str(s: String) = LiteralNode(s)
    protected fun id(name: String) = LiteralNode(name)

    protected fun plus(l: ASTNode, r: ASTNode) = DoubleExpressionNode(l, "+", r, pos())
    protected fun minus(l: ASTNode, r: ASTNode) = DoubleExpressionNode(l, "-", r, pos())
    protected fun star(l: ASTNode, r: ASTNode) = DoubleExpressionNode(l, "*", r, pos())
    protected fun slash(l: ASTNode, r: ASTNode) = DoubleExpressionNode(l, "/", r, pos())

    protected fun newInterpreterWithBuffer(): Pair<Interpreter, BufferOutput> {
        val out = BufferOutput()
        val it = Interpreter(output = out)
        return it to out
    }
}
