package org.printscript.interpreter.eval

import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.io.OutputProvider
import org.printscript.interpreter.ir.AssignIR
import org.printscript.interpreter.ir.ConstDeclIR
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.IfIR
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.StmtVisitor
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.runtime.Value
import org.printscript.interpreter.runtime.asString
import org.printscript.interpreter.runtime.runtimeTypeOf

// ejecuta sentencias del IR usando patron visitor
internal class Executor(
    private val env: Environment,
    private val output: OutputProvider,
    private val io: IOContext,
) : StmtVisitor<Unit> {
    private val eval = Evaluator(env, output, io)

    override fun visitDecl(d: DeclIR) {
        val init = d.initializer?.accept(eval)
        if (init != null) {
            val actualType = runtimeTypeOf(init)
            require(actualType == d.declaredType) {
                "Inicialización incompatible de '${d.name}': se esperaba ${d.declaredType} y se recibió $actualType"
            }
        }
        env.declare(d.name, d.declaredType, init)
    }

    override fun visitAssign(a: AssignIR) {
        val v = a.expr.accept(eval)
        env.assign(a.name, v)
    }

    override fun visitPrint(p: PrintIR) {
        when (val v = p.expr.accept(eval)) {
            is Value.Num, is Value.Str -> output.println(v.asString())
            else -> throw RuntimeError("println solo acepta valores de tipo number o string")
        }
    }

    override fun visitIf(i: IfIR) {
        val condValue = i.condition.accept(eval)
        require(condValue is Value.Bool) { "La condición de if debe ser booleana" }
        if (condValue.v) {
            i.thenBranch.forEach { it.accept(this) }
        } else {
            i.elseBranch?.forEach { it.accept(this) }
        }
    }

    override fun visitConstDecl(c: ConstDeclIR) {
        val init = c.initializer.accept(eval)
        val actualType = runtimeTypeOf(init)
        require(actualType == c.declaredType) {
            "Inicialización incompatible de constante '${c.name}': se esperaba ${c.declaredType} y se recibió $actualType"
        }
        env.declareConst(c.name, c.declaredType, init)
    }
}
