package org.printscript.interpreter.eval

import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.ir.Binary
import org.printscript.interpreter.ir.BoolLit
import org.printscript.interpreter.ir.ExprVisitor
import org.printscript.interpreter.ir.IdRef
import org.printscript.interpreter.ir.NumLit
import org.printscript.interpreter.ir.Op
import org.printscript.interpreter.ir.ReadEnv
import org.printscript.interpreter.ir.ReadInput
import org.printscript.interpreter.ir.StrLit
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.runtime.Value
import org.printscript.interpreter.runtime.Value.Num
import org.printscript.interpreter.runtime.Value.Str
import org.printscript.interpreter.runtime.asString

internal class Evaluator(
    private val env: Environment,
    private val io: IOContext,
) : ExprVisitor<Value> {
    override fun visitNum(n: NumLit): Value = Num(n.value)

    override fun visitStr(s: StrLit): Value = Str(s.value)

    override fun visitId(i: IdRef): Value = env.read(i.name)

    override fun visitBool(b: BoolLit): Value = Value.Bool(b.value)

    override fun visitReadInput(r: ReadInput): Value {
        val prompt = requireString(r.prompt.accept(this), "readInput")
        val line = io.input.readLine(prompt) ?: throw RuntimeError("Fin de la entrada: readInput no recibió más datos")
        return Str(line)
    }

    override fun visitReadEnv(r: ReadEnv): Value {
        val key = requireString(r.key.accept(this), "readEnv")
        val value =
            io.env.get(key)
                ?: throw RuntimeError("Variable de entorno '$key' no encontrada")
        return Str(value)
    }

    override fun visitBinary(b: Binary): Value {
        val l = b.left.accept(this)
        val r = b.right.accept(this)
        return when (b.op) {
            Op.PLUS ->
                when {
                    l is Num && r is Num -> Num(l.v + r.v)
                    l is Str || r is Str -> Str(l.asString() + r.asString())
                    else -> throw RuntimeError(
                        "Operador '+' no definido para ${typeName(l)} y ${typeName(r)}",
                    )
                }
            Op.MINUS -> Num(reqNum(l, "-") - reqNum(r, "-"))
            Op.STAR -> Num(reqNum(l, "*") * reqNum(r, "*"))
            Op.SLASH -> {
                val rv = reqNum(r, "/")
                if (rv == 0.0) throw RuntimeError("División por cero")
                Num(reqNum(l, "/") / rv)
            }
        }
    }

    private fun reqNum(
        v: Value,
        ctx: String,
    ): Double = (v as? Num)?.v ?: throw RuntimeError("Operador '$ctx' requiere números")

    private fun typeName(v: Value) =
        when (v) {
            is Num -> "number"
            is Str -> "string"
            is Value.Bool -> "boolean"
        }

    private fun requireString(
        value: Value,
        ctx: String,
    ): String = (value as? Str)?.v ?: throw RuntimeError("$ctx requiere un argumento de tipo string")
}
