package org.printscript.interpreter.runtime

sealed interface Value {
    data class Num(val v: Double) : Value

    data class Str(val v: String) : Value

    data class Bool(val v: Boolean) : Value
}

enum class RType { NUMBER, STRING, BOOLEAN }

internal fun Value.asString(): String =
    when (this) {
        is Value.Num -> if (v % 1.0 == 0.0) v.toLong().toString() else v.toString()
        is Value.Str -> v
        is Value.Bool -> v.toString()
    }

internal fun runtimeTypeOf(v: Value): RType =
    when (v) {
        is Value.Num -> RType.NUMBER
        is Value.Str -> RType.STRING
        is Value.Bool -> RType.BOOLEAN
    }
