package org.printscript.cli.adapters

import org.printscript.interpreter.Interpreter
import org.printscript.interpreter.io.DefaultOutputProvider
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.io.OutputProvider
import org.printscript.parser.node.ASTNode

class InterpreterAdapter(
    private val interpreterFactory: (
        OutputProvider,
        IOContext,
    ) -> Interpreter = { output, io -> Interpreter(output = output, ioContext = io) },
) {
    fun run(
        ast: List<ASTNode>,
        ioContext: IOContext = IOContext.systemDefault(),
        output: OutputProvider = DefaultOutputProvider(),
    ) {
        val interpreter = interpreterFactory(output, ioContext)
        // Ejecutar el AST en un intérprete que usa el IO provisto (stdin/stdout por defecto)
        interpreter.execute(ast)
    }
}
