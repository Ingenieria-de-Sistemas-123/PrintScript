package org.printscript.interpreter

import node.*
import org.junit.jupiter.api.Tag
import org.printscript.interpreter.testutil.BufferOutput

@Tag("integration")
abstract class BaseInterpreterIT {
    // Helpers AST (parser)
    protected fun num(n: Double) = LiteralNode(n, "number")
    protected fun str(s: String) = LiteralNode(s, "string")
    protected fun id(name: String) = LiteralNode(name, "identifier")
    protected fun plus(l: ASTNode, r: ASTNode)  = DoubleExpressionNode(l, "+", r)
    protected fun minus(l: ASTNode, r: ASTNode) = DoubleExpressionNode(l, "-", r)
    protected fun star(l: ASTNode, r: ASTNode)  = DoubleExpressionNode(l, "*", r)
    protected fun slash(l: ASTNode, r: ASTNode) = DoubleExpressionNode(l, "/", r)

    protected fun newInterpreterWithBuffer(): Pair<Interpreter, BufferOutput> {
        val out = BufferOutput()
        val it = Interpreter(output = out)
        return it to out
    }
}
