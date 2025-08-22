package org.printscript.interpreter.eval

import org.printscript.interpreter.runtime.Value
import org.printscript.interpreter.runtime.Value.Num
import org.printscript.interpreter.runtime.Value.Str
import org.printscript.interpreter.runtime.asString
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.ir.*

// usamos visitor, evalua expresiones a valores en runtime
internal class Evaluator(private val env: Environment) : ExprVisitor<Value> {

    override fun visitNum(n: NumLit): Value = Num(n.value)
    override fun visitStr(s: StrLit): Value = Str(s.value)
    override fun visitId(i: IdRef): Value = env.read(i.name)

    override fun visitBinary(b: Binary): Value {
        val l = b.left.accept(this)
        val r = b.right.accept(this)
        return when (b.op) {
            Op.PLUS  -> if (l is Str || r is Str) Str(l.asString() + r.asString())
            else Num(reqNum(l, "+") + reqNum(r, "+"))
            Op.MINUS -> Num(reqNum(l, "-") - reqNum(r, "-"))
            Op.STAR  -> Num(reqNum(l, "*") * reqNum(r, "*"))
            Op.SLASH -> {
                val rv = reqNum(r, "/")
                if (rv == 0.0) throw RuntimeError("División por cero")
                Num(reqNum(l, "/") / rv)
            }
        }
    }

    private fun reqNum(v: Value, ctx: String): Double =
        (v as? Num)?.v ?: throw RuntimeError("Operador '$ctx' requiere números")
}
