package org.printscript.interpreter.runtime

class Environment {
    private data class Var(
        val type: RType,
        var value: Value?,
        var initialized: Boolean,
        val isConst: Boolean = false,
    )

    private val vars = mutableMapOf<String, Var>()

    fun declare(
        name: String,
        type: RType,
        init: Value?,
    ) {
        if (name in vars) throw RuntimeError("Variable '$name' ya definida")
        vars[name] = Var(type, init, init != null)
    }

    fun declareConst(
        name: String,
        type: RType,
        init: Value,
    ) {
        if (name in vars) throw RuntimeError("Constante '$name' ya definida")
        vars[name] = Var(type, init, true, isConst = true)
    }

    fun assign(
        name: String,
        value: Value,
    ) {
        val v = vars[name] ?: throw RuntimeError("Variable '$name' no definida")
        if (v.isConst) throw RuntimeError("No se puede reasignar la constante '$name'")
        if (v.type != runtimeTypeOf(value)) {
            throw RuntimeError("Asignación incompatible a '$name': se esperaba ${v.type} y se recibió ${runtimeTypeOf(value)}")
        }
        v.value = value
        v.initialized = true
    }

    fun read(name: String): Value {
        val v = vars[name] ?: throw RuntimeError("Variable '$name' no definida")
        if (!v.initialized) throw RuntimeError("Variable '$name' usada antes de inicializar")
        return v.value!!
    }

    // this snapshot is used for testing and debugging purposes. The way it works is that it returns a map of variable names to their string representation of their values. If a variable is uninitialized, it will show "<uninitialized>"
    fun snapshot(): Map<String, String> =
        vars.mapValues { (_, v) ->
            when (val value = v.value) {
                null -> "<uninitialized>"
                is Value.Num -> if (value.v % 1.0 == 0.0) value.v.toLong().toString() else value.v.toString()
                is Value.Str -> value.v
                is Value.Bool -> value.v.toString()
            }
        }
}
