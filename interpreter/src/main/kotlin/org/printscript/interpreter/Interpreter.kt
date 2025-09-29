package org.printscript.interpreter

import org.printscript.interpreter.adapter.ASTtoIR
import org.printscript.interpreter.eval.Executor
import org.printscript.interpreter.io.DefaultOutputProvider
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.io.OutputProvider
import org.printscript.interpreter.runtime.Environment
import org.printscript.parser.node.ASTNode

class Interpreter(
    output: OutputProvider = DefaultOutputProvider(),
    ioContext: IOContext = IOContext.systemDefault(),
) {
    private val env = Environment()
    private val exec = Executor(env, output, ioContext)
    private val adapter = ASTtoIR()

    fun execute(
        ast: Iterable<ASTNode>,
        errorHandler: ErrorHandler? = null,
    ) {
        try {
            for (node in ast) {
                val stmt = adapter.toStmt(node)
                stmt.accept(exec)
            }
        } catch (oom: OutOfMemoryError) {
            val handler = errorHandler ?: throw oom
            handler.reportError(oom.message ?: "Java heap space")
        } catch (ex: Exception) {
            val handler = errorHandler ?: throw ex
            handler.reportError(ex.message ?: ex.toString())
        }
    }

    fun execute(
        ast: Sequence<ASTNode>,
        errorHandler: ErrorHandler? = null,
    ) = execute(ast.asIterable(), errorHandler)

    fun environmentSnapshot(): Map<String, String> = env.snapshot()
}
