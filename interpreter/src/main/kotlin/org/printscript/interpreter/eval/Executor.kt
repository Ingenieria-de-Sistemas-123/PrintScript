package org.printscript.interpreter.eval

import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.io.OutputProvider
import org.printscript.interpreter.ir.AssignIR
import org.printscript.interpreter.ir.ConstDeclIR
import org.printscript.interpreter.ir.DeclIR
import org.printscript.interpreter.ir.IfIR
import org.printscript.interpreter.ir.PrintIR
import org.printscript.interpreter.ir.StmtIR
import org.printscript.interpreter.ir.StmtVisitor
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.runtime.Value
import org.printscript.interpreter.runtime.asString
import org.printscript.interpreter.runtime.runtimeTypeOf

// visitor que ejecuta sentencias usando el Evaluator para expressions
internal class Executor(
    private val env: Environment,
    private val output: OutputProvider,
    private val io: IOContext,
) : StmtVisitor<Unit> {
    private val eval = Evaluator(env, io)

    fun exec(block: List<StmtIR>) {
        block.forEach { it.accept(this) }
    }

    // v1.0
    override fun visitDecl(d: DeclIR) {
        val init = d.initializer.accept(eval)
        val actualType = runtimeTypeOf(init)
        require(actualType == d.declaredType) {
            "Inicialización incompatible de '${d.name}': se esperaba ${d.declaredType} y se recibió $actualType"
        }
        env.declare(d.name, d.declaredType, init)
    }

    // v1.1
    override fun visitConstDecl(c: ConstDeclIR) {
        val init = c.initializer.accept(eval)
        val actualType = runtimeTypeOf(init)
        require(actualType == c.declaredType) {
            "Inicialización incompatible de const '${c.name}': se esperaba ${c.declaredType} y se recibió $actualType"
        }
        env.declareConst(c.name, c.declaredType, init)
    }

    override fun visitAssign(a: AssignIR) {
        val v = a.expr.accept(eval)
        env.assign(a.name, v)
    }

    override fun visitPrint(p: PrintIR) {
        val v = p.expr.accept(eval)
        output.println(v.asString())
    }

    // v1.1
    override fun visitIf(i: IfIR) {
        val cond = env.read(i.conditionVar)
        val isTrue =
            (cond as? Value.Bool)?.v
                ?: throw RuntimeError("La condición de if debe ser una variable boolean: '${i.conditionVar}'")
        if (isTrue) {
            exec(i.thenBlock)
        } else {
            i.elseBlock?.let { exec(it) }
        }
    }
}
