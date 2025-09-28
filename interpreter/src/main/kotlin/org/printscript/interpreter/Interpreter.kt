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

    fun execute(ast: List<ASTNode>) {
        val irProgram = adapter.transform(ast) // List<StmtIR>
        irProgram.forEach { stmt -> stmt.accept(exec) }
    }

    fun environmentSnapshot(): Map<String, String> = env.snapshot()
}
