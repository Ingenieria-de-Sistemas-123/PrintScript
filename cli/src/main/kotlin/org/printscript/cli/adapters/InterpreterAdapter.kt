package org.printscript.cli.adapters

import org.printscript.interpreter.Interpreter
import org.printscript.parser.node.ASTNode

class InterpreterAdapter(
    private val interpreter: Interpreter = Interpreter(),
) {
    fun run(ast: List<ASTNode>) {
        interpreter.execute(ast)
    }
}
