package org.printscript.interpreter

import org.printscript.interpreter.adapter.AstToIr
import org.printscript.interpreter.adapter.AstToIrMapper
import org.printscript.interpreter.eval.Executor
import org.printscript.interpreter.io.DefaultOutputProvider
import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.io.OutputProvider
import org.printscript.interpreter.io.StdinInputProvider
import org.printscript.interpreter.io.SystemEnvProvider
import org.printscript.interpreter.runtime.Environment
import org.printscript.parser.node.ASTNode

class Interpreter(
    private val output: OutputProvider = DefaultOutputProvider(),
) {
    private val env = Environment()
    private val defaultMapper: AstToIrMapper = AstToIr()

    fun execute(ast: List<ASTNode>) {
        val defaultIo =
            IOContext(
                input = StdinInputProvider(),
                env = SystemEnvProvider(),
            )
        execute(ast, defaultIo, defaultMapper)
    }

    fun execute(
        ast: List<ASTNode>,
        io: IOContext,
    ) {
        execute(ast, io, defaultMapper)
    }

    fun execute(
        ast: List<ASTNode>,
        io: IOContext,
        mapper: AstToIrMapper,
    ) {
        val irProgram = mapper.transform(ast) // List<StmtIR>
        val exec = Executor(env, output, io)
        exec.exec(irProgram)
    }

    fun environmentSnapshot(): Map<String, String> = env.snapshot()
}
