package org.printscript.interpreter

import node.ASTNode
import node.DoubleExpressionNode
import node.LiteralNode
import org.junit.jupiter.api.Tag
import org.printscript.interpreter.testutil.BufferOutput

@Tag("integration")
abstract class BaseInterpreterIT {
    // Helpers AST (parser)
    protected fun num(n: Double) = LiteralNode(n, "number", n.toString())
    protected fun num(n: Int) = LiteralNode(n.toDouble(), "number", n.toString())
    protected fun str(s: String) = LiteralNode(s, "string", s)
    protected fun id(name: String) = LiteralNode(name, "identifier", name)
    protected fun plus(l: ASTNode, r: ASTNode)  = DoubleExpressionNode(l, "+", r, "$l + $r")
    protected fun minus(l: ASTNode, r: ASTNode) = DoubleExpressionNode(l, "-", r, "$l - $r")
    protected fun star(l: ASTNode, r: ASTNode)  = DoubleExpressionNode(l, "*", r, "$l * $r")
    protected fun slash(l: ASTNode, r: ASTNode) = DoubleExpressionNode(l, "/", r, "$l / $r")

    protected fun newInterpreterWithBuffer(): Pair<Interpreter, BufferOutput> {
        val out = BufferOutput()
        val it = Interpreter(output = out)
        return it to out
    }
}