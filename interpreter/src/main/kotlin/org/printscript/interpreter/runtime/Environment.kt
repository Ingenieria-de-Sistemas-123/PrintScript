package org.printscript.interpreter.runtime

class Environment {

    private data class Var(
        val type: RType,
        var value: Value?,
        var initialized: Boolean
    )

    private val vars = mutableMapOf<String, Var>()

    fun declare(name: String, type: RType, init: Value?) {
        if (name in vars) throw RuntimeError("Variable '$name' ya definida")
        vars[name] = Var(type, init, init != null)
    }

    fun assign(name: String, value: Value) {
        val v = vars[name] ?: throw RuntimeError("Variable '$name' no definida")
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

    // Lo usamos mas adelante. Util a futuro para tests/CLI: nos devuelve una vista stringificada del ambiente
    fun snapshot(): Map<String, String> = vars.mapValues { (_, v) ->
        when (val value = v.value) {
            null -> "<uninitialized>"
            is Value.Num -> if (value.v % 1.0 == 0.0) value.v.toLong().toString() else value.v.toString()
            is Value.Str -> value.v
        }
    }
}
