package org.printscript.cli.adapters

import org.printscript.interpreter.Interpreter
import org.printscript.interpreter.adapter.AstToIr
import org.printscript.interpreter.io.IOContext
import org.printscript.parser.node.ASTNode

class InterpreterAdapter(
    private val interpreter: Interpreter = Interpreter(),
) {
    fun run(ast: List<ASTNode>) {
        interpreter.execute(ast)
    }

    fun run(
        ast: List<ASTNode>,
        languageVersion: String,
        io: IOContext,
    ) {
        val mapper =
            when (languageVersion.trim()) {
                "1.0" -> AstToIr()
                "1.1" -> AstToIrV11()
                else -> error("Unsupported PrintScript version: $languageVersion (use 1.0 or 1.1)")
            }
        interpreter.execute(ast, io, mapper)
    }
}
