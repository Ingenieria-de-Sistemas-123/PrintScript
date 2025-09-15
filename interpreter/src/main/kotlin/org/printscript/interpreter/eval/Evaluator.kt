package org.printscript.interpreter.eval

import org.printscript.interpreter.io.IOContext
import org.printscript.interpreter.ir.Binary
import org.printscript.interpreter.ir.BoolLit
import org.printscript.interpreter.ir.ExprVisitor
import org.printscript.interpreter.ir.IdRef
import org.printscript.interpreter.ir.NumLit
import org.printscript.interpreter.ir.Op
import org.printscript.interpreter.ir.ReadEnvIR
import org.printscript.interpreter.ir.ReadInputIR
import org.printscript.interpreter.ir.StrLit
import org.printscript.interpreter.runtime.Environment
import org.printscript.interpreter.runtime.RType
import org.printscript.interpreter.runtime.RuntimeError
import org.printscript.interpreter.runtime.Value
import org.printscript.interpreter.runtime.Value.Bool
import org.printscript.interpreter.runtime.Value.Num
import org.printscript.interpreter.runtime.Value.Str

internal class Evaluator(
    private val env: Environment,
    private val io: IOContext,
) : ExprVisitor<Value> {
    override fun visitNum(n: NumLit): Value = Num(n.value)

    override fun visitStr(s: StrLit): Value = Str(s.value)

    override fun visitId(i: IdRef): Value = env.read(i.name)

    // v1.1
    override fun visitBool(b: BoolLit): Value = Bool(b.value)

    private fun numToString(n: Double): String =
        if (n % 1.0 == 0.0) n.toInt().toString() else n.toString()

    override fun visitBinary(b: Binary): Value {
        val l = b.left.accept(this)
        val r = b.right.accept(this)
        return when (b.op) {
            Op.PLUS ->
                when {
                    l is Num && r is Num -> Num(l.v + r.v)
                    l is Str && r is Str -> Str(l.v + r.v)
                    l is Str && r is Num -> Str(l.v + numToString(r.v))
                    l is Num && r is Str -> Str(numToString(l.v) + r.v)
                    else -> throw RuntimeError(
                    "Operador '+' no definido para ${typeName(l)} y ${typeName(r)}"
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


    // v1.1: readInput
    override fun visitReadInput(r: ReadInputIR): Value {
        val raw =
            io.input.readLine(r.prompt)
                ?: throw RuntimeError("No se recibió input para el prompt '${r.prompt}'")
        return when (r.expected ?: RType.STRING) {
            RType.BOOLEAN ->
                Bool(
                    parseBoolean(raw)
                        ?: throw RuntimeError("Se esperaba boolean, se recibió '$raw'"),
                )
            RType.NUMBER ->
                Num(
                    parseNumber(raw)
                        ?: throw RuntimeError("Se esperaba number, se recibió '$raw'"),
                )
            RType.STRING -> Str(raw)
        }
    }

    // v1.1: readEnv
    override fun visitReadEnv(r: ReadEnvIR): Value {
        val raw =
            io.env.get(r.name)
                ?: throw RuntimeError("Variable de entorno '${r.name}' no definida")
        return when (r.expected ?: RType.STRING) {
            RType.BOOLEAN ->
                Bool(
                    parseBoolean(raw)
                        ?: throw RuntimeError("Se esperaba boolean en '${r.name}', se recibió '$raw'"),
                )
            RType.NUMBER ->
                Num(
                    parseNumber(raw)
                        ?: throw RuntimeError("Se esperaba number en '${r.name}', se recibió '$raw'"),
                )
            RType.STRING -> Str(raw)
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
            is Bool -> "boolean"
        }

    private fun parseBoolean(s: String): Boolean? =
        when (s.trim().lowercase()) {
            "true" -> true
            "false" -> false
            else -> null
        }

    private fun parseNumber(s: String): Double? = s.trim().toDoubleOrNull()
}
