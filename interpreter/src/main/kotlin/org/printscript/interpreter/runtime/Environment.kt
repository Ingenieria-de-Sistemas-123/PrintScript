package org.printscript.interpreter.runtime

class Environment {
    private data class Var(
        val type: RType,
        var value: Value?,
        var initialized: Boolean,
        val isConst: Boolean,
    )

    private val vars = mutableMapOf<String, Var>()

    fun declare(
        name: String,
        type: RType,
        init: Value?,
    ) {
        if (name in vars) throw RuntimeError("Variable '$name' ya definida")
        if (init != null && type != runtimeTypeOf(init)) {
            throw RuntimeError("Inicialización incompatible de '$name': se esperaba $type y se recibió ${runtimeTypeOf(init)}")
        }
        vars[name] = Var(type, init, init != null, isConst = false)
    }

    fun declareConst(
        name: String,
        type: RType,
        init: Value,
    ) {
        if (name in vars) throw RuntimeError("Variable '$name' ya definida")
        if (type != runtimeTypeOf(init)) {
            throw RuntimeError("Inicialización incompatible de const '$name': se esperaba $type y se recibió ${runtimeTypeOf(init)}")
        }
        vars[name] = Var(type, init, initialized = true, isConst = true)
    }

    fun assign(
        name: String,
        value: Value,
    ) {
        val v = vars[name] ?: throw RuntimeError("Variable '$name' no definida")
        if (v.isConst) {
            throw RuntimeError("No se puede asignar a la constante '$name'")
        }
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
