package org.printscript.interpreter.eval

import org.printscript.interpreter.io.OutputProvider
import org.printscript.interpreter.ir.AssignIR
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.StmtVisitor
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.asString
import org.printscript.interpreter.runtime.runtimeTypeOf

internal class Executor(
    private val env: Environment,
    private val output: OutputProvider,
) : StmtVisitor<Unit> {
    private val eval = Evaluator(env)

    override fun visitDecl(d: DeclIR) {
        val init = d.initializer.accept(eval)
        val actualType = runtimeTypeOf(init)
        require(actualType == d.declaredType) {
            "Inicialización incompatible de '${d.name}': se esperaba ${d.declaredType} y se recibió $actualType"
        }
        env.declare(d.name, d.declaredType, init)
    }

    override fun visitAssign(a: AssignIR) {
        val v = a.expr.accept(eval)
        env.assign(a.name, v)
    }

    override fun visitPrint(p: PrintIR) {
        val v = p.expr.accept(eval)
        output.println(v.asString())
    }
}
