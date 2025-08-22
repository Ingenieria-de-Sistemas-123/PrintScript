package org.printscript.interpreter

import node.ASTNode
import org.printscript.interpreter.eval.Executor
import org.printscript.interpreter.io.DefaultOutputProvider
import org.printscript.interpreter.io.OutputProvider
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.transform.AstToIr

/**
 * - Recibe el AST del parser
 * - Lo transforma a IR (interna del intérprete) intermediate representation
 * - ejecuta las sentencias con visitors (Executor/Evaluator)
 */
class Interpreter(
    private val output: OutputProvider = DefaultOutputProvider()
) {
    private val env = Environment()
    private val exec = Executor(env, output)
    private val adapter = AstToIr()

    // Entrada publica mas tipica: AST del parser--> ejecuta
    fun execute(ast: List<ASTNode>) {
        val irProgram = adapter.transform(ast)    // List<StmtIR>
        irProgram.forEach { stmt -> stmt.accept(exec) }
    }

    /** a futuro: si queremos exponer un snapshot del entorno para tests/CLI */
    fun environmentSnapshot(): Map<String, String> = env.snapshot()
}
